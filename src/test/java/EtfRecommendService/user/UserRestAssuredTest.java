package EtfRecommendService.user;

import EtfRecommendService.DatabaseCleanup;
import EtfRecommendService.admin.AdminDataSeeder;
import EtfRecommendService.user.dto.*;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;


import java.io.IOException;


@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserRestAssuredTest {

    @LocalServerPort
    int port;

    @Autowired
    DatabaseCleanup databaseCleanup;

    @Autowired
    private AdminDataSeeder adminDataSeeder;

    @BeforeEach
    void setUp() throws IOException {
        databaseCleanup.execute();
        RestAssured.port = port;
        RestAssured.baseURI = "https://localhost";
        RestAssured.useRelaxedHTTPSValidation();
    }

    @Test
    void 멤버생성Test() {
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new UserCreateRequest("user1","123","nick1",false))
                .when()
                .post("/api/v1/join")
                .then().log().all()
                .statusCode(201);
    }

    @Test
    void 로그인Test() {

        UserResponse userResponse = RestAssured

                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new UserCreateRequest("user1", "123", "nick1", false))
                .when()
                .post("/api/v1/join")
                .then().log().all()
                .statusCode(201)
                .extract()
                .as(UserResponse.class);

        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new UserLoginRequest("user1","123", "USER"))
                .when()
                .post("/api/v1/login")
                .then().log().all()
                .statusCode(200);

    }

    @Test
    void 다른비밀번호Test() {
        UserResponse userResponse = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new UserCreateRequest("user1", "123", "nick1", false))
                .when()
                .post("/api/v1/join")
                .then().log().all()
                .statusCode(201)
                .extract()
                .as(UserResponse.class);

        String token = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new UserLoginRequest("user1", "1234", "USER"))
                .when()
                .post("/api/v1/login")
                .then().log().all()
                .statusCode(401)
                .extract()
                .jsonPath()
                .getString("token");
    }

    @Test
    void 회원수정Test() {
        UserResponse userResponse = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new UserCreateRequest("user1", "123", "nick1", false))
                .when()
                .post("/api/v1/join")
                .then().log().all()
                .statusCode(201)
                .extract()
                .as(UserResponse.class);

        String token = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new UserLoginRequest("user1", "123", "USER"))
                .when()
                .post("/api/v1/login")
                .then().log().all()
                .statusCode(200)  // 400에서 200으로 수정
                .extract()
                .cookie("accessToken");

        UserUpdateRequest updateRequest = new UserUpdateRequest("newNick",false);

        UserUpdateResponse updateResponse = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(updateRequest)
                .when()
                .patch("/api/v1/users")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(UserUpdateResponse.class);
        adminDataSeeder.seedAdmin();

        String adminToken = RestAssured.given().log().all()
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
                .cookie("accessToken");


        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + adminToken)
                .body(updateRequest)
                .when()
                .patch("/api/v1/users")
                .then().log().all()
                .statusCode(403);
    }

    @Test
    void 회원삭제Test() {


        UserResponse userResponse = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new UserCreateRequest("user1", "123", "nick1", false))
                .when()
                .post("/api/v1/join")
                .then().log().all()
                .statusCode(201)
                .extract()
                .as(UserResponse.class);

        String token = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new UserLoginRequest("user1", "123", "USER"))
                .when()
                .post("/api/v1/login")
                .then().log().all()
                .statusCode(200)
                .extract()
                .cookie("accessToken");

        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/api/v1/users")
                .then().log().all()
                .statusCode(204)
                .extract();
    }

    @Test
    void 비밀번호재설정Test() {
        Password password = new Password("123");
        Password newPassword = new Password("321");

        UserResponse userResponse = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new UserCreateRequest("user1", "123", "nick1", false))
                .when()
                .post("/api/v1/join")
                .then().log().all()
                .statusCode(201)
                .extract()
                .as(UserResponse.class);

        String token = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new UserLoginRequest("user1", "123", "USER"))
                .when()
                .post("/api/v1/login")
                .then().log().all()
                .statusCode(200)
                .extract()
                .cookie("accessToken");

        UserPasswordResponse response = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(new UserPasswordRequest("123","321","321"))
                .when()
                .patch("/api/v1/users/me/password")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(UserPasswordResponse.class);
    }

    @Test
    void 유저비밀번호와입력비밀번호불일치Test() {
        Password password = new Password("123");
        Password noPassword = new Password("456");
        Password newPassword = new Password("321");
        Password confirmPassword = new Password("4321");

        UserResponse userResponse = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new UserCreateRequest("user1", "123", "nick1", false))
                .when()
                .post("/api/v1/join")
                .then().log().all()
                .statusCode(201)
                .extract()
                .as(UserResponse.class);

        String token = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new UserLoginRequest("user1", "123", "USER"))
                .when()
                .post("/api/v1/login")
                .then().log().all()
                .statusCode(200)
                .extract()
                .cookie("accessToken");

        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(new UserPasswordRequest(noPassword.getHash(),newPassword.getHash(),confirmPassword.getHash()))
                .when()
                .patch("/api/v1/users/me/password")
                .then().log().all()
                .statusCode(401)
                .extract();
    }

    @Test
    void 새비밀번호와확인비밀번호불일치Test() {
        Password password = new Password("123");
        Password newPassword = new Password("321");
        Password confirmPassword = new Password("4321");

        UserResponse userResponse = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new UserCreateRequest("user1", "123", "nick1", false))
                .when()
                .post("/api/v1/join")
                .then().log().all()
                .statusCode(201)
                .extract()
                .as(UserResponse.class);

        String token = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new UserLoginRequest("user1", "123", "USER"))
                .when()
                .post("/api/v1/login")
                .then().log().all()
                .statusCode(200)
                .extract()
                .cookie("accessToken");

        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(new UserPasswordRequest(password.getHash(),newPassword.getHash(),confirmPassword.getHash()))
                .when()
                .patch("/api/v1/users/me/password")
                .then().log().all()
                .statusCode(401)
                .extract();
    }

    @Test
    void 같은비밀번호입력Test() {
        Password password = new Password("123");

        UserResponse userResponse = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new UserCreateRequest("user1", "123", "nick1", false))
                .when()
                .post("/api/v1/join")
                .then().log().all()
                .statusCode(201)
                .extract()
                .as(UserResponse.class);

        String token = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new UserLoginRequest("user1", "123", "USER"))
                .when()
                .post("/api/v1/login")
                .then().log().all()
                .statusCode(200)
                .extract()
                .cookie("accessToken");

        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(new UserPasswordRequest("123","123","123"))
                .when()
                .patch("/api/v1/users/me/password")
                .then().log().all()
                    .statusCode(500)
                .extract();
    }

    @Test
    void 공백입력() {
        Password password = new Password("123");

        UserResponse userResponse = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new UserCreateRequest("user1", "123", "nick1", false))
                .when()
                .post("/api/v1/join")
                .then().log().all()
                .statusCode(201)
                .extract()
                .as(UserResponse.class);

        String token = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new UserLoginRequest("user1", "123", "USER"))
                .when()
                .post("/api/v1/login")
                .then().log().all()
                .statusCode(200)
                .extract()
                .cookie("accessToken");

        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(new UserPasswordRequest(password.getHash(),null,null))
                .when()
                .patch("/api/v1/users/me/password")
                .then().log().all()
                .statusCode(500)
                .extract();
    }

    @Test
    void 회원정보조회Test() {
        Password password = new Password("123");

        UserResponse userResponse = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new UserCreateRequest("user1", "123", "nick1", false))
                .when()
                .post("/api/v1/join")
                .then().log().all()
                .statusCode(201)
                .extract()
                .as(UserResponse.class);

        String token = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new UserLoginRequest("user1","123", "USER"))
                .when()
                .post("/api/v1/login")
                .then().log().all()
                .statusCode(200)
                .extract()
                .cookie("accessToken");

        Long userId = userResponse.id();

        UserDetailResponse detailResponse = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/v1/users/{userId}", userId)
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(UserDetailResponse.class);


    }
}
