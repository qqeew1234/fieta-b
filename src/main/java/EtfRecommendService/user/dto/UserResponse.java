package EtfRecommendService.user.dto;

public record UserResponse(
        Long id,
        String loginId,
        String nickname,
        Boolean isLikePrivate) {
}
