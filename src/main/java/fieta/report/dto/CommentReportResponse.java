package fieta.report.dto;

import fieta.report.domain.CommentReport;
import lombok.Builder;

import java.time.LocalDateTime;
@Builder
public record CommentReportResponse(
        Long reportId,
        String reportReason,
        LocalDateTime createdAt
) {
    public static CommentReportResponse toDto(CommentReport commentReport){
        return CommentReportResponse.builder()
                .reportId(commentReport.getId())
                .reportReason(commentReport.getReportReason().toString())
                .createdAt(commentReport.getCreatedAt())
                .build();
    }
}
