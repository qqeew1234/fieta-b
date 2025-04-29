package EtfRecommendService.notification;

public record NotificationDto(
        String message,
        ReceiverType receiverType,
        NotificationType type,
        String targetId
) {

}
