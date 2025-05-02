package EtfRecommendService.reply.controller;

import EtfRecommendService.loginUtils.LoginMember;
import EtfRecommendService.reply.dto.RepliesPageList;
import EtfRecommendService.reply.dto.ReplyRequest;
import EtfRecommendService.reply.service.ReplyService;
import jakarta.validation.Valid;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user/replies")
public class ReplyController {
    private final ReplyService replyService;

    @PostMapping
    public ResponseEntity<String> createReply(@LoginMember String loginId, @RequestBody@Valid ReplyRequest rq){
        replyService.create(loginId, rq);
        return ResponseEntity.status(HttpStatus.CREATED).body("Reply Create Successfully");
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<RepliesPageList> readAllReplies(@LoginMember String loginId,
                                                          @PathVariable Long commentId,
                                                          @RequestParam(required = false, defaultValue = "1") int page,
                                                          @RequestParam(required = false, defaultValue = "10") int size,
                                                          @RequestParam(required = false, defaultValue = "createdAt,desc") String sort){

        Pageable pageable = createPageable(page,size,sort);
        RepliesPageList repliesPageList = replyService.readAll(loginId, commentId, pageable);
        return ResponseEntity.ok(repliesPageList);
    }

    @PutMapping("/{replyId}")
    public ResponseEntity<String> updateReply(@LoginMember String loginId, @PathVariable Long replyId, @RequestBody@Valid ReplyRequest rq){
        replyService.update(loginId, replyId, rq);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Reply update successfully");
    }

    @DeleteMapping("/{replyId}")
    public ResponseEntity<String> deleteReply(@LoginMember String loginId, @PathVariable Long replyId){
        replyService.delete(loginId, replyId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Reply delete successfully");
    }

    @PostMapping("/{replyId}/likes")
    public void toggleLike(
            @LoginMember String loginId,
            @PathVariable Long replyId) {
        replyService.toggleLike(loginId, replyId);
    }

    private Pageable createPageable(int page, int size,String sort){
        String[] orderAndDirection = sort.split(",");
        Sort sortObj;
        if (orderAndDirection.length > 1) {
            Sort.Direction direction = Sort.Direction.fromString(orderAndDirection[1]);
            sortObj = Sort.by(direction, orderAndDirection[0]);
        } else {
            sortObj = Sort.by(Sort.Direction.DESC, orderAndDirection[0]);
        }
        return PageRequest.of(page-1, size,sortObj);
    }
}
