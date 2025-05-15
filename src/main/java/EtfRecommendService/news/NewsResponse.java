package EtfRecommendService.news;

public record NewsResponse(
        Long id,
        String newsTitle,
        String newsLink,
        String imageUrl

) {
}
