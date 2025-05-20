package EtfRecommendService.article;

import java.time.LocalDateTime;

public record ArticleResponse(
        Long id,
        String title,
        String sourceUrl,
        String thumbnailUrl,
        LocalDateTime publishedAt
) {
}
