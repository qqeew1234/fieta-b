package fieta.user.dto;

import lombok.Builder;

@Builder
public record UserUpdateRequest(String nickname, Boolean isLikePrivate) {
}
