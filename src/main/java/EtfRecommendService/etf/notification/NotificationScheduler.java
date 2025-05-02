package EtfRecommendService.etf.notification;

import org.springframework.stereotype.Service;

@Service
public class NotificationScheduler {

    /*private final SubscriptionRepository subscriptionRepository;
    private final NotificationService notificationService;

    public NotificationScheduler(SubscriptionRepository subscriptionRepository,
                                 NotificationService notificationService) {
        this.subscriptionRepository = subscriptionRepository;
        this.notificationService = notificationService;
    }

    // 매 시간마다 실행 (초/분/시간/일/월/요일)
    @Scheduled(cron = "0 0 * * * *") // 매시 정각마다
    public void notifyExpiringSubscriptions() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneDayLater = now.plusDays(1);

        List<Subscription> expiringSubscriptions = subscriptionRepository
                .findByExpiryDateBetween(now, oneDayLater);

        for (Subscription subscription : expiringSubscriptions) {
            String message = "구독하신 ETF '" + subscription.getEtfName() + "'가 내일 만료됩니다.";

            notificationService.sendNotificationToUser(
                    subscription.getUserId(), // emitterId
                    new NotificationDto(message, LocalDateTime.now())
            );
        }
    }*/
}
