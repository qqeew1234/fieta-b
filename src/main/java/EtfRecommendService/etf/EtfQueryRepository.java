package EtfRecommendService.etf;

import EtfRecommendService.etf.domain.QEtfProjection;
import EtfRecommendService.etf.dto.EtfReturnDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class EtfQueryRepository {
    private final JPAQueryFactory jpaQueryFactory;
    private final QEtfProjection etfProjection = QEtfProjection.etfProjection;

    public List<EtfReturnDto> findEtfsByPeriod(
            Theme theme,
            String keyword,
            Pageable pageable,
            String period
    ) {
        NumberExpression<Double> returnRate =
                "monthly".equalsIgnoreCase(period) ? etfProjection.monthlyReturn : etfProjection.weeklyReturn;

        return jpaQueryFactory
                .select(Projections.constructor(
                        EtfReturnDto.class,
                        etfProjection.etfName,
                        etfProjection.etfCode,
                        etfProjection.theme,
                        returnRate
                ))
                .from(etfProjection)
                .where(themeEq(theme), keywordContains(keyword))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    public Long fetchTotalCount(Theme theme, String keyword){
        Long count = jpaQueryFactory
                .select(etfProjection.count())
                .from(etfProjection)
                .where(themeEq(theme),
                        keywordContains(keyword))
                .fetchOne();

        //null 체크, 조회된거 없으면 0L로 처리
        return count == null ? 0L : count;
    }

    private BooleanExpression themeEq(Theme theme) {
        if (theme == null) {
            return null;  // dsl은 null값 무시 -> 전체 조회
        }
        return etfProjection.theme.eq(theme);
    }

    //검색어 기능 - 종목명, 종목코드
    private BooleanExpression keywordContains(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return null;
        }
        return etfProjection.etfName.containsIgnoreCase(keyword)  //대소문자 구분없이 검색
                .or(etfProjection.etfCode.containsIgnoreCase(keyword));
    }
}


