package EtfRecommendService.refreshtoken;

import EtfRecommendService.AcceptanceTest;
import EtfRecommendService.admin.AdminDataSeeder;
import EtfRecommendService.user.dto.UserLoginRequest;
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

        String refreshToken = RestAssured.given().log().all()
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
                .cookie("refreshToken");

        System.out.println(refreshToken);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie("refreshToken", refreshToken)
                .when()
                .post("/api/v1/refresh")
                .then().log().all()
                .statusCode(200);

    }
}
