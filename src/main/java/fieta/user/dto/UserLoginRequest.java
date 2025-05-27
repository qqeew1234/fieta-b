package fieta.user.dto;

import lombok.Builder;

@Builder
public record UserLoginRequest(
        String loginId,
        String password,
        //Role: ADMIN, USER
        String role) {
}
