package EtfRecommendService.etf.notification;

import java.time.LocalDateTime;

public record NotificationDto(
        String message,
        LocalDateTime expiredTime
) {
}
