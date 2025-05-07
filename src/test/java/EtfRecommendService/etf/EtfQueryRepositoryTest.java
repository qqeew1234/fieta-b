package EtfRecommendService.etf;

import EtfRecommendService.TestConfig;
import EtfRecommendService.etf.domain.EtfProjection;
import EtfRecommendService.etf.dto.EtfReturnDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Constructor;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestConfig.class)
public class EtfQueryRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private JPAQueryFactory queryFactory;

    private EtfQueryRepository repository;

    @Autowired
    private EntityManager em;

    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    private EtfQueryRepository etfQueryRepository;

    @BeforeEach
    void setUp() {
        repository = new EtfQueryRepository(queryFactory);
        loadTestData();
    }

    @Transactional
    void loadTestData() {
        persistEtf("삼성전자 ETF", "005930", Theme.AI_DATA, 1.5, 3.2);
        persistEtf("SK하이닉스 ETF", "000660", Theme.AI_DATA, 2.1, 4.5);
        persistEtf("현대차 ETF", "005380", Theme.GOLD, -0.5, 1.8);
        persistEtf("기아차 ETF", "000270", Theme.GOLD, 0.8, 2.3);
        persistEtf("KB금융 ETF", "105560", Theme.COMMODITIES, 1.1, 2.2);
        em.flush();
        em.clear();
    }

    private void persistEtf(String name,
                            String code,
                            Theme theme,
                            double weekly,
                            double monthly) {
        try {
            Constructor<EtfProjection> ctor = EtfProjection.class.getDeclaredConstructor();
            ctor.setAccessible(true);

            EtfProjection etf = ctor.newInstance();

            ReflectionTestUtils.setField(etf, "etfName", name);
            ReflectionTestUtils.setField(etf, "etfCode", code);
            ReflectionTestUtils.setField(etf, "theme", theme);
            ReflectionTestUtils.setField(etf, "weeklyReturn", weekly);
            ReflectionTestUtils.setField(etf, "monthlyReturn", monthly);

            em.persist(etf);

        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException("테스트용 EtfReadProjection 생성 실패", ex);
        }
    }

    @Test
    @DisplayName("조건 없이 모든 주간 ETF 조회")
    void findAllWeeklyWithoutFilter() {
        Pageable pageable = PageRequest.of(0, 10);
        List<EtfReturnDto> list = repository.findEtfsByPeriod(null, null, pageable, "weekly");

        assertThat(list).hasSize(5)
                .extracting("etfName")
                .containsExactlyInAnyOrder(
                        "삼성전자 ETF",
                        "SK하이닉스 ETF",
                        "현대차 ETF",
                        "기아차 ETF",
                        "KB금융 ETF"
                );
    }

    @Test
    @DisplayName("조건 없이 모든 월간 ETF 조회")
    void findAllMonthlyWithoutFilter() {
        Pageable pageable = PageRequest.of(0, 10);
        List<EtfReturnDto> list = repository.findEtfsByPeriod(null, null, pageable, "monthly");

        assertThat(list).hasSize(5)
                .extracting("etfCode")
                .containsExactlyInAnyOrder(
                        "005930", "000660", "005380", "000270", "105560"
                );
    }

    @Test
    @DisplayName("주간 ETF 데이터를 테마별로 조회 가능")
    void findWeeklyEtfsByTheme() {
        Pageable pageable = PageRequest.of(0, 10);

        List<EtfReturnDto> itList =
                repository.findEtfsByPeriod(Theme.AI_DATA, null, pageable, "monthly");

        assertThat(itList).hasSize(2)
                .extracting("theme").containsOnly(Theme.AI_DATA);
        assertThat(itList).extracting("etfName")
                .containsExactlyInAnyOrder("삼성전자 ETF", "SK하이닉스 ETF");
    }

    @Test
    @DisplayName("월간 ETF 데이터를 키워드로 검색 가능")
    void findMonthlyEtfsByKeyword() {
        Pageable pageable = PageRequest.of(0, 10);

        List<EtfReturnDto> result =
                repository.findEtfsByPeriod(null, "기아", pageable, "monthly");

        assertThat(result).hasSize(1)
                .extracting("etfCode").containsOnly("000270");
    }

    @Test
    @DisplayName("전체 개수를 정확히 가져옴")
    void fetchTotalCount() {
        long total = repository.fetchTotalCount(null, null);
        long itCnt = repository.fetchTotalCount(Theme.AI_DATA, null);
        long kiaCnt = repository.fetchTotalCount(null, "기아");

        assertThat(total).isEqualTo(5);
        assertThat(itCnt).isEqualTo(2);
        assertThat(kiaCnt).isEqualTo(1);
    }

    @Test
    @DisplayName("페이지네이션 제대로 동작 테스트")
    void paginationTest() {
        Pageable p1 = PageRequest.of(0, 2);
        Pageable p2 = PageRequest.of(1, 2);

        List<EtfReturnDto> first = repository.findEtfsByPeriod(null, null, p1, "monthly");
        List<EtfReturnDto> second = repository.findEtfsByPeriod(null, null, p2, "monthly");

        assertThat(first).hasSize(2);
        assertThat(second).hasSize(2);
        // 첫 페이지와 둘째 페이지는 겹치지 않는다
        assertThat(first).extracting("etfCode")
                .doesNotContainAnyElementsOf(
                        second.stream()
                                .map(EtfReturnDto::etfCode)
                                .toList()
                );
    }

    @Test
    @DisplayName("키워드로 주간 ETF 검색")
    void testFindWeeklyByKeyword() {
        Pageable pageable = PageRequest.of(0, 10);
        List<EtfReturnDto> result = repository.findEtfsByPeriod(null, "현대차", pageable, "weekly");

        assertThat(result).hasSize(1)
                .extracting("etfCode").containsOnly("005380");
    }

    @Test
    @DisplayName("테마별 월간 ETF 검색")
    void testFindMonthlyByTheme() {
        Pageable pageable = PageRequest.of(0, 10);
        List<EtfReturnDto> result = repository.findEtfsByPeriod(Theme.GOLD, null, pageable, "monthly");

        assertThat(result).hasSize(2)
                .extracting("etfName")
                .containsExactlyInAnyOrder("현대차 ETF", "기아차 ETF");
    }
}
