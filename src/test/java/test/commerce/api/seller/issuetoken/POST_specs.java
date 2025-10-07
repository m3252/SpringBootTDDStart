package test.commerce.api.seller.issuetoken;

import commerce.CommerceApiApp;
import commerce.command.CreateSellerCommand;
import commerce.query.IssueSellerToken;
import commerce.result.AccessTokenCarrier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import test.commerce.EmailGenerator;
import test.commerce.PasswordGenerator;
import test.commerce.UsernameGenerator;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static test.commerce.JwtAssertions.conformsToJwtFormat;

@SpringBootTest(
    classes = CommerceApiApp.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@DisplayName("POST /seller/issueToken")
public class POST_specs {

    @DisplayName("올바르게 요청하면 200 OK 상태코드를 반환한다")
    @Test
    void test1(@Autowired TestRestTemplate client) {
        // Arrange
        String email = EmailGenerator.generate();
        String password = PasswordGenerator.generate();
        client.postForObject(
            "/seller/signUp",
            new CreateSellerCommand(
                email,
                UsernameGenerator.generate(),
                password
            ),
            Void.class
        );

        // Act
        ResponseEntity<AccessTokenCarrier> response = client.postForEntity(
            "/seller/issueToken",
            new IssueSellerToken(
                email,
                password
            ),
            AccessTokenCarrier.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }

    @DisplayName("올바르게 요청하면 접근 토큰을 반환한다")
    @Test
    void test2(@Autowired TestRestTemplate client) {
        // Arrange
        String email = EmailGenerator.generate();
        String password = PasswordGenerator.generate();

        client.postForObject(
            "/seller/signUp",
            new CreateSellerCommand(
                email,
                UsernameGenerator.generate(),
                password
            ),
            Void.class
        );

        // Act
        ResponseEntity<AccessTokenCarrier> response = client.postForEntity(
            "/seller/issueToken",
            new IssueSellerToken(
                email,
                password
            ),
            AccessTokenCarrier.class
        );

        // Assert
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().accessToken()).isNotBlank();
    }

    @DisplayName("접근 토큰은 JWT 형식을 따른다")
    @Test
    void test3(@Autowired TestRestTemplate client) {
        // Arrange
        String email = EmailGenerator.generate();
        String password = PasswordGenerator.generate();

        client.postForObject(
            "/seller/signUp",
            new CreateSellerCommand(email, UsernameGenerator.generate(), password),
            Void.class
        );

        // Act
        ResponseEntity<AccessTokenCarrier> response = client.postForEntity(
            "/seller/issueToken",
            new IssueSellerToken(email, password),
            AccessTokenCarrier.class
        );

        // Assert
        String actual = requireNonNull(response.getBody()).accessToken();
        assertThat(actual).satisfies(conformsToJwtFormat());
    }
}
