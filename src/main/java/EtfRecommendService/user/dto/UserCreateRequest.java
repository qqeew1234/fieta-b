package EtfRecommendService.user.dto;

import EtfRecommendService.user.Password;

public record UserCreateRequest(
        String loginId,
        String password,
        String nickname,
        Boolean isLikePrivate) {
}
