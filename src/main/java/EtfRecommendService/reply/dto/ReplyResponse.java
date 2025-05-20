package EtfRecommendService.reply.dto;

import lombok.Builder;

import java.time.LocalDateTime;
@Builder
public record ReplyResponse(
        Long id,
        Long userId,
        String nickname,
        String content,
        int likesCount,
        LocalDateTime createdAt
) {
}
