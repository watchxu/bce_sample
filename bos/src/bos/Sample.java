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
                String ACCESS_KEY_ID = "758903b8f51549eda94bbb5fc5e390d3";                   // �û���Access Key ID
                String SECRET_ACCESS_KEY = "82ddfbe1304d46799b16debd5b562ace";           // �û���Secret Access Key
                //String ENDPOINT = "http://bj.bcebos.com"; 
                //String BUCKET_NAME = "baidubce1";
                //String OBKECTKEY = "1.mp4";
 //               int EXPIRATIONINSECONDS =-1;
                // ��ʼ��һ��BosClient
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

       //ָ���û���Ҫ��ȡ��Object���ڵ�Bucket���ơ���Object���ơ�ʱ�����URL����Чʱ��   
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
    // ��ȡ�û���Bucket�б�
    java.util.List<BucketSummary> buckets = client.listBuckets().getBuckets();
    
   
    for (BucketSummary bucket : buckets) {
    	System.out.println(bucket.getName());
    
    }
}

}
