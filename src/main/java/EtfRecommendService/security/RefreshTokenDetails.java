package EtfRecommendService.security;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class RefreshTokenDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String refreshToken; // 실제 토큰 문자열

    @Column(nullable = false)
    private Long userId; // 사용자 식별자 (User 엔티티의 PK)

    @Column(nullable = false)
    private LocalDateTime expiryDate; // 만료일시

}
