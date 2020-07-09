package pl.com.krx.malinowka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.logging.Logger;


public class DataSender {

    private static final Logger log = Logger.getLogger("datasender");

    public static final String host = "http://pi.krx.com.pl";

    public static void send(UUID deviceId, Double value) throws IOException {


        HttpPost httpPost = new HttpPost(host + "/device/" + deviceId.toString());

        LocalDateTime now = LocalDateTime.now();

        ObjectMapper objectMapper = new ObjectMapper();

        String json = objectMapper.writeValueAsString(new Element(value, now.toString()));

        StringEntity stringEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
        httpPost.setEntity(stringEntity);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = httpClient.execute(httpPost);
        log.info("sent "+json+" with response "+response.getStatusLine().getStatusCode());



    }

}
