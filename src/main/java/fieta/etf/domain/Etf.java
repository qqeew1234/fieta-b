package fieta.etf.domain;

import fieta.comment.domain.Comment;
import fieta.etf.Theme;
import fieta.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class Etf extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String etfName;

    @Column(nullable = false)
    private String etfCode;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private LocalDateTime listingDate;

    @Column(nullable = false, name = "theme")
    @Enumerated(EnumType.STRING)
    private Theme theme;

    @OneToMany(mappedBy = "etf")
    private List<Comment> commentList = new ArrayList<>();

    @Builder
    public Etf(String etfName,
               String etfCode,
               String companyName,
               LocalDateTime listingDate,
               Theme theme) {
        this.etfName = etfName;
        this.etfCode = etfCode;
        this.companyName = companyName;
        this.listingDate = listingDate;
        this.theme = theme;
    }

}
