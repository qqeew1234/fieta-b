package EtfRecommendService.user;

import lombok.Builder;

@Builder
public record RefreshRequest(String refreshToken) {
}
