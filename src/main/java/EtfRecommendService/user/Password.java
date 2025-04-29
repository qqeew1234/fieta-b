package EtfRecommendService.user;

import EtfRecommendService.loginUtils.SecurityUtils;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Embeddable
@Getter
@NoArgsConstructor
public class Password {

    private String password;

    public Password(String password) {
        if (password == null || password.isEmpty()) {
            throw new RuntimeException("비밀번호가 공백이면 안됩니다.");
        }
        this.password = SecurityUtils.sha256EncryptHex2(password);
    }

    public boolean isSamePassword(Password otherPassword) {
        if (this.password.equals(otherPassword.getPassword())) {
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Password password1 = (Password) o;
        return Objects.equals(password, password1.password);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(password);
    }

}
