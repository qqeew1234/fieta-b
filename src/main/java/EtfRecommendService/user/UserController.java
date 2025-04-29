package EtfRecommendService.user;

import EtfRecommendService.loginUtils.LoginMember;
import EtfRecommendService.user.dto.*;
import jakarta.persistence.Id;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "/api/v1/users")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    @PostMapping()
    public UserResponse create(@RequestBody CreateUserRequest userRequest) {
        return userService.create(userRequest);
    }

    @PostMapping("/login")
    public UserLoginResponse login(@RequestBody UserLoginRequest loginRequest) {
        return userService.login(loginRequest);
    }

    @PatchMapping()
    public UserUpdateResponse profileUpdate(@LoginMember String auth, @RequestBody UserUpdateRequest updateRequest) {
        return userService.profileUpdate(auth,updateRequest);
    }

    @DeleteMapping()
    public UserDeleteResponse delete(@LoginMember String auth) {
        return userService.delete(auth);
    }

    @PostMapping("/me/password")
    public UserPasswordResponse passwordUpdate(@LoginMember String auth, @RequestBody UserPasswordRequest passwordRequest) {
        return userService.passwordUpdate(auth, passwordRequest);
    }

    @GetMapping("/{userId}")
    public MypageResponse findByUser(@LoginMember String auth, @PathVariable Long userId) {
        return userService.findByUser(auth, userId);
    }
}
