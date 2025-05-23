package EtfRecommendService.webSocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

//메시지 타입 판별
@Component
public class MessageTypeClassifier {
    private final ObjectMapper objectMapper;

    public MessageTypeClassifier(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public WebSocketMessageType classify(String payload) {
        try {
            if (payload.startsWith("{")) {
                JsonNode root = objectMapper.readTree(payload);
                String trId = root.path("header").path("tr_id").asText();
                if ("PINGPONG".equals(trId)) {
                    return WebSocketMessageType.PINGPONG;
                } else if ("H0STCNT0".equals(trId)) {
                    return WebSocketMessageType.SUBSCRIBE_SUCCESS;
                } else {
                    return WebSocketMessageType.STOCK_PRICE_DATA;
                }
            } else if (payload.contains("|")) {
                return WebSocketMessageType.STOCK_PRICE_DATA;
            } else {
                return WebSocketMessageType.UNKNOWN;
            }
        } catch (Exception e) {
            return WebSocketMessageType.UNKNOWN;
        }
    }
}

