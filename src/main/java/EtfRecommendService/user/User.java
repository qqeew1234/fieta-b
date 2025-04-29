package EtfRecommendService.user;

import EtfRecommendService.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "users")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String loginId;

    @Embedded
    private Password password;

    private String nickName;

    private String imageUrl = "";

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

    public void profileUpdate(String nickName, Boolean isLikePrivate) {
        if (nickName != null) {
            this.nickName = nickName;
        }
        this.isLikePrivate = isLikePrivate;
    }

    public void passwordUpdate(Password newRawPassword) {
        this.password = newRawPassword;
    }

    public boolean isSamePassword(Password otherPassword) {
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
