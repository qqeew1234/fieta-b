package fieta.loginUtils;

public record JwtTokens(
        String accessToken,
        String refreshToken) {
}
