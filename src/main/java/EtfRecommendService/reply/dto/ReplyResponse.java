package EtfRecommendService.reply.dto;

import lombok.Builder;

import java.time.LocalDateTime;
@Builder
public record ReplyResponse(
        Long id,
        Long userId,
        String nickName,
        String content,
        int likesCount,
        LocalDateTime createdAt
) {
}
