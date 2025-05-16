package EtfRecommendService.reply.controller;

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
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/replies")
public class ReplyController {
    private final ReplyService replyService;

    @Secured("ROLE_USER")
    @PostMapping
    public ResponseEntity<String> createReply(@AuthenticationPrincipal UserDetails userDetails, @RequestBody@Valid ReplyRequest rq){
        replyService.create(userDetails.getUsername(), rq);
        return ResponseEntity.status(HttpStatus.CREATED).body("Reply Create Successfully");
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{commentId}")
    public ResponseEntity<RepliesPageList> readAllReplies(@AuthenticationPrincipal UserDetails userDetails,
                                                          @PathVariable Long commentId,
                                                          @RequestParam(required = false, defaultValue = "1") int page,
                                                          @RequestParam(required = false, defaultValue = "10") int size,
                                                          @RequestParam(required = false, defaultValue = "createdAt,desc") String sort){

        Pageable pageable = createPageable(page,size,sort);
        RepliesPageList repliesPageList = replyService.readAll(userDetails.getUsername(), commentId, pageable);
        return ResponseEntity.ok(repliesPageList);
    }

    @Secured("ROLE_USER")
    @PutMapping("/{replyId}")
    public ResponseEntity<String> updateReply(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long replyId, @RequestBody@Valid ReplyRequest rq){
        replyService.update(userDetails.getUsername(), replyId, rq);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Reply update successfully");
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @DeleteMapping("/{replyId}")
    public ResponseEntity<String> deleteReply(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long replyId){
        replyService.delete(userDetails.getUsername(), replyId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Reply delete successfully");
    }

    @Secured("ROLE_USER")
    @PostMapping("/{replyId}/likes")
    public void toggleLike(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long replyId) {
        replyService.toggleLike(userDetails.getUsername(), replyId);
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
