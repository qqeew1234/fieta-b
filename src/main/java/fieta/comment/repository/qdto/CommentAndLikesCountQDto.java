package fieta.comment.repository.qdto;

import fieta.comment.domain.Comment;

public record CommentAndLikesCountQDto(
        Comment comment,
        Long likesCount
) {
}
