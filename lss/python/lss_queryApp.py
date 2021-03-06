#!/usr/bin/env python
#coding=utf-8

import urllib
import hashlib
import hmac
import logging
import datetime
import requests
import json

IAM_NOHEADERS = 400
IAM_HEADERS_NOHOST = 401
IAM_HEADERS_NOSIGNED = 402
IAM_NOMETHOD = 420
IAM_NOURI = 440
IAM_SIGNINGKEY = 460
IAM_SIGNATURE = 480

errmessage = {
    400: '(IAM) missing required headers',
    401: '(IAM) headers lack host',
    402: '(IAM) header to singed no exist',
    420: '(IAM) missing required method',
    440: '(IAM) uri is empty',
    460: '(IAM) signing key error',
    480: '(IAM) signature error',
}


class ErrorInfo(object):
    """Error Information.
    """
    def __init__(self, code, info=None):
        self._code = code
        self._message = errmessage.get(code, '')
        self._info = info

    def __str__(self):
        return self.tostring()

    def tostring(self):
        """convert to string
        Returns:
           return the error string
        """
        info = '%s [Errno %d]' % (self._message, self._code)
        if self._info:
            info += " %s" % self._info
        return info


def serialize_authorization(auth):
    """
    serialize Authorization object to authorization string
    """
    val = "/".join((auth['version'], auth['access'], auth['timestamp'], auth['period'],
            ";".join(auth['signedheaders']), auth['signature']))
    return BceSigner.get_utf8_value(val)


def build_authorization(accesskey, signedheaders, period=1800, timestamp=None):
    """
    build Authorization object
    """
    auth = {}
    auth['version'] = "bce-auth-v1"
    auth['access'] = accesskey
    if not timestamp:
        auth['timestamp'] = datetime.datetime.utcnow().strftime('%Y-%m-%dT%H:%M:%SZ')
    else:
        auth['timestamp'] = timestamp
    auth['period'] = str(period)
    auth['signedheaders'] = signedheaders
    return auth


def is_utc_timestamp(timestamp):
    """
    check if timestamp is with utc format
    """
    try:
        datetime.datetime.strptime(timestamp, '%Y-%m-%dT%H:%M:%SZ')
        return True
    except ValueError:
        return False


def is_integer(value):
    """
    check if value is an interger
    """
    try:
        v = int(value)
        return True
    except ValueError:
        return False


