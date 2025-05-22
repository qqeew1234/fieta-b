package EtfRecommendService.etf;

import EtfRecommendService.etf.domain.Etf;
import EtfRecommendService.etf.domain.EtfProjection;
import EtfRecommendService.etf.domain.Subscribe;
import EtfRecommendService.etf.dto.*;
import EtfRecommendService.user.User;
import EtfRecommendService.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EtfService {

    private final EtfRepository etfRepository;
    private final UserRepository userRepository;
    private final SubscribeRepository subscribeRepository;
    private final EtfQueryRepository etfQueryRepository;

    public EtfService(EtfRepository etfRepository, UserRepository userRepository, SubscribeRepository subscribeRepository, EtfQueryRepository etfQueryRepository) {
        this.etfRepository = etfRepository;
        this.userRepository = userRepository;
        this.subscribeRepository = subscribeRepository;
        this.etfQueryRepository = etfQueryRepository;
    }

    //페이징 있는 전체 etf 조회. 기본값은 주간 수익률 반환.
    public EtfResponse readAll(
            Theme theme,
            String keyword,
            Pageable pageable,
            String period
    ) {
        long totalCount = etfQueryRepository.fetchTotalCount(theme, keyword);
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber() + 1; // 0-based index이므로 +1
        int totalPage = (int) Math.ceil((double) totalCount / pageSize);

        List<EtfProjection> etfs = etfQueryRepository.findEtfsByPeriod(theme, keyword, pageable);

        List<EtfReturnDto> dtoList = etfs.stream()
                .map(etf -> new EtfReturnDto(
                        etf.getId(),
                        etf.getEtfName(),
                        etf.getEtfCode(),
                        etf.getTheme(),
                        "monthly".equalsIgnoreCase(period)
                                ? etf.getMonthlyReturn()
                                : etf.getWeeklyReturn()
                ))
                .collect(Collectors.toList());

        return new EtfResponse(
                totalPage,
                totalCount,
                currentPage,
                pageSize,
                dtoList
        );
    }

    //페이징 없는 전체 조회용. 주간 수익률 반환.
    public EtfAllResponse searchAll(Theme theme, String keyword) {
        long totalCount = etfQueryRepository.fetchTotalCount(theme, keyword);
        List<EtfProjection> etfProjections = etfQueryRepository.findEtfsByKeyword(theme, keyword);

        List<EtfReturnDto> etfReturnDtos = etfProjections.stream()
                .map(etf -> new EtfReturnDto(
                        etf.getId(),
                        etf.getEtfName(),
                        etf.getEtfCode(),
                        etf.getTheme(),
                        etf.getWeeklyReturn()))
                .collect(Collectors.toList());

        return new EtfAllResponse(totalCount, etfReturnDtos);
    }

    public EtfDetailResponse findById(Long etfId) {
        Etf etf = etfRepository.findById(etfId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 etf"));

        return new EtfDetailResponse(
                etf.getId(),
                etf.getEtfName(),
                etf.getEtfCode(),
                etf.getCompanyName(),
                etf.getListingDate()
        );
    }

    //회원 맞는지 확인 - 이미 구독된 종목인지 확인 - 구독
    @Transactional
    public SubscribeResponse subscribe(String memberLoginId, Long etfId) {
        User user = userRepository.findByLoginIdAndIsDeletedFalse(memberLoginId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원"));
        Etf etf = etfRepository.findById(etfId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 etf"));

        //중복 구독 확인
        if (subscribeRepository.existsByUserAndEtfId(user, etfId)) {
            Subscribe subscribe = subscribeRepository.findByUserAndEtfId(user, etfId)
                    .orElseThrow(() -> new IllegalArgumentException("구독하지 않은 etf"));
            subscribeRepository.delete(subscribe);
        }

        Subscribe subscribe = Subscribe.builder()
                .user(user)
                .etf(etf)
                .startTime(LocalDateTime.now())
                .expiredTime(LocalDateTime.now().plusMonths(1))
                .build();
        subscribeRepository.save(subscribe);

        return new SubscribeResponse(
                subscribe.getId(),
                etf.getId(),
                subscribe.getStartTime(),
                subscribe.getExpiredTime()
        );
    }

    //    @Transactional(readOnly = true)
    public SubscribeListResponse subscribeReadAll(Pageable pageable, String memberLoginId) {
        User user = userRepository.findByLoginIdAndIsDeletedFalse(memberLoginId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원"));

        Page<Subscribe> subscribePage = subscribeRepository.findByUser(user, pageable);

        List<SubscribeResponse> subscribeDTOs = subscribePage.stream()
                .map(sub -> new SubscribeResponse(
                        sub.getId(),
                        sub.getEtf().getId(),
                        sub.getStartTime(),
                        sub.getExpiredTime()
                )).toList();

        return new SubscribeListResponse(
                subscribePage.getTotalPages(),
                subscribePage.getTotalElements(),
                subscribePage.getNumber() + 1,
                subscribePage.getSize(),
                subscribeDTOs
        );
    }

    //로그인 확인 - 취소하려는 etfid가 구독이 돼있던건지 확인 후 구독 취소
    @Transactional
    public SubscribeDeleteResponse unsubscribe(String memberLoginId, Long etfId) {
        User user = userRepository.findByLoginIdAndIsDeletedFalse(memberLoginId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원"));

        //유저가 해당 etf 구독하고 있는지 확인
        Subscribe subscribe = subscribeRepository.findByUserAndEtfId(user, etfId)
                .orElseThrow(() -> new IllegalArgumentException("구독하지 않은 etf"));

        subscribeRepository.delete(subscribe);

        return new SubscribeDeleteResponse(etfId);
    }


    public EtfReadResponse findTopByThemeOrderByWeeklyReturn(Theme theme) {
        EtfProjection etfProjection = etfQueryRepository.findTopByThemeOrderByWeeklyReturn(theme);
        if (etfProjection == null) {
            throw new IllegalArgumentException("No ETF found for the given theme: " + theme);
        }
        return EtfReadResponse.builder()
                .etfId(etfProjection.getId())
                .etfName(etfProjection.getEtfName())
                .weeklyReturn(etfProjection.getWeeklyReturn())
                .etfCode(etfProjection.getEtfCode())
                .build();
    }
}
