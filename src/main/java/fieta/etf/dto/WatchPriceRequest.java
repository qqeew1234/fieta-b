package fieta.etf.dto;

import java.util.List;

public record WatchPriceRequest(List<String> etfCodes) {
}