class BceSigner(object):
    """
    Utility class which adds allows a request to be signed with an BCE signature
    """

    def __init__(self, access_key, secret_key, logger=None):
        self.access_key = access_key.encode()
        self.secret_key = secret_key.encode()
        if logger:
            self.logger = logger
        else:
            self.logger = logging.getLogger()
            self.logger.addHandler(logging.StreamHandler())
            self.logger.setLevel(logging.INFO)

    def gen_authorization(self, request, timestamp=None, expire_period=1800):
        """
        generate authorization string
        if not specify timestamp, then use current time;
        """
        signedheaders = []
        if "headers" in request:
            signedheaders = list(key.lower() for key in request["headers"].keys() if key != '')
            signedheaders.sort()
        authorization = build_authorization(self.access_key,
                signedheaders, expire_period, timestamp)
        signingkey = self._calc_signingkey(authorization)
        self.logger.debug("SigningKey: %(signingkey)s", {"signingkey": signingkey})
        signature = self._calc_signature(signingkey, request, signedheaders)
        authorization["signature"] = signature
        return serialize_authorization(authorization)

    def authenticate(self, authorization, request):
        """
        autenticate a request.
        calcaulate request signature, and compare with authorization
        """
        signingkey = self._calc_signingkey(authorization)
        self.logger.debug("SigningKey: %(signingkey)s", {"signingkey": signingkey})
        signature = self._calc_signature(signingkey, request, authorization["signedheaders"])
        return signature == authorization["signature"]

    @staticmethod
    def get_utf8_value(value):
        """
        Get the UTF8-encoded version of a value.
        """
        if not isinstance(value, (str, unicode)):
            value = str(value)
        if isinstance(value, unicode):
            return value.encode('utf-8')
        else:
            return value

    @staticmethod
    def canonical_qs(params):
        """
        Construct a sorted, correctly encoded query string
        """
        keys = list(params)
        keys.sort()
        pairs = []
        for key in keys:
            if key == "authorization":
                continue
            val = BceSigner.normalized(params[key])
            pairs.append(urllib.quote(key, safe='') + '=' + val)
        qs = '&'.join(pairs)
        return qs

    @staticmethod
    def canonical_header_str(headers, signedheaders=None):
        """
        calculate canonicalized header string
        """
        headers_norm_lower = dict()
        for (k, v) in headers.iteritems():
            key_norm_lower = BceSigner.normalized(k.lower())
            value_norm_lower = BceSigner.normalized(v.strip())
            headers_norm_lower[key_norm_lower] = value_norm_lower
        keys = list(headers_norm_lower)
        keys.sort()
        if "host" not in keys:
            raise IAMHeaderError(ErrorInfo(IAM_HEADERS_NOHOST))
        header_list = []
        default_signed = ("host", "content-length", "content-type", "content-md5")
        if signedheaders:
            for key in signedheaders:
                key = BceSigner.normalized(key.lower())
                if key not in keys:
                    raise IAMHeaderError(ErrorInfo(IAM_HEADERS_NOSIGNED))
                if headers_norm_lower[key]:
                    header_list.append(key + ":" + headers_norm_lower[key])
        else:
            for key in keys:
                if key.startswith("x-bce-") or key in default_signed:
                    header_list.append(key + ":" + headers_norm_lower[key])
        return '\n'.join(header_list)

    @staticmethod
    def normalized_uri(uri):
        """
        Construct a normalized(except slash '/') uri
        eg. /json-api/v1/example/ ==> /json-api/v1/example/
        """
        return urllib.quote(BceSigner.get_utf8_value(uri), safe='-_.~/')

    @staticmethod
    def normalized(msg):
        """
        Construct a normalized uri
        """
        return urllib.quote(BceSigner.get_utf8_value(msg), safe='-_.~')

    def _calc_signingkey(self, auth):
        """ Get a a signing key """
        string_to_sign = "/".join((auth['version'], auth['access'],
            auth['timestamp'], auth['period']))
        try:
            signingkey = hmac.new(self.secret_key, self.get_utf8_value(string_to_sign),
                                 hashlib.sha256).hexdigest()
            return signingkey
        except Exception as err:
            raise IAMSignatureError(ErrorInfo(IAM_SIGNINGKEY, str(err)))

    def _calc_signature(self, key, request, signedheaders):
        """Generate BCE signature string."""
        if not request.get('method'):
            raise IAMMethodError(ErrorInfo(IAM_NOMETHOD))
        if not request.get('uri'):
            raise IAMURIError(ErrorInfo(IAM_NOURI))
        # Create canonical request
        params = {}
        headers = {}
        if "params" in request: params = request['params']
        if "headers" in request: headers = request['headers']
        cr = "\n".join((request['method'].upper(),
                        self.normalized_uri(request['uri']),
                        self.canonical_qs(params),
                        BceSigner.canonical_header_str(headers, signedheaders)))
        try:
            self.logger.debug("CanonicalRequest: %(request)s", {"request": cr})
            signature = hmac.new(key, cr, hashlib.sha256).hexdigest()
            self.logger.debug("Signature: %(signature)s", {"signature": signature})
            return signature
        except Exception as err:
            raise IAMSignatureError(ErrorInfo(IAM_SIGNATURE, str(err)))


class IAMError(Exception):
    """
    base Error class
    """
    def __init__(self, message):
        self.message = "[IAMError] " + message
        super(IAMError, self).__init__(self.message)




if __name__ == '__main__':
    headers={}
    accesskey='xxxxx'
    secretkey='xxxxx'
    headers['x-bce-date'] = datetime.datetime.utcnow().strftime('%Y-%m-%dT%H:%M:%SZ')
    headers['Content-Type'] = 'application/json'
    headers['Host'] = 'lss.bj.baidubce.com'
    
    
    
     
    request = {
        'method': 'GET',
        'uri': '/v5/domain/<playDomain>/app',
        'headers': headers
    }
    
    signer = BceSigner(accesskey, secretkey)
    auth = signer.gen_authorization(request)
    headers['Authorization'] = auth
    print (headers)

    
    
    url = 'http://lss.bj.baidubce.com/v5/domain/<playDomain>/app'
    response = requests.request('GET',url, headers=headers)
    print response.text
