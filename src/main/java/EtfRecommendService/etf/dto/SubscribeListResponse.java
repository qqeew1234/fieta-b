package EtfRecommendService.etf.dto;

import java.util.List;

public record SubscribeListResponse(
        int totalPage,
        Long totalCount,
        int currentPage,
        int pageSize,
        List<SubscribeResponse> subscribeResponseList
) {
}
