package fieta.reply.repository.qdto;

import fieta.reply.domain.Reply;

public record ReplyAndLikesCountQDto(
        Reply reply,
        int likesCount
) {
}
