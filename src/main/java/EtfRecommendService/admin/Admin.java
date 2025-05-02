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
    @Column(nullable = false)
    private Password password;

    public boolean isSamePassword(Password inputPassword) {
        return this.getPassword().isSamePassword(inputPassword);
    }

}
