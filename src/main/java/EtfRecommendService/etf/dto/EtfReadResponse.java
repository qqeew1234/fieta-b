package EtfRecommendService.etf.dto;

public record EtfReadResponse(
        Long etfId,
        String etfName,
        int etfCode
) {
}
