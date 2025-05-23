package EtfRecommendService.webSocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
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

    public List<StockPriceData> getAllStockPriceData() {
        try {
            Set<String> keys = redisTemplate.keys("stock:" + "*");
            if (keys == null || keys.isEmpty()) {
                log.debug("No stock data keys found in Redis");
                return Collections.emptyList();
            }

            List<String> values = redisTemplate.opsForValue().multiGet(keys);
            if (values == null) {
                log.warn("Failed to retrieve values for stock keys");
                return Collections.emptyList();
            }

            return values.stream()
                    .filter(Objects::nonNull)
                    .map(this::deserializeStockData)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Failed to retrieve all stock data from Redis", e);
            return Collections.emptyList();
        }
    }

    private StockPriceData deserializeStockData(String value) {
        try {
            return objectMapper.readValue(value, StockPriceData.class);
        } catch (JsonProcessingException e) {
            log.warn("Failed to deserialize stock data: {}", value, e);
            return null;
        }
    }
}
