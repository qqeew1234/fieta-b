package Etf.user.dto;

public record UserResponse(
        Long id,
        String loginId,
        String nickName,
        Boolean isLikePrivate) {
}
