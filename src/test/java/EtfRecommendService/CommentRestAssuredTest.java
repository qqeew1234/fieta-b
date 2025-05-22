package EtfRecommendService;

import EtfRecommendService.comment.dto.CommentCreateRequest;
import EtfRecommendService.comment.dto.CommentUpdateRequest;
import EtfRecommendService.loginUtils.JwtProvider;
import EtfRecommendService.security.UserDetail;
import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@ActiveProfiles("test")
public class CommentRestAssuredTest extends AcceptanceTest {


    @Autowired
    JwtProvider jwtProvider;



    // 댓글 생성 테스트
    @DisplayName("댓글 생성 테스트")
    @Test
    void 댓글생성테스트() {

        UserDetails userDetails = new UserDetail("pepero",null, List.of(new SimpleGrantedAuthority("ROLE_"+"USER")));

        String token = jwtProvider.createToken(userDetails);

        RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .body(new CommentCreateRequest(1L, "이 ETF 대박나게 해주세요"))
                .when()
                .post("/api/v1/comments")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
    }


    // 댓글 삭제 테스트
    @DisplayName("댓글 삭제 테스트")
    @Test
    void 댓글삭제테스트() {

        UserDetails userDetails = new UserDetail("pepero",null, List.of(new SimpleGrantedAuthority("ROLE_"+"USER")));

        String token = jwtProvider.createToken(userDetails);

        RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + token)

                .contentType("application/json")
                .body(new CommentCreateRequest(1L, "이 ETF 대박나게 해주세요"))
                .when()
                .post("/api/v1/comments")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());


//        Password password = new Password("123");
//
//        RestAssured
//                .given().log().all()
//                .contentType(ContentType.JSON)
//                .body(new CreateUserRequest("pepero", password, "nick1", false))
//                .when()
//                .post("/api/v1/users")
//                .then().log().all()
//                .statusCode(HttpStatus.OK.value());


        RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .pathParam("commentId", 1)
                .when()
                .delete("/api/v1/comments/{commentId}")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
    }


    // 댓글 수정 테스트
    @DisplayName("댓글 수정 테스트")
    @Test
    void 댓글수정테스트() {


        UserDetails userDetails = new UserDetail("pepero",null, List.of(new SimpleGrantedAuthority("ROLE_"+"USER")));

        String token = jwtProvider.createToken(userDetails);


        RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .body(new CommentCreateRequest(1L, "이 ETF 대박나게 해주세요"))
                .when()
                .post("/api/v1/comments")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .pathParam("commentId", 1)

                .body(new CommentUpdateRequest("이글을 믿고 1000%가 올랐어요"))
                .when()
                .patch("/api/v1/comments/{commentId}")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
    }


    // 댓글 좋아요 테스트
    @DisplayName("댓글 좋아요 테스트")
    @Test
    void 댓글좋아요테스트() {

        UserDetails userDetails = new UserDetail("pepero",null, List.of(new SimpleGrantedAuthority("ROLE_"+"USER")));

        String token = jwtProvider.createToken(userDetails);


        RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .body(new CommentCreateRequest(1L, "이 ETF 대박나게 해주세요"))
                .when()
                .post("/api/v1/comments")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .pathParam("commentId", 1)
                .when()
                .post("/api/v1/comments/{commentId}/likes")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

    }


    // 댓글 좋아요 취소 테스트
    @DisplayName("댓글 좋아요 취소 테스트")
    @Test
    void 댓글좋아요취소테스트() {

        UserDetails userDetails = new UserDetail("pepero",null, List.of(new SimpleGrantedAuthority("ROLE_"+"USER")));

        String token = jwtProvider.createToken(userDetails);

        RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .body(new CommentCreateRequest(1L, "이 ETF 대박나게 해주세요"))
                .when()
                .post("/api/v1/comments")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .pathParam("commentId", 1)
                .when()
                .post("/api/v1/comments/{commentId}/likes")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .pathParam("commentId", 1)
                .when()
                .post("/api/v1/comments/{commentId}/likes")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

    }


    // 댓글 조회 테스트
    @DisplayName("댓글 조회 테스트")
    @Test
    void 댓글조회테스트() {

        UserDetails userDetails = new UserDetail("pepero",null, List.of(new SimpleGrantedAuthority("ROLE_"+"USER")));

        String token = jwtProvider.createToken(userDetails);

        Long etfId = 1L;
        int page = 0;
        int size = 20;

        RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .body(new CommentCreateRequest(1L, "이 ETF 대박나게 해주세요"))
                .when()
                .post("/api/v1/comments")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .queryParam("etf_id", etfId)
                .queryParam("page", page)
                .queryParam("size", size)
                .when()
                .get("/api/v1/comments")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

    }
}