<?php
require "auth.php";
// 第一步：生成认证字符串
$ak = "xxxxx";  // AccessKeyId
$sk = "xxxxx";  // SecretAccessKey

$method = "POST";
$host = "bcc.bj.baidubce.com";
$uri = "/v2/instance";

$params = array("clientToken" => uniqid("", true));
date_default_timezone_set('UTC');
$timestamp = new \DateTime();
$expirationInSeconds = 60;
$authorization = generateAuthorization($ak, $sk, $method, $host, $uri, $params, $timestamp, $expirationInSeconds);

// 第二步：构造HTTP请求的header、body等信息
$url = "http://{$host}{$uri}?clientToken={$params["clientToken"]}";
$timeStr = $timestamp->format("Y-m-d\TH:i:s\Z");
$head =  array(
"Content-Type: application/json",
"Authorization:{$authorization}",
"x-bce-date:{$timeStr}",
);
$body = array(
		"instanceType"=>"bcc.t1.tiny",
		"name" => "baidu",
		"imageId" => "m-nA5jdLpQ",  //镜像id
		"billing" => array(
				"paymentTiming" => "Postpaid",   //创建后付费的BCC
				"reservation"=>array(
						"reservationLength" => "1"
				)
		
)								
);

$bodyStr = json_encode($body);


// 第三步：发送HTTP请求，并输出响应信息。
$curlp = curl_init();
//curl_setopt($curlp, CURLOPT_POST, 1);
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
print("response: {$response}\n");;


?>
