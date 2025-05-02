package EtfRecommendService.etf.notification;

import java.time.LocalDateTime;

public record NotificationRequest(
        String userId,
        String message,
        LocalDateTime expiredTime
) {
}
