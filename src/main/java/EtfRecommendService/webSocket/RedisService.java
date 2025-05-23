package EtfRecommendService.webSocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisService(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public void saveStockPriceData(StockPriceData data) {
        String key = "stock:" + data.stockCode();
        try {
            // JSON 직렬화 예시 (ObjectMapper 활용)
            String value = objectMapper.writeValueAsString(data);
            redisTemplate.opsForValue().set(key, value);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public StockPriceData getStockPriceData(String stockCode) {
        String value = redisTemplate.opsForValue().get("stock:" + stockCode);
        if (value == null) return null;
        try {
            return objectMapper.readValue(value, StockPriceData.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
