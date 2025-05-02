package EtfRecommendService.user;


import EtfRecommendService.comment.domain.QComment;
import EtfRecommendService.user.dto.UserCommentResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QUser user = QUser.user;
    private final QComment comment = QComment.comment;

    public UserQueryRepository(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public List<UserCommentResponse> findUserComment(Long userId, Pageable pageable) {
        return jpaQueryFactory
                .select(Projections.constructor(UserCommentResponse.class,
                        comment.id,
                        comment.user.id,
                        comment.user.nickName))
                .from(comment)
                .join(comment.user, user)
                .where(user.id.eq(userId)
                        .and(user.isLikePrivate.eq(false)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

    }

    public long countUserComments(Long userId) {
        Long count = jpaQueryFactory
                .select(comment.count())
                .from(comment)
                .join(comment.user, user)
                .where(user.id.eq(userId)
                        .and(user.isLikePrivate.eq(false)))
                .fetchOne();
        return count != null ? count : 0L;
    }
}
