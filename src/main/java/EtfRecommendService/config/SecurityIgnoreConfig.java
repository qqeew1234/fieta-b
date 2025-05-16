package EtfRecommendService.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

@Configuration
public class SecurityIgnoreConfig {

    /**
     * H2 콘솔 관련 모든 요청을 스프링 시큐리티의 필터 체인에서 완전히 무시합니다.
     * 이렇게 하면 formLogin, CSRF, 세션 정책 등이 전혀 적용되지 않아
     * 브라우저 기반 H2 로그인 화면이 정상 동작합니다.
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web
                .ignoring()
                .requestMatchers(PathRequest.toH2Console());
    }
}
