package fieta.comment.domain;

import fieta.etf.domain.Etf;
import fieta.reply.domain.Reply;
import fieta.report.domain.CommentReport;
import fieta.user.User;
import fieta.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Builder
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString
@AllArgsConstructor
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String content;
    @Column(nullable = false)
    @Builder.Default
    private boolean isDeleted = false;

    @OneToMany(mappedBy = "comment")
    @ToString.Exclude
    @Builder.Default
    private List<CommentLike> commentLikes = new ArrayList<>();

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "etf_id")
    private Etf etf;

    @ManyToOne
    @ToString.Exclude
    private User user;

    @OneToMany(mappedBy = "comment")
    @Builder.Default
    @ToString.Exclude
    private List<CommentReport> ReportList = new ArrayList<>();

    @OneToMany(mappedBy = "comment")
    @Builder.Default
    @ToString.Exclude
    private List<Reply> replyList = new ArrayList<>();

    public void setDeleted() {
        isDeleted = true;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void addEtfAndUser(Etf etf, User user) {
        etf.getCommentList().add(this);
        user.getCommentList().add(this);
    }
}
