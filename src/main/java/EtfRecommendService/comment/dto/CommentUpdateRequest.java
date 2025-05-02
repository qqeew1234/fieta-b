package EtfRecommendService.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentUpdateRequest(

        @NotBlank(message = "댓글 내용을 입력해 주세요.")
        @Size(min = 2, max = 1000, message = "댓글은 2자 이상 1000자 이하여야 합니다.")
        String content

) {
}
