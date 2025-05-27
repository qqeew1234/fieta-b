package fieta.report.repository;

import fieta.report.domain.CommentReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentReportRepository extends JpaRepository<CommentReport, Long> {
    long countByCommentId(Long aLong);

    List<CommentReport> findAllByIsCheckedFalse();
}
