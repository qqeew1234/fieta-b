package fieta.ai.dto;

import fieta.etf.dto.EtfReadResponse;

import java.util.List;

public record RecommendationResponseDTO(
        String status,
        RecommendationDTO recommendation,
        List<EtfReadResponse> etfs
) {
}
