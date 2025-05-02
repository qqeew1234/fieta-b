package EtfRecommendService.report.domain;

import EtfRecommendService.reply.domain.Reply;
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
public class ReplyReport extends BaseEntity {
    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long Id;

    @ManyToOne
    @ToString.Exclude
    private Reply reply;

    @ManyToOne
    @ToString.Exclude
    private User reporter;

    @Column(nullable = false)
    private ReportReason reportReason;

    @Builder.Default
    @Column(nullable = false)
    private boolean isChecked = false;

    //Reply 와 User 에 Report 연결된 후 활성화
    public void addReport(Reply reply, User user) {
        reply.getReportList().add(this);
        user.getReplyReportList().add(this);
    }
}
