package fieta.user.dto;

public record UserResponse(
        Long id,
        String loginId,
        String nickname,
        Boolean isLikePrivate) {
}
