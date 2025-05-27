package fieta.user.dto;

public record CreateUserRequest(
        String loginId,
        String password,
        String nickname,
        Boolean isLikePrivate) {
}
