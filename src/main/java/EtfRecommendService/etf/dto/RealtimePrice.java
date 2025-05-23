package EtfRecommendService.etf.dto;

public record RealtimePrice(
        String etfCode,
        int price,
        double dayOverDayRate,
        long volume
) {
}
