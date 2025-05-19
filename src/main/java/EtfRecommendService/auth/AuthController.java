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
    public ResponseEntity<JwtTokens> login(@RequestBody UserLoginRequest loginRequest) {
        JwtTokens tokens = authService.login(loginRequest);

        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtTokens> refresh(@RequestBody String refreshToken) {
        JwtTokens newTokens = authService.refresh(refreshToken);

        return ResponseEntity.ok(newTokens);
    }
}
