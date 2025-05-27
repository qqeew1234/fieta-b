package fieta.comment.repository.qdto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CommentQDto(
        Long id,
        Long etfId,
        Long userId,
        String nickname,
        String content,
        Long likesCount,
        LocalDateTime createdAt
) {
}
