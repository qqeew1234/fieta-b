package Etf.user.dto;

import Etf.user.Password;

public record CreateUserRequest(
        String loginId,
        Password password,
        String nickName,
        Boolean isLikePrivate) {
}
