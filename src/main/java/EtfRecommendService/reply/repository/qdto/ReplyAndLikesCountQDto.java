package EtfRecommendService.reply.repository.qdto;

import EtfRecommendService.reply.domain.Reply;

public record ReplyAndLikesCountQDto(
        Reply reply,
        int likesCount
) {
}
