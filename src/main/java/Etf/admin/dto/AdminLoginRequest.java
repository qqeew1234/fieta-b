package Etf.admin.dto;

import Etf.user.Password;

public record AdminLoginRequest(String loginId, Password password) {
}


