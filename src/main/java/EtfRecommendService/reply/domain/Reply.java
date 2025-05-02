package EtfRecommendService.reply.domain;

import EtfRecommendService.comment.domain.Comment;
import EtfRecommendService.report.domain.ReplyReport;
import EtfRecommendService.user.User;
import EtfRecommendService.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true,callSuper = false)
@ToString
public class Reply extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;
    @Column(nullable = false)
    @Builder.Default
    private boolean isDeleted = false;

    @ManyToOne
    @ToString.Exclude
    private Comment comment;
    @ManyToOne
    @ToString.Exclude
    private User user;
    @OneToMany(mappedBy = "reply")
    @Builder.Default
    @ToString.Exclude
    private List<ReplyLike> replyLikeList = new ArrayList<>();
    @OneToMany(mappedBy = "reply")
    @Builder.Default
    @ToString.Exclude
    private List<ReplyReport> ReportList = new ArrayList<>();

    public void addCommentAndUser(Comment comment, User user){
        comment.getReplyList().add(this);
        user.getReplyList().add(this);
    }

    public void update(String content) {
        this.content = content;
    }

    public void softDelete(User user){
            this.isDeleted = true;
    }

    public void validateUserPermission(User user){
        if (!this.user.equals(user)) throw new RuntimeException("Permission denied to delete this comment.");
    }

    public boolean isWrittenBy(User user){
        return this.user.equals(user);
    }
}
