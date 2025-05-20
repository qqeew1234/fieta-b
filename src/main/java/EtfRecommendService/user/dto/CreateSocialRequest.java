package EtfRecommendService.user.dto;

public record CreateSocialRequest(
        String email,
        String nickname,
        Boolean isLikePrivate
) {
}
