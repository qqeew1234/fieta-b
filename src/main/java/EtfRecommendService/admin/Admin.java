package EtfRecommendService.admin;


import EtfRecommendService.user.Password;

import EtfRecommendService.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Admin extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String loginId;

    @Embedded
    private Password password;

    public Admin(String loginId, Password password) {
        this.loginId = loginId;
        this.password = password;
    }

    public boolean isSamePassword(String inputPassword) {
        return this.password.isSamePassword(inputPassword);
    }

}
