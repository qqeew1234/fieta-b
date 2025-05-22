package EtfRecommendService.etf.dto;

import java.util.List;

public record EtfAllResponse(
        Long totalCount,
        List<EtfReturnDto> etfReadResponseList
) {
}
