package EtfRecommendService.comment.repository.qdto;

import lombok.Builder;

import java.util.List;
@Builder
public record SortedCommentsQDto(
        List<CommentAndLikesCountQDto> commentAndLikesCountQDtoPage,
        Long totalCount
) {
}
