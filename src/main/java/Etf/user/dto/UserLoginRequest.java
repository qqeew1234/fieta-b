package Etf.user.dto;

import Etf.user.Password;

public record UserLoginRequest(String loginId, Password password) {
}
