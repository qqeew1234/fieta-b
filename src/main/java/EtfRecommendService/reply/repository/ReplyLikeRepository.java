package EtfRecommendService.reply.repository;

import EtfRecommendService.reply.domain.ReplyLike;
import EtfRecommendService.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReplyLikeRepository extends JpaRepository<ReplyLike, Long> {
    boolean existsByReply_ReplyLikeList_User(User user);

    boolean existsByUserIdAndReplyId(Long id, Long id1);

    Optional<ReplyLike> findByUserIdAndReplyId(Long userId, Long replyId);
}
