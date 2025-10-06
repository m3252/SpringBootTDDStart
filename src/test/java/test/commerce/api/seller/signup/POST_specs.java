package test.commerce.api.seller.signup;

import commerce.CommerceApiApp;
import commerce.command.CreateSellerCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
    classes = CommerceApiApp.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@DisplayName("POST /api/seller/signUp")
public class POST_specs {

    @Test
    @DisplayName("올바르게 요청하면 204 No Content 상태코드를 반환한다")
    void test1(@Autowired TestRestTemplate client) {
        // Arrange
        CreateSellerCommand command = new CreateSellerCommand(
            "seller@test.com",
            "seller",
            "password"
        );

        // Act
        ResponseEntity<Void> response = client.postForEntity(
            "/seller/signUp",
            command,
            Void.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(204);
    }

    @Test
    @DisplayName("email 속성이 지정되지 않으면 400 Bad Request 상태코드를 반환한다")
    void test2(@Autowired TestRestTemplate client) {
        // Arrange
        var command = new CreateSellerCommand(
            null,
            "seller",
            "password"
        );

        // Act
        ResponseEntity<Void> response = client.postForEntity(
            "/seller/signUp",
            command,
            Void.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "plainaddress",
        "missingusername@",
        "missingusername@test",
        "missingusername@test.",
        "missingusername@.com",
    })
    @DisplayName("email 속성이 올바른 형식을 따르지 않으면 400 Bad Request 상태코드를 반환한다")
    void test3(String email, @Autowired TestRestTemplate client) {
        // Arrange
        var command = new CreateSellerCommand(
            email,
            "seller",
            "password"
        );

        // Act
        ResponseEntity<Void> response = client.postForEntity(
            "/seller/signUp",
            command,
            Void.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }
}
