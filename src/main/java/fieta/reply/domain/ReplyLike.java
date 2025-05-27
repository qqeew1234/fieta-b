package fieta.reply.domain;

import fieta.user.User;
import fieta.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString
@Table(
        name = "reply_like",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"user_id", "reply_id"}
        )
)
public class ReplyLike extends BaseEntity {
    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne
    @ToString.Exclude
    private User user;
    @ManyToOne
    @ToString.Exclude
    private Reply reply;

    public void toggleLike(User user, Reply reply){
        user.getReplyLikeList().add(this);
        reply.getReplyLikeList().add(this);
    }
}
