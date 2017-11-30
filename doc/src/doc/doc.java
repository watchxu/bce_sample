package doc;

import com.baidubce.BceClientConfiguration;
import com.baidubce.auth.DefaultBceCredentials;
import com.baidubce.services.doc.DocClient;
import com.baidubce.services.doc.model.GetDocumentResponse;

public class doc {

public static void main(String[] args) {
    String ACCESS_KEY_ID = "758903b8f51549eda94bbb5fc5e390d3";
    String SECRET_ACCESS_KEY = "82ddfbe1304d46799b16debd5b562ace";
    String ENDPOINT = "http://doc.bj.baidubce.com";
    String documentId = "doc-hhpqa3qwesu2wne";
    // 初始化一个DocClient
    BceClientConfiguration config = new BceClientConfiguration();
    config.setCredentials(new DefaultBceCredentials(ACCESS_KEY_ID, SECRET_ACCESS_KEY));
    DocClient client = new DocClient(config);
    config.setEndpoint(ENDPOINT);
    System.out.println("hello");
    //readDocument(client, documentId);
    getDocument(client, documentId);
    }
//public static void readDocument(DocClient client, String documentId) {
//    ReadDocumentResponse resp = client.readDocument(documentId);
//    System.out.println("documentId: " + resp.getDocumentId());
//    System.out.println("host: " + resp.getHost());
//    System.out.println("token: " + resp.getToken());
//    System.out.println("hello");
//}
public static void getDocument(DocClient client, String documentId) {
    GetDocumentResponse resp = client.getDocument(documentId);
    System.out.println("documentId: " + resp.getDocumentId());
    System.out.println("title: " + resp.getTitle());
    System.out.println("format: " + resp.getFormat());
    System.out.println("status: " + resp.getStatus()); 
    if (resp.getStatus() == "PUBLISHED") {
        System.out.println("pageCount: " + resp.getPublishInfo().getPageCount());
        System.out.println("sizeInBytes: " + resp.getPublishInfo().getSizeInBytes());
        System.out.println("coverUrl: " + resp.getPublishInfo().getCoverUrl());
        System.out.println("publishTime: " + resp.getPublishInfo().getPublishTime());
    }
    if (resp.getStatus() == "UPLOADING") {
        System.out.println("bucket: " + resp.getUploadInfo().getBucket());
        System.out.println("object: " + resp.getUploadInfo().getObject());
        System.out.println("bosEndpoint: " + resp.getUploadInfo().getBosEndpoint());
        
       
    }       
    if (resp.getStatus() == "FAILED") {
        System.out.println("errorCode: " + resp.getError().getCode());
        System.out.println("errorMessage: " + resp.getError().getMessage());
    }
    System.out.println("notification: " + resp.getNotification());
    System.out.println("createTime: " + resp.getCreateTime());
    System.out.println("hello");
}

}
