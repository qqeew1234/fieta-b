package fieta.user.dto;

public record UserPasswordRequest(String existingPassword, String newPassword, String confirmNewPassword) {
}
