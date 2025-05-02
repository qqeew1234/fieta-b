package EtfRecommendService.etf.dto;

import java.time.LocalDateTime;

public record EtfDetailResponse(
        Long etfId,
        String etfName,
        String etfCode,
        String companyName,
        LocalDateTime listingDate
) {
}
