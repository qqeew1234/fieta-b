package EtfRecommendService.user.dto;

import java.time.LocalDateTime;

public record UserDetailResponse(
        Long id,
        String loginId,
        String nickname,
        String imageUrl,
        Boolean isLikePrivate,
        LocalDateTime createdAt
        ) {
}
