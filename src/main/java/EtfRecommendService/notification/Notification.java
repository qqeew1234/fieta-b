package EtfRecommendService.notification;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String receiverId; // 실제 ID 값 (예: "1")

    @Enumerated(EnumType.STRING)
    private ReceiverType receiverType; // USER, ADMIN 구별

    private String message;
    private boolean isRead = false;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private String targetId;

    public Notification(String receiverId, ReceiverType receiverType, String message, NotificationType type, String targetId) {
        this.receiverId = receiverId;
        this.receiverType = receiverType;
        this.message = message;
        this.type = type;
        this.targetId = targetId;
    }
}
