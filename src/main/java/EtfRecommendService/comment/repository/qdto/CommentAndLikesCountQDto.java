package EtfRecommendService.comment.repository.qdto;

import EtfRecommendService.comment.domain.Comment;

public record CommentAndLikesCountQDto(
        Comment comment,
        Long likesCount
) {
}
