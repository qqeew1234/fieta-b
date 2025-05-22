package EtfRecommendService.etf;

import EtfRecommendService.etf.domain.Etf;
import EtfRecommendService.etf.domain.EtfProjection;
import EtfRecommendService.etf.dto.EtfResponse;
import EtfRecommendService.etf.dto.EtfReturnDto;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class EtfServiceTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private EtfService etfService;

    @Autowired
    private EtfQueryRepository etfQueryRepository;

    private Pageable pageable;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 20); // 페이지 인덱스 0, 크기 20
        loadTestData();
    }

    @Transactional
    void loadTestData() {
        // ETF 엔티티 생성
        persistEtf("삼성전자 ETF", "005930", "Test Company", LocalDateTime.of(2000, 6, 15, 10, 0), Theme.AI_DATA);
        persistEtfProjection("삼성전자 ETF", "005930", Theme.AI_DATA, 1.2, 3.4, LocalDate.of(2025, 5, 1));

        persistEtf("SK하이닉스 ETF", "000660", "Test Company", LocalDateTime.of(2002, 3, 20, 12, 0), Theme.AI_DATA);
        persistEtfProjection("SK하이닉스 ETF", "000660", Theme.AI_DATA, 1.0, 2.8, LocalDate.of(2025, 5, 1));

        persistEtf("현대차 ETF", "005380", "Test Company", LocalDateTime.of(2005, 9, 10, 14, 0), Theme.GOLD);
        persistEtfProjection("현대차 ETF", "005380", Theme.GOLD, 0.9, 2.1, LocalDate.of(2025, 5, 1));

        persistEtf("기아차 ETF", "000270", "Test Company", LocalDateTime.of(2008, 12, 5, 16, 0), Theme.GOLD);
        persistEtfProjection("기아차 ETF", "000270", Theme.GOLD, 1.1, 2.5, LocalDate.of(2025, 5, 1));

        persistEtf("KB금융 ETF", "105560", "Test Company", LocalDateTime.of(2010, 7, 25, 9, 0), Theme.COMMODITIES);
        persistEtfProjection("KB금융 ETF", "105560", Theme.COMMODITIES, 0.7, 1.9, LocalDate.of(2025, 5, 1));

        em.flush();
        em.clear();
    }

    private void persistEtf(String etfName, String etfCode, String companyName, LocalDateTime listingDate, Theme theme) {
        Etf etf = Etf.builder()
                .etfName(etfName)
                .etfCode(etfCode)
                .companyName(companyName)
                .listingDate(listingDate)
                .theme(theme)
                .build();

        em.persist(etf);
    }

    private void persistEtfProjection(String etfName, String etfCode, Theme theme,
                                      double weeklyReturn, double monthlyReturn, LocalDate date) {
        EtfProjection projection = new EtfProjection(
                null,          // ID는 자동 생성
                etfName,
                etfCode,
                theme,
                weeklyReturn,
                monthlyReturn,
                date
        );
        em.persist(projection);
    }

    @Test
    @DisplayName("기간이 'weekly'인 경우 주간 ETF 목록을 조회")
    void readAll_WithWeeklyPeriod_ReturnsWeeklyEtfResponse() {
        // Given
        Theme theme = null; // 전체 조회
        String keyword = "";
        String period = "weekly";

        // When
        EtfResponse response = etfService.readAll(theme, keyword, pageable, period);

        // Then
        assertThat(response.totalCount()).isEqualTo(5L); // 전체 5개 ETF
        assertThat(response.totalPage()).isEqualTo(1); // 20개 페이지 크기로 1페이지
        assertThat(response.currentPage()).isEqualTo(1);
        assertThat(response.pageSize()).isEqualTo(20);
        assertThat(response.etfReadResponseList()).hasSize(5);

        // 주간 수익률 확인
        EtfReturnDto firstEtf = response.etfReadResponseList().get(0);
        assertThat(firstEtf.returnRate()).isNotNull();
    }

    @Test
    @DisplayName("기간이 'monthly'인 경우 월간 ETF 목록을 조회")
    void readAll_WithMonthlyPeriod_ReturnsMonthlyEtfResponse() {
        // Given
        Theme theme = null; // 전체 조회
        String keyword = "";
        String period = "monthly";

        // When
        EtfResponse response = etfService.readAll(theme, keyword, pageable, period);

        // Then
        assertThat(response.totalCount()).isEqualTo(5L);
        assertThat(response.totalPage()).isEqualTo(1);
        assertThat(response.currentPage()).isEqualTo(1);
        assertThat(response.pageSize()).isEqualTo(20);
        assertThat(response.etfReadResponseList()).hasSize(5);

        // 월간 수익률 확인 (monthly 기간일 때는 monthlyReturn 값이 사용됨)
        EtfReturnDto firstEtf = response.etfReadResponseList().get(0);
        assertThat(firstEtf.returnRate()).isNotNull();
    }

    @Test
    @DisplayName("테마 필터링이 정상적으로 작동")
    void readAll_WithThemeFilter_ReturnsFilteredResults() {
        // Given
        Theme theme = Theme.AI_DATA; // AI_DATA 테마만 조회
        String keyword = "";
        String period = "weekly";

        // When
        EtfResponse response = etfService.readAll(theme, keyword, pageable, period);

        // Then
        assertThat(response.totalCount()).isEqualTo(2L); // AI_DATA 테마는 2개
        assertThat(response.etfReadResponseList()).hasSize(2);
        assertThat(response.etfReadResponseList())
                .allMatch(etf -> etf.theme() == Theme.AI_DATA);
    }

    @Test
    @DisplayName("키워드 검색이 정상적으로 작동")
    void readAll_WithKeywordSearch_ReturnsMatchingResults() {
        // Given
        Theme theme = null;
        String keyword = "삼성"; // 삼성전자 ETF만 조회
        String period = "weekly";

        // When
        EtfResponse response = etfService.readAll(theme, keyword, pageable, period);

        // Then
        assertThat(response.totalCount()).isEqualTo(1L);
        assertThat(response.etfReadResponseList()).hasSize(1);
        assertThat(response.etfReadResponseList().get(0).etfName()).contains("삼성");
    }

    @Test
    @DisplayName("검색 결과가 없는 경우 빈 목록을 반환")
    void readAll_WithNoResults_ReturnsEmptyList() {
        // Given
        Theme theme = null;
        String keyword = "존재하지않는ETF"; // 존재하지 않는 키워드
        String period = "weekly";

        // When
        EtfResponse response = etfService.readAll(theme, keyword, pageable, period);

        // Then
        assertThat(response.totalCount()).isEqualTo(0L);
        assertThat(response.totalPage()).isEqualTo(0);
        assertThat(response.etfReadResponseList()).isEmpty();
    }

    @Test
    @DisplayName("페이징이 정상적으로 작동")
    void readAll_WithPaging_CalculatesTotalPageCorrectly() {
        // Given
        Theme theme = null;
        String keyword = "";
        String period = "weekly";
        Pageable smallPageable = PageRequest.of(0, 2); // 페이지 크기를 2로 설정

        // When
        EtfResponse response = etfService.readAll(theme, keyword, smallPageable, period);

        // Then
        assertThat(response.totalCount()).isEqualTo(5L);
        assertThat(response.totalPage()).isEqualTo(3); // 5개 데이터를 2개씩 나누면 3페이지
        assertThat(response.currentPage()).isEqualTo(1);
        assertThat(response.pageSize()).isEqualTo(2);
        assertThat(response.etfReadResponseList()).hasSize(2); // 첫 페이지에는 2개
    }

    @Test
    @DisplayName("ETF 코드로 검색이 정상적으로 작동")
    void readAll_WithEtfCodeSearch_ReturnsMatchingResults() {
        // Given
        Theme theme = null;
        String keyword = "005930"; // 삼성전자 ETF 코드
        String period = "weekly";

        // When
        EtfResponse response = etfService.readAll(theme, keyword, pageable, period);

        // Then
        assertThat(response.totalCount()).isEqualTo(1L);
        assertThat(response.etfReadResponseList()).hasSize(1);
        assertThat(response.etfReadResponseList().get(0).etfCode()).isEqualTo("005930");
    }

    @Test
    @DisplayName("테마와 키워드를 함께 사용한 필터링이 정상적으로 작동")
    void readAll_WithThemeAndKeyword_ReturnsFilteredResults() {
        // Given
        Theme theme = Theme.GOLD; // GOLD 테마
        String keyword = "현대"; // 현대차 ETF
        String period = "weekly";

        // When
        EtfResponse response = etfService.readAll(theme, keyword, pageable, period);

        // Then
        assertThat(response.totalCount()).isEqualTo(1L);
        assertThat(response.etfReadResponseList()).hasSize(1);
        assertThat(response.etfReadResponseList().get(0).theme()).isEqualTo(Theme.GOLD);
        assertThat(response.etfReadResponseList().get(0).etfName()).contains("현대");
    }
}