package EtfRecommendService.user.dto;

public record CreateSocialRequest(
        String email,
        String nickName,
        Boolean isLikePrivate
) {
}
