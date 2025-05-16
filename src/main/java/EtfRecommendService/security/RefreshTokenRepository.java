package EtfRecommendService.security;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenDetails, Long> {
    Optional<RefreshTokenDetails> findByRefreshToken(String refreshToken);
}
