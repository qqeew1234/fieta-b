package EtfRecommendService.comment.dto;

public record ToggleLikeResponse(
        Long commentId,
        Boolean liked,
        Long likesCount
) {
}
