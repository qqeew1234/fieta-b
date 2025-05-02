package EtfRecommendService.report.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ReportRequest(
        Long commentId,
        Long replyId,
        @NotNull
        String reportReason
) {
}
