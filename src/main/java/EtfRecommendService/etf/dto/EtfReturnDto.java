package EtfRecommendService.etf.dto;

import EtfRecommendService.etf.Theme;

public record EtfReturnDto(
        String etfName,
        String etfCode,
        Theme theme,
        double returnRate
) {
}
