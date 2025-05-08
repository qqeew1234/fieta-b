package EtfRecommendService.user.dto;

public record UserDetailResponse(
        Long id,
        String loginId,
        String nickName,
        String imageUrl,
        Boolean isLikePrivate) {
}
