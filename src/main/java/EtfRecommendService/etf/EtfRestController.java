package EtfRecommendService.etf;

import EtfRecommendService.etf.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class EtfRestController {

    private final EtfService etfService;


    @GetMapping("/etfs")
    public ResponseEntity<EtfResponse> read(@RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "20") int size,
                                            @RequestParam(required = false) Theme theme,
                                            @RequestParam(required = false, defaultValue = "") String keyword,
                                            @RequestParam(defaultValue = "weekly") String period) {
        Pageable pageable = PageRequest.of(page - 1, size);
        EtfResponse etfResponse = etfService.readAll(pageable, theme,keyword, period);
        return ResponseEntity.status(HttpStatus.OK).body(etfResponse);
    }


    @GetMapping("/etfs/{etfId}")
    public ResponseEntity<EtfDetailResponse> findById(@PathVariable Long etfId){
        EtfDetailResponse etfDetailResponse = etfService.findById(etfId);
        return ResponseEntity.status(HttpStatus.OK).body(etfDetailResponse);
    }

    @Secured("ROLE_USER")
    @PostMapping("/etfs/{etfId}/subscription")
    public ResponseEntity<SubscribeResponse> create(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long etfId){
        SubscribeResponse subscribeResponse = etfService.subscribe(userDetails.getUsername(), etfId);
        return ResponseEntity.status(HttpStatus.CREATED).body(subscribeResponse);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/etfs/subscribes")
    public ResponseEntity<SubscribeListResponse> subscribeReadAll(@AuthenticationPrincipal UserDetails userDetails,
                                                  @RequestParam(defaultValue = "1") int page,
                                                  @RequestParam(defaultValue = "20") int size){
        Pageable pageable = PageRequest.of(page - 1, size);
        SubscribeListResponse subscribeListResponse = etfService.subscribeReadAll(pageable, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(subscribeListResponse);
    }

    @Secured("ROLE_USER")
    @DeleteMapping("/etfs/{etfId}/subscription")
    public ResponseEntity<SubscribeDeleteResponse> delete(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long etfId){
        SubscribeDeleteResponse subscribeDeleteResponse = etfService.unsubscribe(userDetails.getUsername(), etfId);
        return ResponseEntity.status(HttpStatus.OK).body(subscribeDeleteResponse);
    }

    @GetMapping("/etfs/recommend")
    public ResponseEntity<EtfReadResponse> readTopByThemeOrderByWeeklyReturn(@RequestParam String theme){
        return ResponseEntity.ok(etfService.findTopByThemeOrderByWeeklyReturn(Theme.fromDisplayName(theme)));
    }
}
