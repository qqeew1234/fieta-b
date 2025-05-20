package EtfRecommendService.user.dto;

import EtfRecommendService.user.Password;

public record CreateUserRequest(
        String loginId,
        String password,
        String nickname,
        Boolean isLikePrivate) {
}
