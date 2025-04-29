package EtfRecommendService.user.dto;

import EtfRecommendService.user.Password;

public record UserPasswordRequest(Password existingPassword, Password newPassword, Password confirmNewPassword) {
}
