package EtfRecommendService.webSocket;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

//웹소켓 키 발급
@Service
public class WebSocketKey {

    @Value("${kis.domain}")
    private String apiUrl;

    @Value("${kis.appkey}")
    private String appKey;

    @Value("${kis.secretkey}")
    private String secretkey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String getApprovalKey() {
        try {
            // 요청 헤더
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 요청 바디
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("grant_type", "client_credentials");
            requestBody.put("appkey", appKey);
            requestBody.put("secretkey", secretkey);

            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

            // 요청 실행
            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl + "/oauth2/Approval",  // 웹소켓 approval_key 요청 URL
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                JSONObject json = new JSONObject(response.getBody());
                String approvalKey = json.getString("approval_key");
                System.out.println("WebSocket Approval Key: " + approvalKey);
                return approvalKey;
            } else {
                throw new RuntimeException("approval_key 발급 실패: " + response.getStatusCode());
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}