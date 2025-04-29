package EtfRecommendService.etf;

import EtfRecommendService.etf.domain.Subscribe;
import EtfRecommendService.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SubscribeRepository extends JpaRepository<Subscribe, Long> {
    Optional<Subscribe> findByUserAndEtfId(User user, Long etfId);


    boolean existsByUserAndEtfId(User user, Long etfId);

    Page<Subscribe> findByUser(User user, Pageable pageable);


    List<Subscribe> findAllByExpiredTimeBetween(LocalDateTime start, LocalDateTime end);
}
