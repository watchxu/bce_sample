package bos;

import com.baidubce.auth.DefaultBceCredentials;
import com.baidubce.services.bos.BosClient;
import com.baidubce.services.bos.BosClientConfiguration;
import com.baidubce.services.bos.model.BucketSummary;

public class Listbucket {

	public static void main(String[] args) {
	    String ACCESS_KEY_ID = "758903b8f51549eda94bbb5fc5e390d3";                   // 用户的Access Key ID
	    String SECRET_ACCESS_KEY = "82ddfbe1304d46799b16debd5b562ace";           // 用户的Secret Access Key
	    // 初始化一个BosClient
	    BosClientConfiguration config = new BosClientConfiguration();
	    config.setCredentials(new DefaultBceCredentials(ACCESS_KEY_ID, SECRET_ACCESS_KEY));
	    BosClient client = new BosClient(config);
	    listBuckets(client);
	    System.out.println("hello");
	    }
	public static void listBuckets (BosClient client) {
		 System.out.println("hello");
	    // 获取用户的Bucket列表
	    java.util.List<BucketSummary> buckets = client.listBuckets().getBuckets();

	    // 遍历Bucket
	    for (BucketSummary bucket : buckets) {
	        System.out.println(bucket.getName());
	    }
	}
}
