package fieta.etf.dto;

import lombok.Builder;

@Builder
public record EtfReadResponse(
        Long etfId,
        String etfName,
        Double weeklyReturn,
        String etfCode
) {
}
