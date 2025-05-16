package EtfRecommendService.auth;

import EtfRecommendService.loginUtils.JwtTokens;
import EtfRecommendService.user.dto.CreateUserRequest;
import EtfRecommendService.user.dto.UserLoginRequest;
import EtfRecommendService.user.dto.UserResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/join")
    public ResponseEntity<UserResponse> create(@RequestBody CreateUserRequest userRequest) {
        UserResponse userResponse = authService.create(userRequest);
        return ResponseEntity.status(201).body(userResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody UserLoginRequest loginRequest,
                                      HttpServletResponse response) {
        JwtTokens tokens = authService.login(loginRequest);

        setCookies(tokens, response);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<Void> refresh(HttpServletRequest request,
                                        HttpServletResponse response) {
        // 쿠키에서 리프레시 토큰 추출
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        JwtTokens newTokens = authService.refresh(refreshToken);

        setCookies(newTokens, response);

        return ResponseEntity.ok().build();
    }

    private void setCookies(JwtTokens jwtTokens, HttpServletResponse response){
        // 액세스 토큰 쿠키
        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", jwtTokens.accessToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")
                .maxAge(60 * 15) // 15분
                .build();

        // 리프레시 토큰 쿠키
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", jwtTokens.refreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")
                .maxAge(60 * 60 * 24 * 14) // 2주
                .build();

        response.addHeader("Set-Cookie", accessTokenCookie.toString());
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());
    }
}
