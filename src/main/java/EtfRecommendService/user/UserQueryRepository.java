package EtfRecommendService.user;


import EtfRecommendService.comment.domain.QComment;
import EtfRecommendService.etf.domain.QEtf;
import EtfRecommendService.reply.domain.QReply;
import EtfRecommendService.user.dto.getUserCommentsAndReplies;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

import java.util.List;


@Repository
public class UserQueryRepository {

    @PersistenceContext
    private EntityManager em;

    private final JPAQueryFactory jpaQueryFactory;
    private final QUser user = QUser.user;
    private final QComment comment = QComment.comment;
    private final QReply reply = QReply.reply;


    public UserQueryRepository(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public List<getUserCommentsAndReplies> getUserCommentsAndReplies(Long userId, Pageable pageable) {
        String nativeQuery = CommentsAndRepliesQuery();
        List<Object[]> resultList = executeNativeQuery(nativeQuery, userId, pageable);
        return convertToDto(resultList);
    }

    private String CommentsAndRepliesQuery() {
        return """
            (
                SELECT 
                    c.id AS id,
                    c.etf_id AS etfId,
                    c.user_id AS userId,
                    u.nick_name AS nickName,
                    c.content AS content,
                    u.image_url AS imageUrl,
                    c.created_at AS createdAt
                FROM comment c
                JOIN users u ON c.user_id = u.id
                WHERE c.user_id = :userId AND c.is_deleted = false
            )
            UNION ALL
            (
                SELECT 
                    r.id AS id,
                    c.etf_id AS etfId,
                    r.user_id AS userId,
                    u.nick_name AS nickName,
                    r.content AS content,
                    u.image_url AS imageUrl,
                    r.created_at AS createdAt
                FROM reply r
                JOIN users u ON r.user_id = u.id
                JOIN comment c ON r.comment_id = c.id
                WHERE r.user_id = :userId AND r.is_deleted = false
            )
            ORDER BY createdAt DESC
            LIMIT :limit OFFSET :offset
            """;
    }

    private List<Object[]> executeNativeQuery(String query, Long userId, Pageable pageable) {
        @SuppressWarnings("unchecked")
        List<Object[]> resultList = em.createNativeQuery(query)
                .setParameter("userId", userId)
                .setParameter("limit", pageable.getPageSize())
                .setParameter("offset", pageable.getOffset())
                .getResultList();
        return resultList;
    }

    private List<getUserCommentsAndReplies> convertToDto(List<Object[]> resultList) {
        return resultList.stream().map(row -> new getUserCommentsAndReplies(
                ((Number) row[0]).longValue(),
                ((Number) row[1]).longValue(),
                ((Number) row[2]).longValue(),
                (String) row[3],
                (String) row[4],
                (String) row[5],
                ((Timestamp) row[6]).toLocalDateTime()
        )).toList();
    }


    private long countUserComments(Long userId) {
        Long count = jpaQueryFactory
                .select(comment.count())
                .from(comment)
                .join(comment.user, user)
                .where(user.id.eq(userId)
                        .and(comment.isDeleted.eq(false))) // 댓글이 삭제됐는지
                .fetchOne();
        return count != null ? count : 0L;
    }

    private long countUserReplys(Long userId) {
        Long count = jpaQueryFactory
                .select(reply.count())
                .from(reply)
                .join(reply.user, user)
                .where(user.id.eq(userId)
                        .and(comment.isDeleted.eq(false))) // 댓글이 삭제됐는지
                .fetchOne();
        return count != null ? count : 0L;
    }

    public long totalCount(Long userId) {
        return countUserComments(userId) + countUserReplys(userId);
    }

}
