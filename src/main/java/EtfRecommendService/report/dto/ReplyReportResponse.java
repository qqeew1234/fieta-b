package EtfRecommendService.report.dto;

import EtfRecommendService.report.domain.ReplyReport;
import lombok.Builder;

import java.time.LocalDateTime;
@Builder
public record ReplyReportResponse(
        Long reportId,
        String reportReason,
        LocalDateTime createdAt
) {
    public static ReplyReportResponse toDto(ReplyReport report){
        return ReplyReportResponse.builder()
                .reportId(report.getId())
                .reportReason(report.getReportReason().toString())
                .createdAt(report.getCreatedAt())
                .build();
    }
}
