package fieta.user.dto;

public record UserCreateRequest(
        String loginId,
        String password,
        String nickname,
        Boolean isLikePrivate) {
}
