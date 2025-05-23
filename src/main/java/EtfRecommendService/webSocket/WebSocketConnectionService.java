package EtfRecommendService.webSocket;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.net.URI;
import java.util.List;

//한국투자 api에 클라이언트로 연결 + 메세지 받아오는 역할
//ReactorNettyWebSocketClient를 이용
// 웹소켓키, id, 종목코드 받아서 웹소켓 연결
@Slf4j
@Service
public class WebSocketConnectionService {

    @Value("${kis.websocket-url}")
    private String apiUrl;

    private final ReactorNettyWebSocketClient client = new ReactorNettyWebSocketClient();
    private Disposable currentConnection;

    // Reactor Netty WebSocket 클라이언트가 가진 세션을 보관할 필드
    private volatile org.springframework.web.reactive.socket.WebSocketSession kisSession;

    private final KisWebSocketHandler kisWebSocketHandler;
    private final StockDataParser stockDataParseUtil;

    public WebSocketConnectionService(KisWebSocketHandler kisWebSocketHandler, StockDataParser stockDataParseUtil) {
        this.kisWebSocketHandler = kisWebSocketHandler;
        this.stockDataParseUtil = stockDataParseUtil;
    }

    //trId:실시간 TR ID ("H0STCNT0")
    // trKey:종목코드
    // 웹소켓 연결- 메세지 전송 + 윈시 데이터 받음
    // 세션 중복 방지는 FrontendWebSocketHandler에서 관리
    // 웹소켓 연결 재시도
    public void connect(String approvalKey, String trId, List<String> trKeys) {
        log.info("connect() 호출됨");
        log.info("approvalKey: " + approvalKey);
        log.info("trId: " + trId);
        log.info("trKeys: " + trKeys);

        currentConnection = client.execute(
                        URI.create(apiUrl + "?approval_key=" + approvalKey),
                        session -> {
                            //세션 참조 저장
                            this.kisSession = session;

                            //여러 종목 구독 요청 20ms 간격으로 전송
                            Flux<WebSocketMessage> sendMessages = Flux.fromIterable(trKeys)
                                    .delayElements(Duration.ofMillis(20))
                                    .map(trKey -> buildPayload(approvalKey, trId, trKey))
                                    .map(session::textMessage);

                            Mono<Void> sendAll = session.send(sendMessages);

                            //원시 데이터 받음 - 파싱은 핸들러에 맡김
                            Mono<Void> receive = session.receive()
                                    .map(WebSocketMessage::getPayloadAsText)
                                    .doOnNext(kisWebSocketHandler::handleText)
                                    .then();

                            return sendAll.then(receive);
                        }
                )
                //연결 실패 시 재시도 : 최대 5회, 5초 고정 대기
                .retryWhen(Retry.fixedDelay(5, Duration.ofSeconds(5))
                        .doBeforeRetry(retrySignal -> log.warn("웹소켓 연결 재시도", retrySignal.totalRetries())))
                .doOnError(err -> log.error("웹소켓 에러: ", err))
                .subscribe();
    }

    //프론트가 페이지를 바꿔서 호출해 주는 메서드
    //현재 연결된 KIS WebSocket 세션이 있으면, 새 키들만 KIS API로 SUBSCRIBE 메시지로 보냄
    public void subscribeNewKeys(List<String> newKeys) {
        if (kisSession == null || !kisSession.isOpen()) {
            log.warn("KIS WebSocket 세션이 없습니다. 새로운 구독 요청을 보낼 수 없습니다.");
            return;
        }

        log.info("새 구독 요청: {}", newKeys);
        Flux<WebSocketMessage> extraSubs = Flux.fromIterable(newKeys)
                .delayElements(Duration.ofMillis(20))
                .map(key -> buildPayload(/* approvalKey */ null, "H0STCNT0", key))
                .map(kisSession::textMessage);

        // 보낸 뒤에도 이전 receive 스트림에 계속 붙어있으므로,
        // 단지 send만 해 주면 됩니다.
        kisSession.send(extraSubs).subscribe(
                null,
                err -> log.error("추가 구독 전송 실패", err),
                ()  -> log.info("추가 구독 전송 완료")
        );
    }

    //스프링이 종료될 때 WebSocket 연결을 정리하도록 하여 리소스 누수를 방지
    //spring 프레임워크가 알아서 호출함
    @PreDestroy
    public void cleanup() {
        log.info("애플리케이션 종료: WebSocket 연결 정리 중...");
        if (currentConnection != null && !currentConnection.isDisposed()) {
            currentConnection.dispose();
            log.info("WebSocket 연결이 정상적으로 종료되었습니다.");
        }
    }


    //한투 api로 보내는 구독 요청 메세지
    private String buildPayload(String approvalKey, String trId, String trKey) {
        return String.format(
                "{\"header\":{\"approval_key\":\"%s\",\"custtype\":\"P\",\"tr_type\":\"1\",\"content-type\":\"utf-8\",\"tr_id\":\"%s\"},"
                        + "\"body\":{\"input\":{\"tr_id\":\"%s\",\"tr_key\":\"%s\"}}}",
                approvalKey, trId, trId, trKey
        );
    }
}
