package EtfRecommendService.comment.dto;

import EtfRecommendService.comment.domain.Comment;
import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record CommentResponse(
        Long id,
        Long userId,
        String nickName,
        String content,
        Long likesCount,
        LocalDateTime createdAt
) {
    public static CommentResponse toDto(Comment comment){
        return CommentResponse.builder()
                .id(comment.getId())
                .userId(comment.getUser().getId())
                .nickName(comment.getUser().getNickName())
                .content(comment.getContent())
                .likesCount(Long.parseLong(String.valueOf(comment.getCommentLikes().size())) )
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
