package bos;

import com.baidubce.auth.DefaultBceCredentials;
import com.baidubce.services.bos.BosClient;
import com.baidubce.services.bos.BosClientConfiguration;
import com.baidubce.services.bos.model.BucketSummary;

public class Sample {
    public static class getUrl 
    {

            public static void main(String[] args) 
            {
                String ACCESS_KEY_ID = "758903b8f51549eda94bbb5fc5e390d3";                   // 用户的Access Key ID
                String SECRET_ACCESS_KEY = "82ddfbe1304d46799b16debd5b562ace";           // 用户的Secret Access Key
                //String ENDPOINT = "http://bj.bcebos.com"; 
                //String BUCKET_NAME = "baidubce1";
                //String OBKECTKEY = "1.mp4";
 //               int EXPIRATIONINSECONDS =-1;
                // 初始化一个BosClient
                BosClientConfiguration config = new BosClientConfiguration();
                config.setCredentials(new DefaultBceCredentials(ACCESS_KEY_ID, SECRET_ACCESS_KEY));
 //               config.setEndpoint(ENDPOINT);
                
                BosClient client = new BosClient(config);
                
//                generatePresignedUrl(client,BUCKET_NAME,OBKECTKEY,EXPIRATIONINSECONDS);
                listBuckets(client);
            }
           
				
			}
 //   public static  String generatePresignedUrl(BosClient client, String bucketName, String objectKey, int expirationInSeconds)
 //   {
 //    System.out.println(bucketName);

 //      java.net.URL url = client.generatePresignedUrl(bucketName, objectKey, expirationInSeconds);          

       //指定用户需要获取的Object所在的Bucket名称、该Object名称、时间戳、URL的有效时长   
      // System.out.println(bucketName);

 //       System.out.println(url.toString());
 //       return url.toString();
 //   }

//private static void generatePresignedUrl(BosClient client, String bUCKET_NAME, String oBKECTKEY,
//        String eXPIRATIONINSECONDS) 
{
    // TODO Auto-generated method stub
    
}
public static void listBuckets (BosClient client) {
    // 获取用户的Bucket列表
    java.util.List<BucketSummary> buckets = client.listBuckets().getBuckets();
    
   
    for (BucketSummary bucket : buckets) {
    	System.out.println(bucket.getName());
    
    }
}

}
