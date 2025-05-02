package EtfRecommendService.etf.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequiredArgsConstructor
@RestController
public class NotificationRestController {

    private final NotificationService notificationService;

    @GetMapping("/sse/notifications")
    public SseEmitter streamNotifications(@RequestParam String emitterId) {
        System.out.println("연결 요청: " + emitterId);
        return notificationService.createEmitter(emitterId);
    }

    @PostMapping("/notifications/send")
    public ResponseEntity<String> sendNotification(@RequestBody NotificationRequest request) {
        NotificationDto notificationDto = new NotificationDto(
                request.message(),
                request.expiredTime()
        );

        notificationService.sendNotificationToUser(request,notificationDto);

        return ResponseEntity.status(HttpStatus.OK).body("알림 전송 완료");
    }
}
