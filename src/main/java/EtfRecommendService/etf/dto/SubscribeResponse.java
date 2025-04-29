package EtfRecommendService.etf.dto;

import java.time.LocalDateTime;

public record SubscribeResponse(
        Long id,
        Long etfId,
        LocalDateTime createdAt,
        LocalDateTime expiredAt
) {
}
