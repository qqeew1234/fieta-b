package EtfRecommendService.notification;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    //long countByUserIdAndIsReadFalse(String userId);


}
