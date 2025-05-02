package EtfRecommendService.etf.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Service
public class NotificationService {


    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final NotificationRepository notificationRepository;

    public SseEmitter createEmitter(String emitterId) {
        SseEmitter emitter = new SseEmitter(0L); // timeout 없음
        emitters.put(emitterId, emitter);

        emitter.onCompletion(() -> emitters.remove(emitterId));
        emitter.onTimeout(() -> emitters.remove(emitterId));
        emitter.onError(e -> emitters.remove(emitterId));
        return emitter;
    }

    public void sendNotificationToUser(NotificationRequest request, NotificationDto data) {
        SseEmitter emitter = emitters.get(request.userId());

        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("newPost")
                        .data(data));
            } catch (IOException e) {
                emitter.completeWithError(e);
                emitters.remove(request.userId());
                saveNotification(request.userId(), data);
            }
        } else {
            saveNotification(request.userId(), data);
        }
    }

    private void saveNotification(String emitterId, NotificationDto data) {
        Notification notification = new Notification(emitterId, data.message(), data.expiredTime());
        notificationRepository.save(notification);
    }

    /*public long countUnreadNotifications(String emitterId) {
        return notificationRepository.countByUserIdAndIsReadFalse(emitterId);
    }

    public void markAllAsRead(String emitterId) {
        notificationRepository.markAllAsRead(emitterId);
    }*/

}

