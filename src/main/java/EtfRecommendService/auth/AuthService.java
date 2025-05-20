package EtfRecommendService.auth;

import EtfRecommendService.admin.Admin;
import EtfRecommendService.admin.AdminRepository;
import EtfRecommendService.loginUtils.JwtProvider;
import EtfRecommendService.loginUtils.JwtTokens;
import EtfRecommendService.security.RefreshTokenDetails;
import EtfRecommendService.security.RefreshTokenRepository;
import EtfRecommendService.security.TokenNotFoundException;
import EtfRecommendService.security.UserDetail;
import EtfRecommendService.user.Password;
import EtfRecommendService.user.User;
import EtfRecommendService.user.UserRepository;
import EtfRecommendService.user.dto.CreateUserRequest;
import EtfRecommendService.user.dto.UserLoginRequest;
import EtfRecommendService.user.dto.UserResponse;
import EtfRecommendService.user.exception.UserMismatchException;
import com.amazonaws.services.kms.model.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static EtfRecommendService.user.exception.ErrorMessages.USER_MISMATCH;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AdminRepository adminRepository;

    public UserResponse create(CreateUserRequest userRequest) {

        Password password = new Password(userRequest.password());

        User user = new User(
                userRequest.loginId(),
                password,
                userRequest.nickname(),
                userRequest.isLikePrivate());

        userRepository.save(user);

        return new UserResponse(
                user.getId(),
                userRequest.loginId(),
                userRequest.nickname(),
                userRequest.isLikePrivate());
    }

    public JwtTokens login(UserLoginRequest loginRequest) {
        String identifier = loginRequest.role().toUpperCase() + ":" + loginRequest.loginId();

        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(identifier, loginRequest.password());

        Authentication authentication = authenticationManager.authenticate(token);

        UserDetails userDetail = (UserDetails) authentication.getPrincipal();

        //{accessToken, refreshToken}
        String[] tokens = generateTokens(userDetail);

        Long id;
        if (loginRequest.role().equals("USER")){
            id = getByUserLoginId(userDetail.getUsername()).getId();
        }
        else {
            id = getByAdminLoginId(userDetail.getUsername()).getId();
        }
        saveRefreshDetails(id,tokens[1]);

        return new JwtTokens(tokens[0], tokens[1]);
    }

    public JwtTokens refresh(String refresh) {

        if (jwtProvider.isValidToken(refresh, false)) {
            RefreshTokenDetails refreshTokenDetails =
                    refreshTokenRepository.findByRefreshToken(refresh)
                            .orElseThrow(
                                    () -> new TokenNotFoundException("만료된 리프레시 토큰, 재로그인 바람")
                            );
            String username = jwtProvider.getSubjectFromRefresh(refresh);
            List<SimpleGrantedAuthority> roles = jwtProvider.getRolesFromRefresh(refresh)
                    .stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList();

            UserDetails userDetails = new UserDetail(username,"", roles);

            //{accessToken, refreshToken}
            String[] tokens = generateTokens(userDetails);

            refreshTokenRepository.deleteById(refreshTokenDetails.getId());

            Long userId;
            if ("ROLE_ADMIN".equalsIgnoreCase(roles.get(0).getAuthority())){
                userId = getByAdminLoginId(username).getId();
            }
            else {
                userId = getByUserLoginId(username).getId();
            }

            saveRefreshDetails(userId, tokens[1]);

            return new JwtTokens(tokens[0], tokens[1]);
        } else {
            throw new TokenNotFoundException("유효하지 않은 토큰");
        }
    }

    private void saveRefreshDetails(Long id, String refreshToken){
        LocalDateTime expiryDate = jwtProvider
                .getExpirationFromRefreshToken(refreshToken)
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        RefreshTokenDetails refreshTokenDetails = RefreshTokenDetails.builder()
                .refreshToken(refreshToken)
                .userId(id)
                .expiryDate(expiryDate)
                .build();
        refreshTokenRepository.save(refreshTokenDetails);
    }

    private String[] generateTokens(UserDetails userDetail) {
        String accessToken = jwtProvider.createToken(userDetail);
        String refreshToken = jwtProvider.createRefreshToken(userDetail);
        return new String[]{accessToken, refreshToken};
    }

    private User getByUserLoginId(String loginId) {
        return userRepository.findByLoginIdAndIsDeletedFalse(loginId).orElseThrow(
                () -> new UserMismatchException(USER_MISMATCH));
    }
    private Admin getByAdminLoginId(String loginId) {
        return adminRepository.findByLoginId(loginId).orElseThrow(
                () -> new NotFoundException("존재하지 않는 관리자"));
    }

}
