<?php
require "auth.php";
// 第一步：生成认证字符串
$ak = "3ca61c9e73dd46abb6bf9a40c028c32f";  // AccessKeyId
$sk = "456409ff56a84f78af91c15c0b1fd6ba";  // SecretAccessKey

$method = "PUT";
$host = "bcc.bj.baidubce.com";
$uri = "/v2/instance/i-qOTHAwTB";

$params = array("changePass"=>"");

date_default_timezone_set('UTC');
$timestamp = new \DateTime();
$expirationInSeconds = 60;
$authorization = generateAuthorization($ak, $sk, $method, $host, $uri, $params, $timestamp, $expirationInSeconds);

// 第二步：构造HTTP请求的header、body等信息
$url = "http://{$host}{$uri}?changePass";
$timeStr = $timestamp->format("Y-m-d\TH:i:s\Z");
$head =  array(
"Content-Type: application/json",
"Authorization:{$authorization}",
"x-bce-date:{$timeStr}",

);
//加密部分
class userPass {
    public function aes128WithFirst16Char($adminPass, $secretAccessKey)
    {
        $adminPass = $this->pkcs5Pad($adminPass);
        $secretAccessKey = substr($secretAccessKey, 0, 16);
        $crypted = openssl_encrypt($adminPass, 'AES-128-ECB', $secretAccessKey, OPENSSL_RAW_DATA);
        return bin2hex(substr($crypted, 0, 16));
    }
    
    /**
     * This is a filling algorithm, the purpose is to ensure that the content length reached 16
     *
     * @param String $adminPass
     *          The content String to filling.
     *
     * @return string
     *          ensure that the content length reached 16
     */
    private function pkcs5Pad($adminPass)
    {
        $pad = 16 - (strlen($adminPass) % 16);
        return $adminPass . str_repeat(chr($pad), $pad);
    }
}
$obj = new userPass();
$result = $obj->aes128WithFirst16Char("baidu@123","456409ff56a84f78af91c15c0b1fd6ba");  //前面是需要修改的秘密，后面是百度云中的sk

$body = array(
	"adminPass" =>"$result"
);
$bodyStr = json_encode($body);


// 第三步：发送HTTP请求，并输出响应信息。
$curlp = curl_init();
curl_setopt($curlp, CURLOPT_POST, 1);
curl_setopt($curlp, CURLOPT_URL, $url);
curl_setopt($curlp, CURLOPT_HTTPHEADER, $head);
curl_setopt($curlp, CURLOPT_CUSTOMREQUEST, "PUT");
curl_setopt($curlp, CURLOPT_POSTFIELDS, $bodyStr);
curl_setopt($curlp, CURLINFO_HEADER_OUT, 1);
curl_setopt($curlp, CURLOPT_RETURNTRANSFER, 1);
$response = curl_exec($curlp);
$request = curl_getinfo($curlp, CURLINFO_HEADER_OUT);
$status = curl_getinfo($curlp, CURLINFO_HTTP_CODE);
curl_close($curlp);
print("request: {$request}\n");
print("request body: {$bodyStr}\n");
print("status: {$status}\n");
print("response: {$response}\n");

?>
