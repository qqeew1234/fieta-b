package EtfRecommendService.ai.dto;

import EtfRecommendService.etf.dto.EtfReadResponse;

import java.util.List;

public record RecommendationResponseDTO(
        String status,
        RecommendationDTO recommendation,
        List<EtfReadResponse> etfs
) {
}
