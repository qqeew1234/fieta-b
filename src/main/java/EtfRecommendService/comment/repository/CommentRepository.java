package EtfRecommendService.comment.repository;

import EtfRecommendService.comment.domain.Comment;
import EtfRecommendService.etf.domain.Etf;
import EtfRecommendService.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<Comment> findTopByUserOrderByCreatedAtDesc(User user);

    Optional<Comment> findTopByUserAndEtfOrderByCreatedAtDesc(User user, Etf etf);

    Page<Comment> findAllByEtfId(Long etfId, Pageable pageable);

    Optional<Comment> findByIdAndIsDeletedFalse(Long commentId);
}
