package EtfRecommendService.user.dto;

import EtfRecommendService.user.Password;

public record UserLoginRequest(String loginId, Password password) {
}
