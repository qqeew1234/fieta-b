package EtfRecommendService.user;


import EtfRecommendService.user.exception.PasswordMismatchException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;



import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
public class UserUnitTest {

    private User user;

    @BeforeEach
    void setUp() {
        Password password = new Password("현재비밀번호");
        user = new User("user1",password,"nickName",false);
    }

    @Test
    @DisplayName("비밀번호변경 성공")
    void 변경성공() {
        Password existingPassword = new Password("현재비밀번호");
        Password newPassword = new Password("새비밀번호");

        user.updatePassword("현재비밀번호","새비밀번호");

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        assertThat(passwordEncoder.matches("새비밀번호",user.getPassword().getHash())).isTrue();
    }

    @Test
    @DisplayName("입력받은 기존 비밀번호와 유저비밀번호불일치")
    void 변경실패2() {
        Password existingPassword = new Password("잘못된비밀번호");
        Password newPassword = new Password("새비밀번호");

        PasswordMismatchException exception = assertThrows(
                PasswordMismatchException.class,
                () -> user.updatePassword(existingPassword.getHash(), newPassword.getHash())
        );

        assertEquals("유저의 비밀번호와 입력받은 비밀번호가 같지 않습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("변경할비밀번호가 동일")
    void 변경실패3() {
        Password existingPassword = new Password("현재비밀번호");
        Password newPassword = new Password("현재비밀번호");

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> user.updatePassword("현재비밀번호", "현재비밀번호")
        );

        assertEquals("변경할 비밀번호가 같습니다.", exception.getMessage());
    }
}
