package EtfRecommendService.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CommentCreateRequest(

        @NotNull(message = "ETF ID는 필수입니다.")
        Long etfId,

        @NotBlank(message = "댓글 내용을 입력해 주세요.")
        String content


) {
}
