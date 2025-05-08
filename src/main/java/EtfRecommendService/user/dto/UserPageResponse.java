package EtfRecommendService.user.dto;

import java.util.List;

public record UserPageResponse(
        int page,
        int size,
        long totalElements,
        long totalPages,
        List<getUserCommentsAndReplies> commentsAndReplies) {
}
