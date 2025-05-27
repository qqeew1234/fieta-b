package fieta.reply.repository.qdto;

import lombok.Builder;

import java.util.List;
@Builder
public record SortedRepliesQDto(
        List<ReplyAndLikesCountQDto> replyAndLikesCountQDtoList,
        Long totalElements
) {
}
