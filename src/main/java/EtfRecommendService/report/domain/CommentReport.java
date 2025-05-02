package EtfRecommendService.report.domain;

import EtfRecommendService.comment.domain.Comment;
import EtfRecommendService.user.User;
import EtfRecommendService.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString
public class CommentReport extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long Id;

    @ManyToOne
    @ToString.Exclude
    private Comment comment;

    @ManyToOne
    @ToString.Exclude
    private User reporter;

    @Column(nullable = false)
    private ReportReason reportReason;

    @Builder.Default
    @Column(nullable = false)
    private boolean isChecked = false;

    //유저 엔티티에 리포트가 연결된 후 활성화
    public void addReport(Comment comment, User user){
        comment.getReportList().add(this);
        user.getCommentReportList().add(this);
    }
}
