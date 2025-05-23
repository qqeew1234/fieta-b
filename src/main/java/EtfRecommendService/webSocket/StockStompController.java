package EtfRecommendService.webSocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@MessageMapping("/stocks") //클라이언트가 보낼때 사용
@Controller
@RequiredArgsConstructor
public class StockStompController {
    private final SimpMessagingTemplate template;

    // 백엔드 내부에서 이 메서드를 호출해서 전송
    public void broadcast(StockPriceData data) {
        // 전체 구독자에게 전송
        template.convertAndSend("/topic/stocks/all", data);
        // 종목별 구독자에게 전송
        template.convertAndSend("/topic/stocks/" + data.stockCode(), data);
    }
}
