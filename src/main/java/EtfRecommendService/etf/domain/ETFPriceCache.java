package EtfRecommendService.etf.domain;

import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ETFPriceCache {

    private final Map<String, Integer> realtimeEtfPriceMap = new ConcurrentHashMap<>();


    public int getPrice(String etfCode) {
        return realtimeEtfPriceMap.get(etfCode);
    }
}
