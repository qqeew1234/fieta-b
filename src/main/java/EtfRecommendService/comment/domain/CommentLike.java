package EtfRecommendService.comment.domain;

import EtfRecommendService.user.User;
import EtfRecommendService.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.*;

@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Table(
        uniqueConstraints = @UniqueConstraint(
                name = "uk_comment_user",
                columnNames = {"comment_id", "user_id"}
        )
)
@NoArgsConstructor
@Builder
@Getter
@ToString
@AllArgsConstructor
public class CommentLike extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;


    @ManyToOne
    @ToString.Exclude
    private Comment comment;

    @ManyToOne
    @ToString.Exclude
    private User user;


}
