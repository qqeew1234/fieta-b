package EtfRecommendService.notification.dto;

import EtfRecommendService.notification.NotificationType;
import EtfRecommendService.notification.ReceiverType;

public record NotificationDto(
        String message,
        ReceiverType receiverType,
        NotificationType type,
        String targetId
) {

}
