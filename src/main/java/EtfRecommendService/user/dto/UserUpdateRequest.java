package EtfRecommendService.user.dto;

import lombok.Builder;

@Builder
public record UserUpdateRequest(String nickName, Boolean isLikePrivate) {
}
