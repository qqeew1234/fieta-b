package EtfRecommendService.etf.domain;

import EtfRecommendService.etf.Theme;
import EtfRecommendService.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

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

    @Builder
    public EtfProjection(String etfName, String etfCode, Theme theme, double weeklyReturn, double monthlyReturn) {
        this.etfName = etfName;
        this.etfCode = etfCode;
        this.theme = theme;
        this.weeklyReturn = weeklyReturn;
        this.monthlyReturn = monthlyReturn;
    }
}
