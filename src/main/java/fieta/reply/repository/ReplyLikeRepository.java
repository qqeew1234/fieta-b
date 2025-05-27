package fieta.reply.repository;

import fieta.reply.domain.ReplyLike;
import fieta.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReplyLikeRepository extends JpaRepository<ReplyLike, Long> {
    boolean existsByReply_ReplyLikeList_User(User user);

    boolean existsByUserIdAndReplyId(Long id, Long id1);

    Optional<ReplyLike> findByUserIdAndReplyId(Long userId, Long replyId);
}
