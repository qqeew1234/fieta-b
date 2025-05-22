package EtfRecommendService.etf.domain;

import EtfRecommendService.etf.Theme;
import EtfRecommendService.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class EtfProjection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String etfName;

    private String etfCode;

    @Enumerated(EnumType.STRING)
    private Theme theme;

    @Column(nullable = true)
    private double weeklyReturn;

    @Column(nullable = true)
    private double monthlyReturn;

    private LocalDate date;
}
