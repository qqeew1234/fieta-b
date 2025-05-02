package EtfRecommendService.notification;

import EtfRecommendService.admin.Admin;
import EtfRecommendService.admin.AdminRepository;
import EtfRecommendService.etf.SubscribeRepository;
import EtfRecommendService.etf.domain.Etf;
import EtfRecommendService.etf.domain.Subscribe;
import EtfRecommendService.notification.dto.NotificationDto;
import EtfRecommendService.report.domain.ReportType;
import EtfRecommendService.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Service
@Slf4j
public class NotificationService {
    //TODO: emitters Map의 메모리 누수 방지 막기
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final NotificationRepository notificationRepository;
    private final SubscribeRepository subscribeRepository;
    private final AdminRepository adminRepository;

    public SseEmitter createEmitter(Long receiverId, ReceiverType receiverType) {
        //TODO: 시간 설정하기
        SseEmitter emitter = new SseEmitter(0L); // timeout 없음
        String emitterId = generateEmitterId(receiverId, receiverType);

        // 기존 연결이 있으면 정리
        removeExistingEmitter(emitterId);

        emitters.put(emitterId, emitter);

        emitter.onCompletion(() -> {
            log.debug("SSE 연결 실패 {}", emitterId);
            emitters.remove(emitterId);
        });

        emitter.onTimeout(() -> {
            log.debug("SSE 연결 시간 끝 {}", emitterId);
            emitter.complete();
            emitters.remove(emitterId);
        });

        emitter.onError(e -> {
            log.warn("SSE 연결 에러 {}: {}", emitterId, e.getMessage());
            emitters.remove(emitterId);
        });
        return emitter;
    }

    private void removeExistingEmitter(String emitterId) {
        SseEmitter existingEmitter = emitters.get(emitterId);
        if (existingEmitter != null) {
            try {
                existingEmitter.complete();
            } catch (Exception e) {
                log.warn("이미 연결되어 있는 sse: {}", e.getMessage());
            }
            emitters.remove(emitterId);
        }
    }

    public void sendNotificationToUser(Long receiveId, NotificationDto data) {
        String emitterId = generateEmitterId(receiveId, data.receiverType());
        SseEmitter emitter = emitters.get(emitterId);

        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("newNotification")
                        .data(data));
            } catch (IOException e) {
                log.warn("sse 연결에 실패해서 알림을 저장하기 {}: {}", emitterId, e.getMessage(), e);
                emitter.completeWithError(e);
                emitters.remove(emitterId);
                saveNotification(receiveId, data);
            }
        } else {
            saveNotification(receiveId, data);
        }
    }

    private void saveNotification(Long receiverId, NotificationDto data) {
        notificationRepository.save(new Notification(
                receiverId,
                data.receiverType(),
                data.message(),
                data.type(),
                data.targetId()
        ));
    }

    private String generateEmitterId(Long receiverId, ReceiverType receiverType) {
        return receiverType.name() + ":" + receiverId;
    }

    //구독 만료 하루 전날 알림 보내는 기능
    @Scheduled(cron = "0 0 9 * * *") // 매일 오전 9시 실행
    @Transactional
    public void notifyExpiringSubscriptions() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneDayLater = now.plusDays(1);

        // 하루 뒤 만료될 구독 찾기
        List<Subscribe> expiringSubscriptions = subscribeRepository.findAllByExpiredTimeBetween(
                oneDayLater.withHour(0).withMinute(0).withSecond(0),
                oneDayLater.withHour(23).withMinute(59).withSecond(59)
        );

        for (Subscribe subscribe : expiringSubscriptions) {
            User user = subscribe.getUser();
            Etf etf = subscribe.getEtf();

            String message = etf.getEtfName() + " ETF 구독이 하루 후 만료됩니다.";

            NotificationDto notificationDto = new NotificationDto(
                    message,
                    ReceiverType.USER, // 사용자 알림
                    NotificationType.ETF_SUBSCRIPTION,
                    String.valueOf(etf.getId())); // 대상 ETF의 ID

            sendNotificationToUser(user.getId(), notificationDto);
        }
    }

    //TODO: 댓글 신고 10개 쌓이면 관리자에게 알림 보내는 기본 코드 - 수정 필요
    public void notifyIfReportedOverLimit(Long reportedId, ReportType reportType) {
        // 여러 관리자에게 알림을 보내야 할 수도 있음
        List<Long> adminIds = adminRepository.findAll().stream()
                .map(Admin::getId)
                .toList(); // 예시

        for (Long adminId : adminIds) {
            String emitterId = generateEmitterId(adminId, ReceiverType.ADMIN);
            SseEmitter emitter = emitters.get(emitterId);

            if (emitter != null) {
                try {
                    if (reportType.equals(ReportType.COMMENT)){
                        emitter.send(SseEmitter.event()
                                .name("comment-report-alert")
                                .data("댓글 ID " + reportedId + "이(가) 10회 이상 신고되었습니다."));
                    } else if (reportType.equals(ReportType.REPLY)) {
                        emitter.send(SseEmitter.event()
                                .name("reply-report-alert")
                                .data("댓글 ID " + reportedId + "이(가) 10회 이상 신고되었습니다."));
                    }
                } catch (IOException e) {
                    log.warn("관리자 알림 전송 실패 {}: {}", emitterId, e.getMessage());
                    emitters.remove(emitterId);
                }
            }
        }
    }
}



