<?php
require "auth.php";
// 第一步：生成认证字符串
$ak = "";  // AccessKeyId
$sk = "";  // SecretAccessKey

$method = "POST";
$host = "aip.baidubce.com";
$uri = "/rest/2.0/ocr/v1/general_basic";

$params = array();
date_default_timezone_set('UTC');
$timestamp = new \DateTime();
$expirationInSeconds = 60;
$authorization = generateAuthorization($ak, $sk, $method, $host, $uri, $params, $timestamp, $expirationInSeconds);

// 第二步：构造HTTP请求的header、body等信息
$url = "http://{$host}{$uri}";
$timeStr = $timestamp->format("Y-m-d\TH:i:s\Z");
$head =  array(
"Content-Type: application/x-www-form-urlencoded",
"Authorization:{$authorization}",
"x-bce-date:{$timeStr}",
);


$img = file_get_contents('idcard.jpg');
$img = base64_encode($img);

$body = array(
  "image" => $img
);

//$bodyStr = json_encode($body);


// 第三步：发送HTTP请求，并输出响应信息。
$curlp = curl_init();
curl_setopt($curlp, CURLOPT_URL, $url);
curl_setopt($curlp, CURLOPT_HTTPHEADER, $head);
curl_setopt($curlp, CURLOPT_CUSTOMREQUEST, "POST");
curl_setopt($curlp, CURLOPT_POSTFIELDS, $body);
curl_setopt($curlp, CURLINFO_HEADER_OUT, 1);
curl_setopt($curlp, CURLOPT_RETURNTRANSFER, 1);
$response = curl_exec($curlp);
$request = curl_getinfo($curlp, CURLINFO_HEADER_OUT);
$status = curl_getinfo($curlp, CURLINFO_HTTP_CODE);
curl_close($curlp);
print("request: {$request}\n");
print("request body: {$bodyStr}\n");
print("status: {$status}\n");
print("response: {$response}\n");;


?>
