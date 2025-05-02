package EtfRecommendService.admin;

import EtfRecommendService.admin.dto.AdminLoginRequest;
import EtfRecommendService.admin.dto.AdminLoginResponse;
import EtfRecommendService.loginUtils.JwtProvider;
import EtfRecommendService.user.exception.UserMismatchException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static EtfRecommendService.user.exception.ErrorMessages.USER_MISMATCH;


@RequiredArgsConstructor
@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final JwtProvider jwtProvider;

    public AdminLoginResponse login(AdminLoginRequest loginRequest) {
        Admin admin = adminRepository.findByLoginId(loginRequest.loginId()).orElseThrow(
                () -> new UserMismatchException(USER_MISMATCH));

        if (!admin.isSamePassword(loginRequest.password())) {
            throw new UserMismatchException(USER_MISMATCH);
        }

        return new AdminLoginResponse(jwtProvider.createToken(admin.getLoginId()));
    }
}
