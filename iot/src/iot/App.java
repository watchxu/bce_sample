package iot;
import com.baidubce.BceClientConfiguration;
import com.baidubce.auth.DefaultBceCredentials;
import com.baidubce.services.iothub.IotHubClient;
import com.baidubce.services.iothub.model.ListResponse;

//import java.util.HashMap;
//import java.util.logging.Logger;

/**
 * 获取iothub实例列表（即全部endpoints）
 */
public class App {

   // private static Logger logger = Logger.getLogger(App.class.getName());

    public static void main(String[] args) {
        String ak = "758903b8f51549eda94bbb5fc5e390d3";  // AccessKeyId
        String sk = "82ddfbe1304d46799b16debd5b562ace";  // SecretAccessKey
        String endpoint = "iot.gz.baidubce.com";

        BceClientConfiguration config = new BceClientConfiguration()
                .withCredentials(new DefaultBceCredentials(ak, sk))
                .withEndpoint(endpoint);
        IotHubClient client = new IotHubClient(config);

        ListResponse response = client.listEndpoints();
        System.out.println("hello");
        System.out.println(response.getResult());
        System.out.println("hello");
       // for (HashMap<String, String> endpointInfo : response.getResult()) {
         //   logger.info(String.valueOf(endpointInfo));
        //}
    }
}
