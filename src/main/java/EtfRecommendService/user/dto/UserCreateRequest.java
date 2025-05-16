package EtfRecommendService.user.dto;

import EtfRecommendService.user.Password;

public record UserCreateRequest(
        String loginId,
        String password,
        String nickName,
        Boolean isLikePrivate) {
}
