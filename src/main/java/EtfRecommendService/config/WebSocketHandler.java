package EtfRecommendService.config;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

//웹소켓 서버 구현
@Component
public class WebSocketHandler extends TextWebSocketHandler {
    private static Set<WebSocketSession> CLIENTS = Collections.synchronizedSet(new HashSet<>());

    //클라이언트 접속 시 호출
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception{
        System.out.println(session.toString());

        if (CLIENTS.contains(session)){
            System.out.println("이미 연결된 세션" + session);
        } else {
            CLIENTS.add(session);
            System.out.println("새로운 세션" + session);
        }
    }

    //클라이언트 접속 해제 시 호출
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws  Exception{
        CLIENTS.remove(session);
    }

}
