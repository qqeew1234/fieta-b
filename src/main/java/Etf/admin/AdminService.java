package Etf.admin;

import Etf.admin.dto.AdminLoginRequest;
import Etf.admin.dto.AdminLoginResponse;
import Etf.loginUtils.JwtProvider;
import Etf.loginUtils.SecurityUtils;
import Etf.user.Password;
import Etf.user.PasswordMismatchException;
import Etf.user.User;
import Etf.user.dto.CreateUserRequest;
import Etf.user.dto.UserLoginRequest;
import Etf.user.dto.UserLoginResponse;
import Etf.user.dto.UserResponse;
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
