package EtfRecommendService.comment.repository;

import EtfRecommendService.comment.domain.QComment;
import EtfRecommendService.comment.domain.QCommentLike;
import EtfRecommendService.comment.repository.qdto.CommentAndLikesCountQDto;
import EtfRecommendService.comment.repository.qdto.SortedCommentsQDto;
import EtfRecommendService.etf.domain.QEtf;
import EtfRecommendService.user.QUser;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    private final QComment qComment = QComment.comment;
    private final QCommentLike qCommentLike = QCommentLike.commentLike;
    private final QUser qUser = QUser.user;
    private final QEtf qEtf = QEtf.etf;


    public SortedCommentsQDto findAllByEtfIdOrderByLikes(Pageable pageable, Long etfId) {
        //전체 데이터 개수
        long totalCount = countTotalElements(etfId);

        //해당 ETF 에 달린 댓글과 좋아요 개수
        JPAQuery<CommentAndLikesCountQDto> query = queryFactory
                .select(Projections.constructor(
                        CommentAndLikesCountQDto.class,
                        qComment,
                        qCommentLike.count()
                ))
                .from(qComment)
                .join(qComment.etf, qEtf)
                .join(qComment.user, qUser)
                .leftJoin(qComment.commentLikeList, qCommentLike)
                .where(qComment.etf.id.eq(etfId).and(qComment.isDeleted.eq(false)))
                .groupBy(qComment.id);

        //정렬기준이 내림차순인지 오름차순인지 확인
        Sort.Order order = pageable.getSort().getOrderFor("likes");
        boolean isDescending = isDescending(order);


        //정렬 기준에 따라 맞게 쿼리 생성
        if (isDescending) {
            query = query.orderBy(qCommentLike.count().desc());
        } else {
            query = query.orderBy(qCommentLike.count().asc());
        }

        //페이징 구성에 맞게 조회
        List<CommentAndLikesCountQDto> commentAndLikesCountQDtoList =
                query
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch();

        return SortedCommentsQDto.builder()
                .commentAndLikesCountQDtoPage(commentAndLikesCountQDtoList)
                .totalCount(totalCount)
                .build();
    }

    private long countTotalElements(Long etfId){
        return Optional.ofNullable(queryFactory
                .select(qComment.count())
                .from(qComment)
                .where(qComment.etf.id.eq(etfId).and(qComment.isDeleted.eq(false)))
                .fetchOne()).orElse(0L);
    }

    private boolean isDescending(Sort.Order order){
        return Optional.ofNullable(order)
                .map(Sort.Order::getDirection)
                .map(Sort.Direction::isDescending)
                .orElse(true);
    }
}
