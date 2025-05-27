package fieta.loginUtils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class SecurityUtils {

    private static final BCryptPasswordEncoder BCRYPT_ENCODER =
            new BCryptPasswordEncoder(12);

    public static String bcryptEncrypt(String plainText) {
        return BCRYPT_ENCODER.encode(plainText);
    }

    public static boolean bcryptMatches(String plainText, String bcryptHash) {
        if (plainText == null || bcryptHash == null) {
            return false;
        }
        return BCRYPT_ENCODER.matches(plainText, bcryptHash);
    }
}