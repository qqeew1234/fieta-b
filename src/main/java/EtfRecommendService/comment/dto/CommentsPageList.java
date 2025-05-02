package EtfRecommendService.comment.dto;

import lombok.Builder;

import java.util.List;
@Builder
public record CommentsPageList(
        int page,
        int size,
        Long totalElements,
        int totalPages,
        Long etfId,
        List<CommentResponse> commentResponses
) {
}
