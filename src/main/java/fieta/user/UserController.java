package fieta.user;

import fieta.S3Service;
import fieta.user.dto.*;

import fieta.user.exception.PasswordMismatchException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequestMapping(value = "/api/v1")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;
    private final S3Service s3Service;



    @Secured("ROLE_USER")
    @PatchMapping("/users")
    public ResponseEntity<UserUpdateResponse> updateProfile(@AuthenticationPrincipal UserDetails userDetails, @RequestBody UserUpdateRequest updateRequest) {
        UserUpdateResponse userUpdateResponse = userService.UpdateProfile(userDetails.getUsername(), updateRequest);
        return ResponseEntity.ok(userUpdateResponse);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @DeleteMapping("/users")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal UserDetails userDetails
    ) {
        userService.delete(userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @Secured("ROLE_USER")
    @PatchMapping("/users/me/password")
    public ResponseEntity<UserPasswordResponse> updatePassword(@AuthenticationPrincipal UserDetails userDetails, @RequestBody UserPasswordRequest passwordRequest) {
        if (!passwordRequest.newPassword().equals(passwordRequest.confirmNewPassword())) {
            throw new PasswordMismatchException("새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
        }
        System.out.println(userDetails);
        UserPasswordResponse userPasswordResponse = userService.updatePassword(userDetails.getUsername(), passwordRequest);
        return ResponseEntity.ok(userPasswordResponse);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/users/comments/{userId}")
    public ResponseEntity<UserPageResponse> findUserComments(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        UserPageResponse userPageResponse = userService.findUserComments(userDetails.getUsername(), userId, pageable);
        return ResponseEntity.ok(userPageResponse);
    }

    @Secured("ROLE_USER")
    @PatchMapping("/users/image")
    public ResponseEntity<UserProfileResponse> imageUpdate(@AuthenticationPrincipal UserDetails userDetails,
                                                           @RequestPart(value = "images") MultipartFile file) throws IOException {
        UserProfileResponse userProfileResponse = userService.imageUpdate(userDetails.getUsername(), file);
        return ResponseEntity.ok(userProfileResponse);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/users/{loginId}")
    public ResponseEntity<UserDetailResponse> findByUserId(@AuthenticationPrincipal UserDetails userDetails, @PathVariable String loginId) {
        UserDetailResponse userDetailResponse = userService.findByUserId(userDetails.getUsername(), loginId);
        return ResponseEntity.ok(userDetailResponse);
    }

}
