package EtfRecommendService.reply.repository;

import EtfRecommendService.reply.domain.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    Optional<Reply> findFirstByUserIdAndCommentIdOrderByCreatedAtDesc(Long userId, Long commentId);

    Page<Reply> findAllByCommentId(Long commentId, Pageable pageable);
}
