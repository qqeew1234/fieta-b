package EtfRecommendService.loginUtils;

import EtfRecommendService.security.UserDetail;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;

@Component
public class JwtProvider {

    // 에러 로깅을 위해 로거 준비
    private static final Logger logger = LoggerFactory.getLogger(JwtProvider.class);

    // JWT secret을 저장할 변수
    private final SecretKey secretKey;

    // 토큰 만료 시간을 저장할 변수
    private final Long expirationInMilliseconds;

    private final SecretKey refreshSecret;

    private final Long refreshExpirationInMilliseconds;

    // 생성자 함수
    public JwtProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.expiration-time}") Long expirationInMilliseconds,
            @Value("${jwt.refresh.secret}") String refreshSecret,
            @Value("${jwt.refresh.expiration-time}") Long refreshExpirationInMilliseconds
    ) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.expirationInMilliseconds = expirationInMilliseconds;
        keyBytes = Decoders.BASE64.decode(refreshSecret);
        this.refreshSecret = Keys.hmacShaKeyFor(keyBytes);
        this.refreshExpirationInMilliseconds = refreshExpirationInMilliseconds;
    }

    private Claims createClaims(UserDetails userDetail){
        Date now = new Date();
        Date expiration = new Date(now.getTime() + this.expirationInMilliseconds);
        Claims claims = Jwts.claims()
                .setSubject(userDetail.getUsername())       // "sub": "abc@gmail.com"
                .setIssuedAt(now)          // "iat": 1516239022
                .setExpiration(expiration);// "exp": 1516249022
        List<String> roles = userDetail.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        claims.put("roles",roles);
        return claims;
    }

    // 토큰을 만들어 내는 함수
    public String createToken(UserDetails userDetail) {
        return Jwts.builder()
                .setClaims(createClaims(userDetail))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public String createRefreshToken(UserDetails userDetail) {
        return Jwts.builder()
                .setClaims(createClaims(userDetail))
                .signWith(refreshSecret, SignatureAlgorithm.HS512)
                .compact();
    }


    // 유효한 토큰인지 검증하는 함수
    public Boolean isValidToken(String token, boolean isAccess) {
        try {
            if (isAccess){
                parseToken(token); // 토큰 데이터를 읽는 함수를 검증용으로 활용
            }
            else {
                parseRefreshToken(token);
            }
            return true; // 읽는 도중 에러가 발생하지 않았으면 true를 return
        } catch (ExpiredJwtException e) {
            logger.error("Token expired", e);
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token", e);
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token", e);
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature", e);
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty", e);
        }
        return false;
    }

    // 토큰에서 로그인한 사용자의 email을 추출하는 함수
    public String getSubject(String token) {
        return parseToken(token)
                .getSubject();
    }
    public String getSubjectFromRefresh(String token) {
        return parseRefreshToken(token)
                .getSubject();
    }

    // 토큰에서 로그인한 사용자의 토큰 만료기간을 추출하는 함수
    public Date getExpirationFromRefreshToken(String token) {
        return parseRefreshToken(token)
                .getExpiration();
    }

    public List<String> getRolesFromRefresh(String token) {
        Object ob =  parseRefreshToken(token)
                .get("roles");

        List<String> roles = new ArrayList<>();
        if (ob instanceof List<?>) {
            for (Object role : (List<?>) ob) {
                roles.add(String.valueOf(role));
            }
            return roles;
        }
        return roles;
    }

    // 유효한 토큰의 데이터를 읽는 함수
    private Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Claims parseRefreshToken(String refreshToken) {
        return Jwts.parserBuilder()
                .setSigningKey(refreshSecret)
                .build()
                .parseClaimsJws(refreshToken)
                .getBody();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseToken(token);
        String username = getSubject(token);
        @SuppressWarnings("unchecked")
        List<String> roles = claims.get("roles", List.class);
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(roles.get(0).split(","))
                        .map(SimpleGrantedAuthority::new)
                        .toList();
        UserDetails userDetails = new UserDetail(username, null, authorities);
        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
    }
}