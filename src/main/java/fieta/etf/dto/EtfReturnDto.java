package fieta.etf.dto;

import fieta.etf.Theme;

public record EtfReturnDto(
        Long etfId,
        String etfName,
        String etfCode,
        Theme theme,
        double returnRate
) {
}
