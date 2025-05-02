package EtfRecommendService.report.controller;

import EtfRecommendService.loginUtils.LoginMember;
import EtfRecommendService.report.dto.ReportListResponse;
import EtfRecommendService.report.dto.ReportRequest;
import EtfRecommendService.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/")
public class ReportController {
    private final ReportService reportService;

    @PostMapping("user/reports")
    public ResponseEntity<String> createReport(@LoginMember String loginId, @RequestBody ReportRequest rq){
        reportService.create(loginId, rq);
        return ResponseEntity.status(HttpStatus.CREATED).body("Reply was Reported");
    }

    //전체 신고 목록 조회
    @GetMapping("admin/reports")
    public ResponseEntity<ReportListResponse> readAllReports(@LoginMember String loginId) {
        return ResponseEntity.ok(reportService.readAll(loginId));
    }
}
