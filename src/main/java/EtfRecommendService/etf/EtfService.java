package EtfRecommendService.etf;

import EtfRecommendService.etf.domain.Etf;
import EtfRecommendService.etf.domain.Subscribe;
import EtfRecommendService.etf.dto.*;
import EtfRecommendService.user.User;
import EtfRecommendService.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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

    //etf 전체 조회 (테마별, 순위별)
    public EtfResponse readAll(Pageable pageable, Theme theme, String keyword, SortOrder sortOrder) {
        Page<Etf> etfs = etfQueryRepository.findAllByThemeAndSort(theme, sortOrder,keyword,pageable);

        List<EtfReadResponse> etfReadResponseList = etfs.getContent()
                .stream()
                .map(etfList -> new EtfReadResponse(
                        etfList.getId(),
                        etfList.getEtfName(),
                        etfList.getEtfCode()
                ))
                .toList();

        return new EtfResponse(
                etfs.getTotalPages(),
                etfs.getTotalElements(),
                etfs.getNumber()+1,
                etfs.getSize(),
                etfReadResponseList
        );
    }

    //etf상세조회
    public EtfDetailResponse findById(Long etfId) {
        Etf etf = etfRepository.findById(etfId)
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 etf"));

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
        User user = userRepository.findByLoginId(memberLoginId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원"));

        Etf etf = etfRepository.findById(etfId)
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 etf"));

        //중복 구독 확인
        if (subscribeRepository.existsByUserAndEtfId(user,etfId)){
            throw new IllegalStateException("이미 구독한 etf");
        }

        Subscribe subscribe = Subscribe.builder()
//                .user(user)
                .etf(etf)
                .startTime(LocalDateTime.now())
                .expiredTime(LocalDateTime.now().plusMonths(1))
                .build();
        subscribeRepository.save(subscribe);

        return new  SubscribeResponse(
                subscribe.getId(),
                etf.getId(),
                subscribe.getStartTime(),
                subscribe.getExpiredTime()
                );
    }

//    @Transactional(readOnly = true)
    public SubscribeListResponse subscribeReadAll(Pageable pageable, String memberLoginId) {
        User user = userRepository.findByLoginId(memberLoginId)
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
                subscribePage.getNumber()+1,
                subscribePage.getSize(),
                subscribeDTOs
        );
    }

    //로그인 확인 - 취소하려는 etfid가 구독이 돼있던건지 확인 후 구독 취소
    @Transactional
    public SubscribeDeleteResponse unsubscribe(String memberLoginId, Long etfId) {
        User user = userRepository.findByLoginId(memberLoginId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원"));

        //유저가 해당 etf 구독하고 있는지 확인
        Subscribe subscribe = subscribeRepository.findByUserAndEtfId(user, etfId)
                .orElseThrow(()-> new IllegalArgumentException("구독하지 않은 etf"));

        subscribeRepository.delete(subscribe);

        return new SubscribeDeleteResponse(etfId);
    }
}
