package fieta.report.repository;

import fieta.report.domain.ReplyReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReplyReportRepository extends JpaRepository<ReplyReport, Long> {
    long countByReplyId(Long aLong);

    List<ReplyReport> findByIsCheckedFalse();
}
