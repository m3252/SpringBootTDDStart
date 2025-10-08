package test.commerce.api.shopper.issuetoken;

import commerce.command.CreateShopperCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import test.commerce.EmailGenerator;
import test.commerce.PasswordGenerator;
import test.commerce.UsernameGenerator;
import test.commerce.api.CommerceApiTest;

import static org.assertj.core.api.Assertions.assertThat;

@CommerceApiTest
@DisplayName("POST /shopper/issueToken")
public class POST_specs {

    @Test
    @DisplayName("올바르게 요청하면 200 OK 상태코드와 접근 토큰을 반환한다")
    void test1(@Autowired TestRestTemplate client) {
        // Arrange
        String email = EmailGenerator.generate();
        String password = PasswordGenerator.generate();

        client.postForObject(
            "/shopper/signUp",
            new CreateShopperCommand(
                email,
                UsernameGenerator.generate(),
                password
            ),
            Void.class
        );

        // Act
        var response = client.postForEntity(
            "/shopper/issueToken",
            new commerce.query.IssueShopperToken(
                email,
                password
            ),
            commerce.result.AccessTokenCarrier.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().accessToken()).isNotNull();
    }

    @DisplayName("접근 토큰은 JWT 형식을 따른다")
    @Test
    void test2(@Autowired TestRestTemplate client) {
        // Arrange
        String email = EmailGenerator.generate();
        String password = PasswordGenerator.generate();

        client.postForObject(
            "/shopper/signUp",
            new CreateShopperCommand(
                email,
                UsernameGenerator.generate(),
                password
            ),
            Void.class
        );

        // Act
        var response = client.postForEntity(
            "/shopper/issueToken",
            new commerce.query.IssueShopperToken(
                email,
                password
            ),
            commerce.result.AccessTokenCarrier.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().accessToken()).isNotNull();
        assertThat(response.getBody().accessToken().split("\\.")).hasSize(3);
    }

    @DisplayName("존재하지 않는 이메일 주소가 사용되면 400 Bad Request 상태코드를 반환한다")
    @Test
    void test3(@Autowired TestRestTemplate client) {
        // Arrange
        String email = EmailGenerator.generate();
        String password = PasswordGenerator.generate();
        client.postForObject(
            "/shopper/signUp",
            new CreateShopperCommand(
                email,
                UsernameGenerator.generate(),
                password
            ),
            Void.class
        );
        // Act
        var response = client.postForEntity(
            "/shopper/issueToken",
            new commerce.query.IssueShopperToken(
                EmailGenerator.generate(), // 존재하지 않는 이메일
                password
            ),
            commerce.result.AccessTokenCarrier.class
        );
        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @DisplayName("잘못된 비밀번호가 사용되면 400 Bad Request 상태코드를 반환한다")
    @Test
    void test4(@Autowired TestRestTemplate client) {
        // Arrange
        String email = EmailGenerator.generate();
        String password = PasswordGenerator.generate();
        client.postForObject(
            "/shopper/signUp",
            new CreateShopperCommand(
                email,
                UsernameGenerator.generate(),
                password
            ),
            Void.class
        );
        // Act
        var response = client.postForEntity(
            "/shopper/issueToken",
            new commerce.query.IssueShopperToken(
                email,
                PasswordGenerator.generate() // 잘못된 비밀번호
            ),
            commerce.result.AccessTokenCarrier.class
        );
        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);

    }
}
