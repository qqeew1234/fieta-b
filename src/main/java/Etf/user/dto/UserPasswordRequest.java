package Etf.user.dto;

import Etf.user.Password;

public record UserPasswordRequest(Password existingPassword, Password newPassword, Password confirmNewPassword) {
}
