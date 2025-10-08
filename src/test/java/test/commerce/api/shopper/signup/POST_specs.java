package test.commerce.api.shopper.signup;

import commerce.Shopper;
import commerce.ShopperRepository;
import commerce.command.CreateShopperCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import test.commerce.EmailGenerator;
import test.commerce.PasswordGenerator;
import test.commerce.UsernameGenerator;
import test.commerce.api.CommerceApiTest;

import static org.assertj.core.api.Assertions.assertThat;

@CommerceApiTest
@DisplayName("/shopper/signUp")
public class POST_specs {

//- [ ] password 속성이 지정되지 않으면 400 Bad Request 상태코드를 반환한다
//- [ ] password 속성이 올바른 형식을 따르지 않으면 400 Bad Request 상태코드를 반환한다
//- [ ] email 속성에 이미 존재하는 이메일 주소가 지정되면 400 Bad Request 상태코드를 반환한다
//- [ ] username 속성이 이미 존재하는 사용자이름이 지정되면 400 Bad Request 상태코드를 반환한다
//- [ ] 비밀번호를 올바르게 암호화한다

    @DisplayName("올바르게 요청하면 204 No Content 상태코드를 반환한다")
    @Test
    void test1(@Autowired TestRestTemplate client) {
        // Arrange
        var command = new CreateShopperCommand(
            EmailGenerator.generate(),
            UsernameGenerator.generate(),
            PasswordGenerator.generate()
        );
        // Act
        var response = client.postForEntity(
            "/shopper/signUp",
            command,
            Void.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(204);
    }

    @DisplayName("email 속성이 지정되지 않으면 400 Bad Request 상태코드를 반환한다")
    @Test
    void test2(@Autowired TestRestTemplate client) {
        // Arrange
        var command = new CreateShopperCommand(
            null,
            UsernameGenerator.generate(),
            PasswordGenerator.generate()
        );
        // Act
        var response = client.postForEntity(
            "/shopper/signUp",
            command,
            Void.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @DisplayName("email 속성이 올바른 형식을 따르지 않으면 400 Bad Request 상태코드를 반환한다")
    @ParameterizedTest
    @ValueSource(strings = {
        "plainaddress",
        "missingusername@",
        "missingusername@test",
        "missingusername@test.",
        "missingusername@.com",
    })
    void test3(String email, @Autowired TestRestTemplate client) {
        // Arrange
        var command = new CreateShopperCommand(
            email,
            UsernameGenerator.generate(),
            PasswordGenerator.generate()
        );
        // Act
        var response = client.postForEntity(
            "/shopper/signUp",
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
        var command = new CreateShopperCommand(
            EmailGenerator.generate(),
            null,
            PasswordGenerator.generate()
        );
        // Act
        var response = client.postForEntity(
            "/shopper/signUp",
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
        "sh",
        "shopper ",
        "shopper!",
        "shopper@",
    })
    void test5(String username, @Autowired TestRestTemplate client) {
        // Arrange
        var command = new CreateShopperCommand(
            EmailGenerator.generate(),
            username,
            PasswordGenerator.generate()
        );
        // Act
        var response = client.postForEntity(
            "/shopper/signUp",
            command,
            Void.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @DisplayName("username 속성이 올바른 형식을 따르면 204 No Content 상태코드를 반환한다")
    @ParameterizedTest
    @ValueSource(strings = {
        "shopper",
        "ABDSFD",
        "010111122222",
        "shopper_",
        "shopper-",
    })
    void test6(String username, @Autowired TestRestTemplate client) {
        // Arrange
        var command = new CreateShopperCommand(
            EmailGenerator.generate(),
            username,
            PasswordGenerator.generate()
        );
        // Act
        var response = client.postForEntity(
            "/shopper/signUp",
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
        var command = new CreateShopperCommand(
            EmailGenerator.generate(),
            UsernameGenerator.generate(),
            null
        );
        // Act
        var response = client.postForEntity(
            "/shopper/signUp",
            command,
            Void.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @DisplayName("password 속성이 올바른 형식을 따르지 않으면 400 Bad Request 상태코드를 반환한다")
    @ParameterizedTest
    @MethodSource("test.commerce.TestDataSource#invalidPasswords")
    void test8(String password, @Autowired TestRestTemplate client) {
        // Arrange
        var command = new CreateShopperCommand(
            EmailGenerator.generate(),
            UsernameGenerator.generate(),
            password
        );
        // Act
        var response = client.postForEntity(
            "/shopper/signUp",
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

        var command = new CreateShopperCommand(
            email,
            UsernameGenerator.generate(),
            PasswordGenerator.generate()
        );

        client.postForObject(
            "/shopper/signUp",
            command,
            Void.class
        );

        var commandWithDuplicateEmail = new CreateShopperCommand(
            email,
            UsernameGenerator.generate(),
            PasswordGenerator.generate()
        );

        // Act
        var response = client.postForEntity(
            "/shopper/signUp",
            commandWithDuplicateEmail,
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
        var command = new CreateShopperCommand(
            EmailGenerator.generate(),
            username,
            PasswordGenerator.generate()
        );
        client.postForObject(
            "/shopper/signUp",
            command,
            Void.class
        );
        var commandWithDuplicateUsername = new CreateShopperCommand(
            EmailGenerator.generate(),
            username,
            PasswordGenerator.generate()
        );
        // Act
        var response = client.postForEntity(
            "/shopper/signUp",
            commandWithDuplicateUsername,
            Void.class
        );
        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @DisplayName("비밀번호를 올바르게 암호화한다")
    @Test
    void test11(
        @Autowired TestRestTemplate client,
        @Autowired PasswordEncoder passwordEncoder,
        @Autowired ShopperRepository shopperRepository
    ) {
        // Arrange
        var command = new CreateShopperCommand(
            EmailGenerator.generate(),
            UsernameGenerator.generate(),
            PasswordGenerator.generate()
        );

        // Act
        client.postForObject("/shopper/signUp", command, Void.class);

        // Assert
        Shopper shopper = shopperRepository.findAll()
            .stream()
            .filter(s -> s.getEmail().equals(command.email()))
            .findFirst()
            .orElseThrow();

        String actual = shopper.getHashedPassword();
        assertThat(actual).isNotNull();
        assertThat(passwordEncoder.matches(command.password(), actual)).isTrue();
    }
}
