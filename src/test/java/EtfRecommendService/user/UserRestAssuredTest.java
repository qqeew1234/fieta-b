package EtfRecommendService.user;

import Etf.DatabaseCleanup;
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

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserRestAssuredTest {

    @LocalServerPort
    int port;

    @Autowired
    DatabaseCleanup databaseCleanup;

    @BeforeEach
    void setUp() throws IOException {
        databaseCleanup.execute();
        RestAssured.port = port;
    }

    @Test
    void 멤버생성Test() {
        Password password = new Password("123");

        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new CreateUserRequest("user1",password,"nick1",false))
                .when()
                .post("/api/v1/users")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    void 로그인Test() {
        Password password = new Password("123");

        UserResponse userResponse = RestAssured

                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new CreateUserRequest("user1", password, "nick1", false))
                .when()
                .post("/api/v1/users")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(UserResponse.class);

        UserLoginResponse loginResponse = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new UserLoginRequest("user1",password))
                .when()
                .post("/api/v1/users/login")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(UserLoginResponse.class);
    }

    @Test
    void 다른비밀번호Test() {
        Password password = new Password("123");
        Password wrongPassword = new Password("1234");

        UserResponse userResponse = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new CreateUserRequest("user1", password, "nick1", false))
                .when()
                .post("/api/v1/users")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(UserResponse.class);

        String token = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new UserLoginRequest("user1", wrongPassword))
                .when()
                .post("/api/v1/users/login")
                .then().log().all()
                .statusCode(401)
                .extract()
                .jsonPath()
                .getString("token");
    }

    @Test
    void 회원수정Test() {
        Password password = new Password("123");

        UserResponse userResponse = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new CreateUserRequest("user1", password, "nick1", false))
                .when()
                .post("/api/v1/users")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(UserResponse.class);

        String token = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new UserLoginRequest("user1", password))
                .when()
                .post("/api/v1/users/login")
                .then().log().all()
                .statusCode(200)  // 400에서 200으로 수정
                .extract()
                .jsonPath()
                .getString("token");

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
    }

    @Test
    void 회원삭제Test() {
        Password password = new Password("123");

        UserResponse userResponse = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new CreateUserRequest("user1", password, "nick1", false))
                .when()
                .post("/api/v1/users")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(UserResponse.class);

        String token = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new UserLoginRequest("user1", password))
                .when()
                .post("/api/v1/users/login")
                .then().log().all()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getString("token");

        UserDeleteResponse deleteResponse = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/api/v1/users")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(UserDeleteResponse.class);
    }

    @Test
    void 비밀번호재설정Test() {
        Password password = new Password("123");
        Password newPassword = new Password("321");

        UserResponse userResponse = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new CreateUserRequest("user1", password, "nick1", false))
                .when()
                .post("/api/v1/users")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(UserResponse.class);

        String token = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new UserLoginRequest("user1", password))
                .when()
                .post("/api/v1/users/login")
                .then().log().all()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getString("token");

        UserPasswordResponse response = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(new UserPasswordRequest(password,newPassword,newPassword))
                .when()
                .post("/api/v1/users/me/password")
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
                .body(new CreateUserRequest("user1", password, "nick1", false))
                .when()
                .post("/api/v1/users")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(UserResponse.class);

        String token = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new UserLoginRequest("user1", password))
                .when()
                .post("/api/v1/users/login")
                .then().log().all()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getString("token");

        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(new UserPasswordRequest(noPassword,newPassword,confirmPassword))
                .when()
                .post("/api/v1/users/me/password")
                .then().log().all()
                .statusCode(500)
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
                .body(new CreateUserRequest("user1", password, "nick1", false))
                .when()
                .post("/api/v1/users")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(UserResponse.class);

        String token = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new UserLoginRequest("user1", password))
                .when()
                .post("/api/v1/users/login")
                .then().log().all()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getString("token");

        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(new UserPasswordRequest(password,newPassword,confirmPassword))
                .when()
                .post("/api/v1/users/me/password")
                .then().log().all()
                .statusCode(500)
                .extract();
    }

    @Test
    void 같은비밀번호입력Test() {
        Password password = new Password("123");

        UserResponse userResponse = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new CreateUserRequest("user1", password, "nick1", false))
                .when()
                .post("/api/v1/users")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(UserResponse.class);

        String token = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new UserLoginRequest("user1", password))
                .when()
                .post("/api/v1/users/login")
                .then().log().all()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getString("token");

        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(new UserPasswordRequest(password,password,password))
                .when()
                .post("/api/v1/users/me/password")
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
                .body(new CreateUserRequest("user1", password, "nick1", false))
                .when()
                .post("/api/v1/users")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(UserResponse.class);

        String token = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new UserLoginRequest("user1", password))
                .when()
                .post("/api/v1/users/login")
                .then().log().all()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getString("token");

        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(new UserPasswordRequest(password,null,null))
                .when()
                .post("/api/v1/users/me/password")
                .then().log().all()
                .statusCode(500)
                .extract();
    }

    @Test
    void 유저조회() {
        Password password = new Password("123");

        UserResponse userResponse = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new CreateUserRequest("user1", password, "nick1", false))
                .when()
                .post("/api/v1/users")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(UserResponse.class);

        String token = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new UserLoginRequest("user1", password))
                .when()
                .post("/api/v1/users/login")
                .then().log().all()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getString("token");

        MypageResponse mypageResponse = RestAssured
                .given()
                .pathParam("userId", userResponse.id())
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/v1/users/{userId}")
                .then()
                .statusCode(200)
                .extract()
                .as(MypageResponse.class);

        assertThat(mypageResponse.id()).isEqualTo(userResponse.id());
        assertThat(mypageResponse.nickName()).isEqualTo(userResponse.nickName());
    }

}
