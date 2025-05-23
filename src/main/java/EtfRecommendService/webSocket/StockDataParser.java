package EtfRecommendService.webSocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class StockDataParser {
    private final ObjectMapper objectMapper;

    // 원시 JSON 문자열 → JsonNode → parseFromJson 호출
    // jsonnode : JSON 데이터를 Java에서 다루기 쉽게 구조화
    //objectMapper.readTree()로 파싱하면 JsonNode 객체
    public StockPriceData parseFromJsonString(String jsonStr) throws Exception {
        JsonNode root = objectMapper.readTree(jsonStr);
        //json 문자열에서 body.output 부분을 추출
        JsonNode output = root.path("body").path("output");
        return parseFromJson(output);
    }

    //json node로 들어왔을때 사용
    //stockpricedata로 변환
    public StockPriceData parseFromJson(JsonNode json) {
        return StockPriceData.builder()
                .stockCode(json.path("stockCode").asText())
                .currentPrice(json.path("currentPrice").asDouble())
                .dayOverDaySign(json.path("dayOverDaySign").asText())
                .dayOverDayChange(json.path("dayOverDayChange").asInt())
                .dayOverDayRate(json.path("dayOverDayRate").asDouble())
                .accumulatedVolume(json.path("accumulatedVolume").asLong())
                .build();
    }

    //파이프(|)로 구분된 원시 문자열에서 바로 객체로 변환
    public StockPriceData parseFromPipe(String pipe) throws Exception {
        String[] parts = pipe.split("\\|");
        if (parts.length < 4 || !parts[3].contains("^")) {
            throw new IllegalArgumentException("Invalid pipe message");
        }
        String[] fields = parts[3].split("\\^");
        if (fields.length < 14) {
            throw new IllegalArgumentException("필드 부족");
        }
        return StockPriceData.builder()
                .stockCode(fields[0])
                .currentPrice(Double.parseDouble(fields[2]))
                .dayOverDaySign(fields[3])
                .dayOverDayChange(Integer.parseInt(fields[4]))
                .dayOverDayRate(Double.parseDouble(fields[5]))
                .accumulatedVolume(Long.parseLong(fields[13]))
                .build();
    }
}


