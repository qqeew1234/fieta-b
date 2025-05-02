package EtfRecommendService.reply.repository;

import EtfRecommendService.comment.domain.QComment;
import EtfRecommendService.reply.domain.QReply;
import EtfRecommendService.reply.domain.QReplyLike;
import EtfRecommendService.reply.repository.qdto.ReplyAndLikesCountQDto;
import EtfRecommendService.reply.repository.qdto.SortedRepliesQDto;
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
public class ReplyRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private final QReply qReply = QReply.reply;
    private final QReplyLike qReplyLike = QReplyLike.replyLike;
    private final QComment qComment = QComment.comment;
    private final QUser qUser = QUser.user;

    public SortedRepliesQDto findAllByCommentIdOrderByLikes(Pageable pageable, Long commentId) {
        //전체 데이터 개수
        long totalElements = Optional.ofNullable(queryFactory
                .select(qReply.comment.count())
                .from(qReply)
                .where(qReply.comment.id.eq(commentId))
                .fetchOne()).orElse(0L);

        //해당 ETF 에 달린 댓글과 좋아요 개수
        JPAQuery<ReplyAndLikesCountQDto> query = queryFactory
                .select(Projections.constructor(
                        ReplyAndLikesCountQDto.class,
                        qReply,
                        qReplyLike.count()
                ))
                .from(qReply)
                .join(qReply.comment, qComment)
                .join(qReply.user, qUser)
                .leftJoin(qReply.replyLikeList, qReplyLike)
                .where(qReply.comment.id.eq(commentId))
                .groupBy(qReply.id);

        //정렬기준이 내림차순인지 오름차순인지 확인
        Sort.Order order = pageable.getSort().getOrderFor("likes");
        boolean isDescending = Optional.ofNullable(order)
                .map(Sort.Order::getDirection)
                .map(Sort.Direction::isDescending)
                .orElse(true);


        //정렬 기준에 따라 맞게 쿼리 생성
        if (isDescending) {
            query = query.orderBy(qReplyLike.count().desc());
        } else {
            query = query.orderBy(qReplyLike.count().asc());
        }

        //페이징 구성에 맞게 조회
        List<ReplyAndLikesCountQDto> commentAndLikesCountQDtoList =
                query
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch();

        return SortedRepliesQDto.builder()
                .replyAndLikesCountQDtoList(commentAndLikesCountQDtoList)
                .totalElements(totalElements)
                .build();
    }
}
