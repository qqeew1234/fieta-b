package EtfRecommendService.admin;

import EtfRecommendService.AcceptanceTest;
import EtfRecommendService.user.dto.UserLoginRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
public class AdminApiTest extends AcceptanceTest {

    @Autowired
    private AdminDataSeeder adminDataSeeder;

    @Test
    void adminLogin() {
        adminDataSeeder.seedAdmin();

        RestAssured.given().log().all()
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
                .statusCode(200);
    }

    @Test
    void adminLoginFail() {
        adminDataSeeder.seedAdmin();

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(UserLoginRequest
                        .builder()
                        .loginId("admin")
                        .password("password")
                        .role("USER")
                        .build())
                .when()
                .post("/api/v1/login")
                .then().log().all()
                .statusCode(401);
    }
}
