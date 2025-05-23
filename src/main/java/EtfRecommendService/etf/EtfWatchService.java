package EtfRecommendService.etf;

import EtfRecommendService.etf.dto.WatchPriceRequest;
import EtfRecommendService.etf.dto.WatchResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class EtfWatchService {

    // 종목별 구독 사용자 관리 (ETF Code -> Watch IDs)
    private final Map<String, Set<UUID>> etfWatchers = new ConcurrentHashMap<>();

    // 사용자별 구독 종목 관리 (Watch ID -> ETF Codes)
    private final Map<UUID, Set<String>> watcherToEtfs = new ConcurrentHashMap<>();

    public WatchResponse watch(WatchPriceRequest request) {
        UUID watchId = UUID.randomUUID();
        log.info("클라이언트 {}가 종목 {}을(를) 구독합니다", watchId, String.join(", ", request.etfCodes()));

        Set<String> etfCodes = ConcurrentHashMap.newKeySet();
        etfCodes.addAll(request.etfCodes());
        watcherToEtfs.put(watchId, etfCodes);

        // 종목의 구독자 목록에 사용자 추가
        request.etfCodes().forEach(etfCode -> {
            etfWatchers.computeIfAbsent(etfCode, k -> ConcurrentHashMap.newKeySet())
                    .add(watchId);
            log.info("{}의 현재 구독자 수: {}", etfCode, etfWatchers.get(etfCode).size());
        });

        return new WatchResponse(watchId);
    }

    public List<String> unwatch(UUID watchId) {
        if (watchId == null) {
            throw new IllegalArgumentException("watchId는 null일 수 없습니다");
        }

        // watchId가 구독하고 있는 ETF 목록 조회
        Set<String> subscribedEtfs = watcherToEtfs.remove(watchId);

        if (subscribedEtfs == null || subscribedEtfs.isEmpty()) {
            log.warn("watchId {}에 대한 구독 정보가 없습니다", watchId);
            return Collections.emptyList();
        }

        List<String> removedEtfCodes = new ArrayList<>();

        log.info("클라이언트 {}가 {} 종목에서 감시를 해제합니다", watchId, subscribedEtfs.size());

        // 각 ETF에서 해당 watchId 제거
        for (String etfCode : subscribedEtfs) {
            if (removeWatcherFromEtf(etfCode, watchId)) {
                removedEtfCodes.add(etfCode);
            }
        }

        log.info("watchId {}에서 {} 종목의 감시가 해제되었습니다: {}",
                watchId, removedEtfCodes.size(), String.join(", ", removedEtfCodes));

        return Collections.unmodifiableList(removedEtfCodes);
    }

    private boolean removeWatcherFromEtf(String etfCode, UUID watchId) {
        Set<UUID> watchers = etfWatchers.get(etfCode);
        if (watchers != null && watchers.remove(watchId)) {
            // 감시자가 없으면 메모리에서 제거
            if (watchers.isEmpty()) {
                etfWatchers.remove(etfCode);
                log.debug("ETF {} 감시자 목록이 비어서 제거되었습니다", etfCode);
            } else {
                log.debug("{}의 현재 감시자 수: {}", etfCode, watchers.size());
            }
            return true;
        }
        return false;
    }

    public boolean isWatched(String etfCode) {
        Set<UUID> watchers = etfWatchers.get(etfCode);
        return watchers != null && !watchers.isEmpty();
    }
}
