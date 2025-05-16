package EtfRecommendService.user;

import EtfRecommendService.loginUtils.SecurityUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Embeddable
@Getter
@NoArgsConstructor
public class Password {

    @Column(nullable = false)
    private String hash;

    public Password(String rawPassword) {
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new RuntimeException("비밀번호가 공백이면 안됩니다.");
        }
        this.hash = SecurityUtils.bcryptEncrypt(rawPassword);
    }

    public boolean isSamePassword(String otherPassword) {
        return SecurityUtils.bcryptMatches(otherPassword, this.hash);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Password password1 = (Password) o;
        return Objects.equals(hash, password1.hash);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(hash);
    }

}
