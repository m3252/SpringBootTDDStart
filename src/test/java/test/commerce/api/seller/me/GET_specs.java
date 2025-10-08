package test.commerce.api.seller.me;

import commerce.command.CreateSellerCommand;
import commerce.result.AccessTokenCarrier;
import commerce.view.SellerMeView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import test.commerce.EmailGenerator;
import test.commerce.PasswordGenerator;
import test.commerce.UsernameGenerator;
import test.commerce.api.CommerceApiTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.RequestEntity.get;

@CommerceApiTest
@DisplayName("GET /seller/me")
public class GET_specs {


//- [ ] 서로 다른 판매자의 식별자는 서로 다르다
//- [ ] 같은 판매자의 식별자는 항상 같다
//- [ ] 판매자의 기본 정보가 올바르게 설정된다
//- [ ] 문의 이메일 주소를 올바르게 설정한다

    @DisplayName("올바르게 요청하면 200 OK 상태코드를 반환한다")
    @Test
    void test1(@Autowired TestRestTemplate client) {
        // Arrange
        String email = EmailGenerator.generate();
        String username = UsernameGenerator.generate();
        String password = PasswordGenerator.generate();

        var command = new CreateSellerCommand(email, username, password);
        client.postForEntity("/seller/signUp", command, Void.class);

        AccessTokenCarrier carrier = client.postForObject(
            "/seller/issueToken",
            command,
            AccessTokenCarrier.class
        );

        String token = carrier.accessToken();

        // Act
        ResponseEntity<SellerMeView> response = client.exchange(
            get("/seller/me")
                .header("Authorization", "Bearer " + token)
                .build(),
            SellerMeView.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }

    @DisplayName("접근 토큰을 사용하지 않으면 401 Unauthorized 상태코드를 반환한다")
    @Test
    void test2(@Autowired TestRestTemplate client) {
        // Act
        ResponseEntity<Void> response = client.getForEntity(
            "/seller/me",
            Void.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(401);
    }

    @DisplayName("서로 다른 판매자의 식별자는 서로 다르다")
    @Test
    void test3(@Autowired TestRestTemplate client) {
        // Arrange

    }
}
