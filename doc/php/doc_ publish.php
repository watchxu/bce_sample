<?php
require "auth.php";
// 第一步：生成认证字符串
$ak = "34be01466ea941ecbba30c0a2e48e917";  // AccessKeyId
$sk = "23b39cd3151349929b7ac20b5d62aa61";  // SecretAccessKey

$method = "POST";
$host = "doc.bj.baidubce.com";
$uri = "/v2/document/";

$params = array("source" => "bos");

date_default_timezone_set('UTC');
$timestamp = new \DateTime();
$expirationInSeconds = 60;
$authorization = generateAuthorization($ak, $sk, $method, $host, $uri, $params, $timestamp, $expirationInSeconds);

// 第二步：构造HTTP请求的header、body等信息
$url = "http://{$host}{$uri}?source=bos";
$timeStr = $timestamp->format("Y-m-d\TH:i:s\Z");
$head =  array(
"Content-Type: application/json",
"Authorization:{$authorization}",
"x-bce-date:{$timeStr}",

);
$body = array(
    "bucket"=>"rdsbos",
    "object"=>"cloud.xls",
    "title"=>"2",
    "format"=>"xls",
);
$bodyStr  = json_encode($body);
// 第三步：发送HTTP请求，并输出响应信息。
$curlp = curl_init();
curl_setopt($curlp, CURLOPT_POST, 1);
curl_setopt($curlp, CURLOPT_URL, $url);
curl_setopt($curlp, CURLOPT_HTTPHEADER, $head);
curl_setopt($curlp, CURLOPT_CUSTOMREQUEST, "POST");
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