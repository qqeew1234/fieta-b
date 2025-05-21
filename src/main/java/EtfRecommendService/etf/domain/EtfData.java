package EtfRecommendService.etf.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class EtfData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String etfCode;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    private int startPrice;

    @Column(nullable = false)
    private int highPrice;

    @Column(nullable = false)
    private int lowerPrice;

    @Column(nullable = false)
    private int lastPrice;

}
