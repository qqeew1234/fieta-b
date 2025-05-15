package EtfRecommendService.etf.dto;

import EtfRecommendService.etf.Theme;

public record EtfReturnDto(
        Long etfId,
        String etfName,
        String etfCode,
        Theme theme,
        double returnRate
) {
}
