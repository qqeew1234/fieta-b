package EtfRecommendService.user.dto;

import EtfRecommendService.user.Password;

public record UserPasswordRequest(String existingPassword, String newPassword, String confirmNewPassword) {
}
