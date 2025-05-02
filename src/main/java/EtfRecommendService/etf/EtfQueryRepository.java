package EtfRecommendService.etf;

import EtfRecommendService.etf.domain.Etf;
import EtfRecommendService.etf.domain.QEtf;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EtfQueryRepository {
    private final JPAQueryFactory jpaQueryFactory;
    private final QEtf qEtf = QEtf.etf;

    public EtfQueryRepository(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    //조회(null이면 전체, 테마 필터링, 정렬)
    public Page<Etf> findAllByThemeAndSort(Theme theme, SortOrder sortOrder,String keyword, Pageable pageable){
        //데이터 조회
        List<Etf> content = jpaQueryFactory
                .selectFrom(qEtf)
                .where(themeEq(theme),
                        keywordContains(keyword))
                .offset(pageable.getOffset())  //페이지네이션
                .limit(pageable.getPageSize())  //페이지네이션
                .fetch();

        // 전체 개수 조회 - total 쿼리는 limit적용하면 안됨 (offset, limit 없이 개수만 세기 때문)
        long total = fetchTotalCount(theme, keyword);

        //Page 객체로 포장해서 반환, null방지
        return new PageImpl<>(content, pageable, total);
    }

    private Long fetchTotalCount(Theme theme, String keyword){
        Long count = jpaQueryFactory
                .select(qEtf.count())
                .from(qEtf)
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
        return qEtf.theme.eq(theme);
    }

    //검색어 기능 - 종목명, 종목코드
    private BooleanExpression keywordContains(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return null;
        }
        return qEtf.etfName.containsIgnoreCase(keyword)  //대소문자 구분없이 검색
                .or(qEtf.etfCode.containsIgnoreCase(keyword));
    }
}


