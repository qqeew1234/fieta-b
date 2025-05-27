package fieta.report.controller;

import fieta.report.dto.ReportListResponse;
import fieta.report.dto.ReportRequest;
import fieta.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/reports")
public class ReportController {
    private final ReportService reportService;

    @Secured("ROLE_USER")
    @PostMapping
    public ResponseEntity<String> createReport(@AuthenticationPrincipal UserDetails userDetails, @RequestBody ReportRequest rq){
        reportService.create(userDetails.getUsername(), rq);
        return ResponseEntity.status(HttpStatus.CREATED).body("Reply was Reported");
    }

    @Secured("ROLE_ADMIN")
    //전체 신고 목록 조회
    @GetMapping
    public ResponseEntity<ReportListResponse> readAllReports(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(reportService.readAll(userDetails.getUsername()));
    }
}
