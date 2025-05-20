package EtfRecommendService.user;

import EtfRecommendService.comment.domain.Comment;
import EtfRecommendService.comment.domain.CommentLike;
import EtfRecommendService.reply.domain.Reply;
import EtfRecommendService.reply.domain.ReplyLike;
import EtfRecommendService.report.domain.CommentReport;
import EtfRecommendService.report.domain.ReplyReport;
import EtfRecommendService.user.exception.PasswordMismatchException;
import EtfRecommendService.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "users")

public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String loginId;

    @Embedded
    private Password password;

    @Column(nullable = false, unique = true)
    private String nickname;

    @OneToMany(mappedBy = "user")
    private List<Reply> replyList = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Comment> commentList = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<ReplyLike> replyLikeList = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<CommentLike> commentLikeList = new ArrayList<>();

    @OneToMany(mappedBy = "reporter")
    private List<CommentReport> commentReportList = new ArrayList<>();

    @OneToMany(mappedBy = "reporter")
    private List<ReplyReport> replyReportList = new ArrayList<>();

    private String imageUrl = "";

    private boolean isDeleted = false;

    private String theme;

    // 활정 기간
    private LocalDate suspensionPeriod;

    // 제재되어 삭제된 댓글카운트
    private int deletedCommentCount;

    // 활정 당한 횟수
    private int suspensionCount;

    // 댓글, 구독목록 공개여부
    private boolean isLikePrivate = false;


    public User(String loginId,
                Password password,
                String nickname,
                Boolean isLikePrivate
                ) {
        this.loginId = loginId;
        this.password = password;
        this.nickname = nickname;
        this.isLikePrivate = isLikePrivate;
    }

    public void deleteUser() {
        this.isDeleted = true;
    }

    public void updateProfile(String nickname, Boolean isLikePrivate) {
        if (nickname != null) {
            this.nickname = nickname;
        }
        if (isLikePrivate != null) {
            this.isLikePrivate = isLikePrivate;
        }
    }

    public boolean isSelfProfile(Long userId) {
        if (this.id.equals(userId)) {
            return true;
        }
        return false;
    }

    public void updateProfileImg(String imgUrl) {
        this.imageUrl = imgUrl;
    }

    public void updatePassword(String existingPassword,String newPassword) {
        if (!this.isSamePassword(existingPassword)) {
            throw new PasswordMismatchException("유저의 비밀번호와 입력받은 비밀번호가 같지 않습니다.");
        }
        if (existingPassword.equals(newPassword)) {
            throw new RuntimeException("변경할 비밀번호가 같습니다.");
        }
        this.password = new Password(newPassword);
    }

    public boolean isSamePassword(String otherPassword) {
        if (this.getPassword().isSamePassword(otherPassword)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(password);
    }

}
