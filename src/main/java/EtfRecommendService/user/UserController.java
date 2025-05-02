package EtfRecommendService.user;

import EtfRecommendService.loginUtils.LoginMember;
import EtfRecommendService.user.dto.*;
import EtfRecommendService.user.exception.PasswordMismatchException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "/api/v1/users")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserResponse create(@RequestBody CreateUserRequest userRequest) {
        return userService.create(userRequest);
    }

    @PostMapping("/login")
    public UserLoginResponse login(@RequestBody UserLoginRequest loginRequest) {
        return userService.login(loginRequest);
    }

    @PatchMapping
    public UserUpdateResponse updateProfile(@LoginMember String auth, @RequestBody UserUpdateRequest updateRequest) {
        return userService.UpdateProfile(auth,updateRequest);
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@LoginMember String auth) {
        userService.delete(auth);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/me/password")
    public UserPasswordResponse updatePassword(@LoginMember String auth, @RequestBody UserPasswordRequest passwordRequest) {
        if (!passwordRequest.newPassword().isSamePassword(passwordRequest.confirmNewPassword())) {
            throw new PasswordMismatchException("새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
        }
        return userService.updatePassword(auth, passwordRequest);
    }

    @GetMapping("/{userId}")
    public UserPageResponse findByUser(
            @LoginMember String auth,
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return userService.findByUser(auth, userId,pageable);
    }
}
