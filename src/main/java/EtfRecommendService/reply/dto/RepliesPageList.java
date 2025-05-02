package EtfRecommendService.reply.dto;

import lombok.Builder;

import java.util.List;
@Builder
public record RepliesPageList(
        int page,
        int size,
        Long totalElements,
        int totalPages,
        Long commentId,
        List<ReplyResponse> replyResponses
) {
}
