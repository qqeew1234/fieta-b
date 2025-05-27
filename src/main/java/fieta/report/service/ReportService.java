package fieta.report.service;

import fieta.admin.AdminRepository;
import fieta.comment.domain.Comment;
import fieta.comment.repository.CommentRepository;
import fieta.notification.NotificationService;
import fieta.reply.domain.Reply;
import fieta.reply.repository.ReplyRepository;
import fieta.report.domain.ReportType;
import fieta.report.domain.CommentReport;
import fieta.report.domain.ReplyReport;
import fieta.report.domain.ReportReason;
import fieta.report.dto.CommentReportResponse;
import fieta.report.dto.ReplyReportResponse;
import fieta.report.dto.ReportListResponse;
import fieta.report.dto.ReportRequest;
import fieta.report.repository.CommentReportRepository;
import fieta.report.repository.ReplyReportRepository;
import fieta.user.User;
import fieta.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ReportService {
    private final CommentReportRepository commentReportRepository;
    private final ReplyReportRepository replyReportRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final NotificationService notificationService;
    private final AdminRepository adminRepository;

    @Value("${report.limit:100}") // 환경에서 주입
    private int reportLimit;

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

    private void checkReportLimit(Long contentId, ReportType type, long reportedCount) {
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
