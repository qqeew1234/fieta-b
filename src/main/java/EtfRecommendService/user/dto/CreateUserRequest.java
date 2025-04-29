package EtfRecommendService.user.dto;

import EtfRecommendService.user.Password;

public record CreateUserRequest(
        String loginId,
        Password password,
        String nickName,
        Boolean isLikePrivate) {
}
