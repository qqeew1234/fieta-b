package fieta.user;

import fieta.S3Service;
import fieta.admin.AdminRepository;
import fieta.loginUtils.JwtProvider;
import fieta.security.RefreshTokenRepository;
import fieta.user.dto.*;
import fieta.user.exception.UserMismatchException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import static fieta.user.exception.ErrorMessages.USER_MISMATCH;


@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final S3Service s3Service;
    private final UserQueryRepository userQueryRepository;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AdminRepository adminRepository;

    public User getByLoginId(String loginId) {
        return userRepository.findByLoginIdAndIsDeletedFalse(loginId).orElseThrow(
                () -> new UserMismatchException(USER_MISMATCH));
    }

    @Transactional
    public UserUpdateResponse UpdateProfile(String loginId, UserUpdateRequest updateRequest) {
        User user = getByLoginId(loginId);

        user.updateProfile(
                updateRequest.nickname(),
                updateRequest.isLikePrivate());

        return new UserUpdateResponse(
                user.getId(),
                user.getNickname(),
                user.getImageUrl(),
                user.isLikePrivate());
    }

    @Transactional
    public void delete(String loginId) {
        User user = getByLoginId(loginId);

        user.deleteUser();
    }

    @Transactional
    public UserPasswordResponse updatePassword(String loginId, UserPasswordRequest passwordRequest) {
        User user = getByLoginId(loginId);

        user.updatePassword(
                passwordRequest.existingPassword(),
                passwordRequest.newPassword());

        return new UserPasswordResponse(user.getId());
    }

    public UserPageResponse findUserComments(String loginId, Long userId, Pageable pageable) {
        User findUser = userRepository.findById(userId).orElseThrow(
                () -> new NoSuchElementException("존재하지 않는 회원입니다."));

        User loginUser = getByLoginId(loginId);

        boolean selfProfile = loginUser.isSelfProfile(userId);

        // 만약 찾는유저의 정보가 비공개 설정이고 로그인한 유저의 조회가 아니라면
        if (findUser.isLikePrivate() && !selfProfile) {
            return new UserPageResponse(
                    pageable.getPageNumber() + 1,
                    pageable.getPageSize(),
                    0,
                    0,
                    null
            );
        }

        List<getUserCommentsAndReplies> getUserCommentRespons = userQueryRepository.getUserCommentsAndReplies(userId, pageable);

        long totalCount = userQueryRepository.totalCount(userId);

        return new UserPageResponse(
                pageable.getPageNumber() + 1,
                pageable.getPageSize(),
                totalCount,
                (totalCount + pageable.getPageSize() - 1) / pageable.getPageSize(),
                getUserCommentRespons
        );
    }

    @Transactional
    public UserProfileResponse imageUpdate(String loginId, MultipartFile file) throws IOException {
        User user = getByLoginId(loginId);

        String existingImageUrl = user.getImageUrl();

        if (existingImageUrl != null && !existingImageUrl.isEmpty()) {
            s3Service.deleteFile(existingImageUrl);
        }

        String newImageUrl = s3Service.uploadFile(file);

        user.updateProfileImg(newImageUrl);

        return new UserProfileResponse(user.getId(), user.getImageUrl());
    }

    public UserDetailResponse findByUserId(String myLoginId, String loginId) {
        getByLoginId(myLoginId);
        User user = userRepository.findByLoginIdAndIsDeletedFalse(loginId).orElseThrow(() ->
                new RuntimeException("존재하지 않는 유저 id : " + loginId));


        return new UserDetailResponse(
                user.getId(),
                user.getLoginId(),
                user.getNickname(),
                user.getImageUrl(),
                user.isLikePrivate(),
                user.getCreatedAt());
    }
}
