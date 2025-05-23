package EtfRecommendService.webSocket;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import java.util.List;

//ApplicationRunner는 Spring Boot에서 애플리케이션 실행 이후 로직 자동 실행
@Component
public class WebSocketStartup implements ApplicationRunner {

    private final CsvLoader csvLoader;
    private final WebSocketKey webSocketKeyService;
    private final WebSocketConnectionService webSocketConnectionService;

    public WebSocketStartup(CsvLoader csvLoader, WebSocketKey webSocketKeyService, WebSocketConnectionService webSocketConnectionService) {
        this.csvLoader = csvLoader;
        this.webSocketKeyService = webSocketKeyService;
        this.webSocketConnectionService = webSocketConnectionService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 1) approval_key 발급
        String approvalKey = webSocketKeyService.getApprovalKey();

        // 2) CSV에서 구독할 종목코드 (첫 페이지 50개)
        List<String> 종목코드 = csvLoader
//                .loadCodes("src/main/resources/etf_data_result.csv")
                .getCodes()
                .subList(0,10);

//        //테스트용
//        List<String> 종목코드 = List.of("005930");

        // 3) WebSocket 연결 및 구독 시작
        webSocketConnectionService.connect(approvalKey, "H0STCNT0", 종목코드);

        System.out.println("발급된 approvalKey = " + approvalKey);
    }
}
