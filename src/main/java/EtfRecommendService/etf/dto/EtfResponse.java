package EtfRecommendService.etf.dto;

import java.util.List;

public record EtfResponse(
        int totalPage,
        Long totalCount,
        int currentPage,
        int pageSize,
        List<EtfReturnDto> etfReadResponseList
) {
}
