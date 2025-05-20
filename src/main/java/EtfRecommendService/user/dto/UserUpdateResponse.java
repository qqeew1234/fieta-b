package EtfRecommendService.user.dto;

public record UserUpdateResponse(Long id, String nickname, String imageUrl, Boolean isLikePrivate) {
}
