package EtfRecommendService.user.dto;

import EtfRecommendService.user.Password;
import lombok.Builder;

@Builder
public record UserLoginRequest(
        String loginId,
        String password,
        //Role: ADMIN, USER
        String role) {
}
