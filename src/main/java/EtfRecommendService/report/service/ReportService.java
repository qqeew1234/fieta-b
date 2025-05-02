package EtfRecommendService.report.service;

import EtfRecommendService.admin.Admin;
import EtfRecommendService.admin.AdminRepository;
import EtfRecommendService.comment.domain.Comment;
import EtfRecommendService.comment.repository.CommentRepository;
import EtfRecommendService.notification.NotificationService;
import EtfRecommendService.reply.domain.Reply;
import EtfRecommendService.reply.repository.ReplyRepository;
import EtfRecommendService.report.domain.ReportType;
import EtfRecommendService.report.domain.CommentReport;
import EtfRecommendService.report.domain.ReplyReport;
import EtfRecommendService.report.domain.ReportReason;
import EtfRecommendService.report.dto.CommentReportResponse;
import EtfRecommendService.report.dto.ReplyReportResponse;
import EtfRecommendService.report.dto.ReportListResponse;
import EtfRecommendService.report.dto.ReportRequest;
import EtfRecommendService.report.repository.CommentReportRepository;
import EtfRecommendService.report.repository.ReplyReportRepository;
import EtfRecommendService.user.User;
import EtfRecommendService.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReportService {
    private final CommentReportRepository commentReportRepository;
    private final ReplyReportRepository replyReportRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final NotificationService notificationService;
    private final int reportLimit;
    private final AdminRepository adminRepository;

    @Transactional
    public void create(String loginId, ReportRequest rq) {
        User user = userRepository.findByLoginIdAndIsDeletedFalse(loginId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (rq.commentId() != null) {
            handleCommentReport(user, rq);
        } else {
            handleReplyReport(user, rq);
        }
    }

    private void handleCommentReport(User user, ReportRequest rq) {
        Comment comment = commentRepository.findById(rq.commentId())
                .orElseThrow(() -> new IllegalArgumentException("Comment Not found"));

        CommentReport report = CommentReport.builder()
                .comment(comment)
                .reporter(user)
                .reportReason(ReportReason.toEnum(rq.reportReason()))
                .build();
        report.addReport(comment, user);

        commentReportRepository.save(report);

        checkReportLimit(rq.commentId(), ReportType.COMMENT,
                commentReportRepository.countByCommentId(rq.commentId()));
    }

    private void handleReplyReport(User user, ReportRequest rq) {
        Reply reply = replyRepository.findById(rq.replyId())
                .orElseThrow(() -> new IllegalArgumentException("Reply not found"));

        ReplyReport report = ReplyReport.builder()
                .reporter(user)
                .reply(reply)
                .reportReason(ReportReason.toEnum(rq.reportReason()))
                .build();

        report.addReport(reply, user);
        replyReportRepository.save(report);

        checkReportLimit(rq.replyId(), ReportType.REPLY,
                replyReportRepository.countByReplyId(rq.replyId()));
    }

    private void checkReportLimit(Long contentId, ReportType type, @Value("${report.limit}") long reportedCount) {

        if (reportedCount >= reportLimit) {
            notificationService.notifyIfReportedOverLimit(contentId, type);
        }
    }

    public ReportListResponse readAll(String loginId) {
        List<CommentReportResponse> commentReportResponseList =
                commentReportRepository.findAllByIsCheckedFalse()
                        .stream()
                        .map(CommentReportResponse::toDto)
                        .toList();
        List<ReplyReportResponse> replyReportResponseList =
                replyReportRepository.findByIsCheckedFalse()
                        .stream()
                        .map(ReplyReportResponse::toDto)
                        .toList();

        return ReportListResponse.builder()
                .commentReportResponseList(commentReportResponseList)
                .replyReportResponseList(replyReportResponseList)
                .build();
    }
}
