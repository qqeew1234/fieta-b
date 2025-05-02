package EtfRecommendService.user.dto;

import EtfRecommendService.user.Password;

public record UserCreateRequest(
        String loginId,
        Password password,
        String nickName,
        Boolean isLikePrivate) {
}
