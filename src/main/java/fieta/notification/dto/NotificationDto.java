package fieta.notification.dto;

import fieta.notification.NotificationType;
import fieta.notification.ReceiverType;

public record NotificationDto(
        String message,
        ReceiverType receiverType,
        NotificationType type,
        String targetId
) {

}
