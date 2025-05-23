package EtfRecommendService.etf;

import EtfRecommendService.etf.dto.RealtimePrice;
import EtfRecommendService.webSocket.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class EtfPriceService {

    private final SimpMessagingTemplate messagingTemplate;
    private final EtfWatchService etfWatchService;
    private final RedisService redisService;

    @Scheduled(fixedRate = 1000)
    public void publishSubscribedEtfPrices() {
        redisService.getAllStockPriceData()
                .stream()
                .filter(stockPriceData -> etfWatchService.isWatched(stockPriceData.stockCode()))
                .forEach(stockPriceData -> {
                    String topic = String.format("/topic/etf/%s/price", stockPriceData.stockCode());
                    log.info("Publishing price to {}: {}", topic, stockPriceData.currentPrice());
                    messagingTemplate.convertAndSend(
                            topic,
                            new RealtimePrice(
                                    stockPriceData.stockCode(),
                                    (int) stockPriceData.currentPrice(),
                                    stockPriceData.dayOverDayRate(),
                                    stockPriceData.accumulatedVolume()
                            )
                    );
                });
    }
}
