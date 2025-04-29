package EtfRecommendService.admin;

import EtfRecommendService.admin.dto.AdminLoginRequest;
import EtfRecommendService.admin.dto.AdminLoginResponse;
import EtfRecommendService.loginUtils.JwtProvider;
import EtfRecommendService.user.PasswordMismatchException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final JwtProvider jwtProvider;

    public AdminLoginResponse login(AdminLoginRequest loginRequest) {
        Admin admin = adminRepository.findByLoginId(loginRequest.loginId()).orElseThrow(
                () -> new NoSuchElementException("찾을 수 없는 관리자 id : " + loginRequest.loginId()));

        if (loginRequest.password().isSamePassword(admin.getPassword())) {
            return new AdminLoginResponse(jwtProvider.createToken(admin.getLoginId()));
        }
        throw new PasswordMismatchException("비밀번호가 다릅니다.");
    }
}
