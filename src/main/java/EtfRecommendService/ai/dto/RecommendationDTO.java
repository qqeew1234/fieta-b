package EtfRecommendService.ai.dto;

import java.util.List;

public record RecommendationDTO(
        String mainRecommendation,
        List<String> subRecommendations,
        String reason
) {
}
