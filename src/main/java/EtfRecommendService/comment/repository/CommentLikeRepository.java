package EtfRecommendService.comment.repository;

import EtfRecommendService.comment.domain.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    Optional<CommentLike> findByUser_LoginIdAndComment_Id(String loginId, Long commentId);

    Long countByComment_Id(Long commentId);
}
