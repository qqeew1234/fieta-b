package EtfRecommendService.etf;

import EtfRecommendService.etf.dto.*;
import EtfRecommendService.webSocket.CsvLoader;
import EtfRecommendService.webSocket.WebSocketConnectionService;
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

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class EtfRestController {

    private final EtfService etfService;
    private final WebSocketConnectionService webSocketConnectionService;
    private final CsvLoader csvLoader;


    @GetMapping("/etfs")
    public ResponseEntity<EtfResponse> read(@RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "20") int size,
                                            @RequestParam(required = false) Theme theme,
                                            @RequestParam(required = false, defaultValue = "") String keyword,
                                            @RequestParam(defaultValue = "weekly") String period) {
        Pageable pageable = PageRequest.of(page - 1, size);
        EtfResponse etfResponse = etfService.readAll(theme, keyword, pageable, period);
        return ResponseEntity.status(HttpStatus.OK).body(etfResponse);
    }

    //페이징 없는 전체 조회용. 주간 수익률 반환.
    @GetMapping("/etfs/search")
    public ResponseEntity<EtfAllResponse> readAll(
            @RequestParam(required = false) Theme theme,
            @RequestParam(defaultValue = "") String keyword) {

        EtfAllResponse etfAllResponse = etfService.searchAll(theme, keyword);

        return ResponseEntity.status(HttpStatus.OK).body(etfAllResponse);
    }

    @GetMapping("/etfs/{etfId}")
    public ResponseEntity<EtfDetailResponse> findById(@PathVariable Long etfId) {
        EtfDetailResponse etfDetailResponse = etfService.findById(etfId);
        return ResponseEntity.status(HttpStatus.OK).body(etfDetailResponse);
    }

    @Secured("ROLE_USER")
    @PostMapping("/etfs/{etfId}/subscription")
    public ResponseEntity<SubscribeResponse> create(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long etfId) {
        SubscribeResponse subscribeResponse = etfService.subscribe(userDetails.getUsername(), etfId);
        return ResponseEntity.status(HttpStatus.CREATED).body(subscribeResponse);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/etfs/subscribes")
    public ResponseEntity<SubscribeListResponse> subscribeReadAll(@AuthenticationPrincipal UserDetails userDetails,
                                                                  @RequestParam(defaultValue = "1") int page,
                                                                  @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        SubscribeListResponse subscribeListResponse = etfService.subscribeReadAll(pageable, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(subscribeListResponse);
    }

    @Secured("ROLE_USER")
    @DeleteMapping("/etfs/{etfId}/subscription")
    public ResponseEntity<SubscribeDeleteResponse> delete(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long etfId) {
        SubscribeDeleteResponse subscribeDeleteResponse = etfService.unsubscribe(userDetails.getUsername(), etfId);
        return ResponseEntity.status(HttpStatus.OK).body(subscribeDeleteResponse);
    }

    @GetMapping("/etfs/recommend")
    public ResponseEntity<EtfReadResponse> readTopByThemeOrderByWeeklyReturn(@RequestParam String theme){
        return ResponseEntity.ok(etfService.findTopByThemeOrderByWeeklyReturn(Theme.fromDisplayName(theme)));
    }
    //웹소켓
    //어떤 종목코드를 구독할지
    @GetMapping("/stocks")
    public List<String> getCodes(@RequestParam int page, @RequestParam int size) {
        var all = csvLoader.getCodes();
        return all.subList(page * size, Math.min(all.size(), (page + 1) * size));
    }

    //종목 수 반환
    @GetMapping("/stocks/count")
    public int getTotalStockCount() {
        return csvLoader.getCount();
    }

    //프론트에서 페이지가 바뀔 때마다 호출
    //백엔드가 KIS API에 SUBSCRIBE 요청을 다시 보내도록
    @PostMapping("/stocks/subscribe")
    public ResponseEntity<Void> subscribeStocks(@RequestBody List<String> stockCodes) {
        webSocketConnectionService.subscribeNewKeys(stockCodes);
        return ResponseEntity.ok().build();
    }
}
