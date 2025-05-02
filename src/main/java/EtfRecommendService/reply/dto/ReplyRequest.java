package EtfRecommendService.reply.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReplyRequest(
        @NotNull
        Long commentId,
        @NotNull
        @Size(max = 1000, min = 2)
        String content
) {
}
