package EtfRecommendService.etf;

import EtfRecommendService.TestConfig;
import EtfRecommendService.etf.domain.Etf;
import EtfRecommendService.etf.domain.EtfProjection;
import EtfRecommendService.etf.dto.EtfResponse;
import EtfRecommendService.etf.dto.EtfReturnDto;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class EtfQueryRepositoryTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private EtfService etfService;

    @Autowired
    private EtfQueryRepository etfQueryRepository;

    @BeforeEach
    void setUp() {
        loadTestData();
    }

    @Transactional
    void loadTestData() {
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
    @DisplayName("조건 없이 모든 주간 ETF 조회")
    void findAllWeeklyWithoutFilter() {
        Pageable pageable = PageRequest.of(0, 10);
        EtfResponse response = etfService.readAll(null, null, pageable, "weekly");

        assertThat(response).isNotNull();
        assertThat(response.etfReadResponseList()).hasSize(5)
                .extracting("etfName")
                .containsExactlyInAnyOrder(
                        "삼성전자 ETF",
                        "SK하이닉스 ETF",
                        "현대차 ETF",
                        "기아차 ETF",
                        "KB금융 ETF"
                );
        assertThat(response.totalCount()).isEqualTo(5);
        assertThat(response.totalPage()).isEqualTo(1);
        assertThat(response.currentPage()).isEqualTo(1);
        assertThat(response.pageSize()).isEqualTo(10);
    }

    @Test
    @DisplayName("조건 없이 모든 월간 ETF 조회")
    void findAllMonthlyWithoutFilter() {
        Pageable pageable = PageRequest.of(0, 10);
        EtfResponse response = etfService.readAll(null, null, pageable, "monthly");

        assertThat(response.etfReadResponseList()).hasSize(5)
                .extracting("etfCode")
                .containsExactlyInAnyOrder(
                        "005930", "000660", "005380", "000270", "105560"
                );
        assertThat(response.totalCount()).isEqualTo(5);
    }

    @Test
    @DisplayName("주간 ETF 데이터를 테마별로 조회 가능")
    void findWeeklyEtfsByTheme() {
        Pageable pageable = PageRequest.of(0, 10);
        EtfResponse response = etfService.readAll(Theme.AI_DATA, null, pageable, "weekly");

        assertThat(response.etfReadResponseList()).hasSize(2)
                .extracting("theme").containsOnly(Theme.AI_DATA);
        assertThat(response.etfReadResponseList()).extracting("etfName")
                .containsExactlyInAnyOrder("삼성전자 ETF", "SK하이닉스 ETF");
        assertThat(response.totalCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("월간 ETF 데이터를 키워드로 검색 가능")
    void findMonthlyEtfsByKeyword() {
        Pageable pageable = PageRequest.of(0, 10);
        EtfResponse response = etfService.readAll(null, "기아", pageable, "monthly");

        assertThat(response.etfReadResponseList()).hasSize(1)
                .extracting("etfCode").containsOnly("000270");
        assertThat(response.totalCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("전체 개수를 정확히 가져옴")
    void fetchTotalCount() {
        long total = etfQueryRepository.fetchTotalCount(null, null);
        long itCnt = etfQueryRepository.fetchTotalCount(Theme.AI_DATA, null);
        long kiaCnt = etfQueryRepository.fetchTotalCount(null, "기아");

        assertThat(total).isEqualTo(5);
        assertThat(itCnt).isEqualTo(2);
        assertThat(kiaCnt).isEqualTo(1);
    }

    @Test
    @DisplayName("페이지네이션 제대로 동작 테스트")
    void paginationTest() {
        Pageable p1 = PageRequest.of(0, 2);
        Pageable p2 = PageRequest.of(1, 2);

        EtfResponse first = etfService.readAll(null, null, p1, "monthly");
        EtfResponse second = etfService.readAll(null, null, p2, "monthly");

        assertThat(first.etfReadResponseList()).hasSize(2);
        assertThat(second.etfReadResponseList()).hasSize(2);
        assertThat(first.etfReadResponseList()).extracting("etfCode")
                .doesNotContainAnyElementsOf(
                        second.etfReadResponseList().stream()
                                .map(EtfReturnDto::etfCode)
                                .toList()
                );
        assertThat(first.totalCount()).isEqualTo(5);
        assertThat(second.totalCount()).isEqualTo(5);
    }

    @Test
    @DisplayName("키워드로 주간 ETF 검색")
    void testFindWeeklyByKeyword() {
        Pageable pageable = PageRequest.of(0, 10);
        EtfResponse response = etfService.readAll(null, "현대차", pageable, "weekly");

        assertThat(response.etfReadResponseList()).hasSize(1)
                .extracting("etfCode").containsOnly("005380");
        assertThat(response.totalCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("테마별 월간 ETF 검색")
    void testFindMonthlyByTheme() {
        Pageable pageable = PageRequest.of(0, 10);
        EtfResponse response = etfService.readAll(Theme.GOLD, null, pageable, "monthly");

        assertThat(response.etfReadResponseList()).hasSize(2)
                .extracting("etfName")
                .containsExactlyInAnyOrder("현대차 ETF", "기아차 ETF");
        assertThat(response.totalCount()).isEqualTo(2);
    }
}