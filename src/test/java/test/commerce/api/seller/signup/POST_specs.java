package test.commerce.api.seller.signup;

import commerce.Seller;
import commerce.SellerRepository;
import commerce.command.CreateSellerCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import test.commerce.EmailGenerator;
import test.commerce.PasswordGenerator;
import test.commerce.UsernameGenerator;
import test.commerce.api.CommerceApiTest;

import static org.assertj.core.api.Assertions.assertThat;

@CommerceApiTest
@DisplayName("POST /api/seller/signUp")
public class POST_specs {

    @Test
    @DisplayName("올바르게 요청하면 204 No Content 상태코드를 반환한다")
    void test1(@Autowired TestRestTemplate client) {
        // Arrange
        CreateSellerCommand command = new CreateSellerCommand(
            EmailGenerator.generate(),
            UsernameGenerator.generate(),
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
            UsernameGenerator.generate(),
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
            UsernameGenerator.generate(),
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

    @DisplayName("username 속성이 지정되지 않으면 400 Bad Request 상태코드를 반환한다")
    @Test
    void test4(@Autowired TestRestTemplate client) {
        // Arrange
        var command = new CreateSellerCommand(
            EmailGenerator.generate(),
            null,
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

    @DisplayName("username 속성이 올바른 형식을 따르지 않으면 400 Bad Request 상태코드를 반환한다")
    @ParameterizedTest
    @ValueSource(strings = {
        "",
        "se",
        "seller ",
        "seller!",
        "seller@",
    })
    void test5(String username, @Autowired TestRestTemplate client) {
        // Arrange
        var command = new CreateSellerCommand(
            EmailGenerator.generate(),
            username,
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

    @DisplayName("username 속성이 올바른 형식을 따르면 204 No Content 상태코드를 반환한다")
    @ParameterizedTest
    @ValueSource(strings = {
        "seller",
        "ABDSFD",
        "010111122222",
        "seller_",
        "seller-",
    })
    void test6(String username, @Autowired TestRestTemplate client) {
        // Arrange
        var command = new CreateSellerCommand(
            EmailGenerator.generate(),
            username,
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

    @DisplayName("password 속성이 지정되지 않으면 400 Bad Request 상태코드를 반환한다")
    @Test
    void test7(@Autowired TestRestTemplate client) {
        // Arrange
        var command = new CreateSellerCommand(
            EmailGenerator.generate(),
            UsernameGenerator.generate(),
            ""
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

    @DisplayName("password 속성이 올바른 형식을 따르지 않으면 400 Bad Request 상태코드를 반환한다")
    @ParameterizedTest
    @ValueSource(strings = {
        "",
        "pass",
        "passwor",
    })
    void test8(String password, @Autowired TestRestTemplate client) {
        // Arrange
        var command = new CreateSellerCommand(
            EmailGenerator.generate(),
            UsernameGenerator.generate(),
            password
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

    @DisplayName("email 속성에 이미 존재하는 이메일 주소가 지정되면 400 Bad Request 상태코드를 반환한다")
    @Test
    void test9(@Autowired TestRestTemplate client) {
        // Arrange
        String email = EmailGenerator.generate();

        client.postForEntity(
            "/seller/signUp",
            new CreateSellerCommand(email, UsernameGenerator.generate(), "password"),
            Void.class
        );

        // Act
        ResponseEntity<Void> response = client.postForEntity(
            "/seller/signUp",
            new CreateSellerCommand(email, UsernameGenerator.generate(), "password"),
            Void.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @DisplayName("username 속성에 이미 존재하는 사용자이름이 지정되면 400 Bad Request 상태코드를 반환한다")
    @Test
    void test10(@Autowired TestRestTemplate client) {
        // Arrange
        String username = UsernameGenerator.generate();

        client.postForEntity(
            "/seller/signUp",
            new CreateSellerCommand(EmailGenerator.generate(), username, "password"),
            Void.class
        );

        // Act
        ResponseEntity<Void> response = client.postForEntity(
            "/seller/signUp",
            new CreateSellerCommand(EmailGenerator.generate(), username, "password"),
            Void.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @DisplayName("비밀번호를 올바르게 암호화한다")
    @Test
    void test11(
        @Autowired TestRestTemplate client,
        @Autowired SellerRepository sellerRepository,
        @Autowired PasswordEncoder passwordEncoder
    ) {

        // Arrange
        String password = PasswordGenerator.generate();
        CreateSellerCommand command = new CreateSellerCommand(
            EmailGenerator.generate(),
            UsernameGenerator.generate(),
            password
        );

        // Act
        client.postForEntity("/seller/signUp", command, Void.class);

        // Assert
        Seller seller = sellerRepository.findAll()
            .stream()
            .filter(it -> it.getEmail().equals(command.email()))
            .findAny()
            .orElseThrow();

        String actual = seller.getHashedPassword();
        assertThat(actual).isNotNull();
        assertThat(passwordEncoder.matches(password, actual)).isTrue();
    }
}
