package EtfRecommendService.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

// WebSocketConfigurer의 구현체가 되어 빈으로 주입이 될 예정
@Configuration
@EnableWebSocket //웹소켓 활성화
public class WebSocketConfig implements WebSocketConfigurer {
    private final WebSocketHandler webSocketHandler;

    public WebSocketConfig(WebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    //해당 주소로 접근하면 웹소켓 연결
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler, "/board")
                //클라이언트에서 웹소켓 서버에 요청시 http://localhost:8080"에서 들어오는 WebSocket 연결만을 허용
                .setAllowedOrigins("http://localhost:8080");
    }
}
