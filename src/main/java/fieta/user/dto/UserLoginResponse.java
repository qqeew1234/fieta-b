package fieta.user.dto;

public record UserLoginResponse(
        String accessToken,
        String refreshToken
) {
}
