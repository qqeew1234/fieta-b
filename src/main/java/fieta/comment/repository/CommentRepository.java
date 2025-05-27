package fieta.comment.repository;

import fieta.comment.domain.Comment;
import fieta.etf.domain.Etf;
import fieta.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<Comment> findTopByUserOrderByCreatedAtDesc(User user);

    Optional<Comment> findTopByUserAndEtfOrderByCreatedAtDesc(User user, Etf etf);

    Page<Comment> findAllByEtfId(Long etfId, Pageable pageable);

    Optional<Comment> findByIdAndIsDeletedFalse(Long commentId);

    Page<Comment> findAllByEtfIdAndIsDeletedFalse(Long etfId, Pageable pageable);
}
