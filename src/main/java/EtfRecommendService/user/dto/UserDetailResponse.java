package EtfRecommendService.user.dto;

import java.time.LocalDateTime;

public record UserDetailResponse(
        Long id,
        String loginId,
        String nickName,
        String imageUrl,
        Boolean isLikePrivate,
        LocalDateTime createdAt
        ) {
}
