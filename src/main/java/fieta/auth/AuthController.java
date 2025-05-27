package fieta.auth;

import fieta.loginUtils.JwtTokens;
import fieta.user.dto.CreateUserRequest;
import fieta.user.dto.UserLoginRequest;
import fieta.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
