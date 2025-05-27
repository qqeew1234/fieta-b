package fieta.refreshtoken;

import fieta.AcceptanceTest;
import fieta.admin.AdminDataSeeder;
import fieta.loginUtils.JwtTokens;
import fieta.user.dto.UserLoginRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
public class RefreshTokenTest extends AcceptanceTest {

    @Autowired
    private AdminDataSeeder adminDataSeeder;

    @Test
    void 리프레쉬토큰_재발급() {
        adminDataSeeder.seedAdmin();

        JwtTokens tokens = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(UserLoginRequest
                        .builder()
                        .loginId("admin")
                        .password("password")
                        .role("ADMIN")
                        .build())
                .when()
                .post("/api/v1/login")
                .then().log().all()
                .statusCode(200)
                .extract()
                        .as(JwtTokens.class);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(tokens.refreshToken())
                .when()
                .post("/api/v1/refresh")
                .then().log().all()
                .statusCode(200);
    }
}
