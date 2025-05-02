package EtfRecommendService.report.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record ReportListResponse(
        List<CommentReportResponse> commentReportResponseList,
        List<ReplyReportResponse> replyReportResponseList
) {
}
