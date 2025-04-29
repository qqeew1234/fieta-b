package EtfRecommendService.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequiredArgsConstructor
@RestController
public class NotificationRestController {

    private final NotificationService notificationService;

    @GetMapping("/sse/notifications")
    public SseEmitter streamNotifications(@RequestParam String receiveId,
                                          @RequestParam ReceiverType receiverType) {
        System.out.println("연결 요청: " + receiveId);
        return notificationService.createEmitter(receiveId, receiverType);
    }

    /*
    //특정 이벤트 발생 시 알림 전송 - TODO : 나중에 필요하면 사용, 필요없으면 지우기
    @PostMapping("/notifications/send")
    public ResponseEntity<String> sendNotification(@RequestBody NotificationRequest request) {
        if (request.userId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("사용자 ID가 필요합니다");
        }

        NotificationDto notificationDto = new NotificationDto(
                request.message(),
                request.expiredTime()
        );

        // userId를 String으로 변환하여 전달
        notificationService.sendNotificationToUser(String.valueOf(request.userId()), notificationDto);

        return ResponseEntity.status(HttpStatus.OK).body("알림 전송 완료");
    }*/
}
