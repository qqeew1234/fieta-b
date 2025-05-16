package EtfRecommendService.loginUtils;

public record JwtTokens(
        String accessToken,
        String refreshToken) {
}
