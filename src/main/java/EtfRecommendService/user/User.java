package EtfRecommendService.user;

import EtfRecommendService.comment.domain.Comment;
import EtfRecommendService.reply.domain.Reply;
import EtfRecommendService.user.exception.PasswordMismatchException;
import EtfRecommendService.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "users")
@AllArgsConstructor
@Builder
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String loginId;

    @Embedded
    @Column(nullable = false)
    private Password password;

    @Column(nullable = false)
    private String nickName;

    @OneToMany(mappedBy = "user")
    private List<Reply> replyList = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Comment> commentList = new ArrayList<>();

    private String imageUrl = "";
    @Builder.Default
    private Boolean isDeleted = false;

    private String theme;

    // 활정 기간
    private LocalDate suspensionPeriod;

    // 제재되어 삭제된 댓글카운트
    private int deletedCommentCount;

    // 활정 당한 횟수
    private int suspensionCount;

    // 댓글, 구독목록 공개여부
    private Boolean isLikePrivate = false;


    public User(String loginId,
                Password password,
                String nickName,
                Boolean isLikePrivate
                ) {
        this.loginId = loginId;
        this.password = password;
        this.nickName = nickName;
        this.isLikePrivate = isLikePrivate;
    }

    public void deleteUser() {
        this.isDeleted = true;
    }

    public void updateProfile(String nickName, Boolean isLikePrivate) {
        if (nickName != null) {
            this.nickName = nickName;
        }
        if (isLikePrivate != null) {
            this.isLikePrivate = isLikePrivate;
        }
    }

    public void updatePassword(Password existingPassword,Password newPassword) {
        if (!this.isSamePassword(existingPassword)) {
            throw new PasswordMismatchException("유저의 비밀번호와 입력받은 비밀번호가 같지 않습니다.");
        }
        if (this.isSamePassword(newPassword)) {
            throw new RuntimeException("변경할 비밀번호가 같습니다.");
        }
        this.password = newPassword;
    }

    public boolean isSamePassword(Password otherPassword) {
        if (this.getPassword().isSamePassword(otherPassword)) {
            return true;
        }
        return false;
    }

}
