package test.commerce.api.seller.me;

import commerce.command.CreateSellerCommand;
import commerce.query.IssueSellerToken;
import commerce.result.AccessTokenCarrier;
import commerce.view.SellerMeView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import test.commerce.EmailGenerator;
import test.commerce.PasswordGenerator;
import test.commerce.UsernameGenerator;
import test.commerce.api.CommerceApiTest;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.RequestEntity.get;

@CommerceApiTest
@DisplayName("GET /seller/me")
public class GET_specs {

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
        String email1 = EmailGenerator.generate();
        String username1 = UsernameGenerator.generate();
        String password1 = PasswordGenerator.generate();

        var command1 = new CreateSellerCommand(email1, username1, password1);
        client.postForEntity("/seller/signUp", command1, Void.class);

        AccessTokenCarrier carrier1 = client.postForObject(
            "/seller/issueToken",
            new IssueSellerToken(email1, password1),
            AccessTokenCarrier.class
        );
        String token1 = carrier1.accessToken();

        String email2 = EmailGenerator.generate();
        String username2 = UsernameGenerator.generate();
        String password2 = PasswordGenerator.generate();

        var command2 = new CreateSellerCommand(email2, username2, password2);
        client.postForEntity("/seller/signUp", command2, Void.class);

        AccessTokenCarrier carrier2 = client.postForObject(
            "/seller/issueToken",
            new IssueSellerToken(email2, password2),
            AccessTokenCarrier.class
        );
        String token2 = carrier2.accessToken();

        // Act
        ResponseEntity<SellerMeView> response1 = client.exchange(
            get("/seller/me")
                .header("Authorization", "Bearer " + token1)
                .build(),
            SellerMeView.class
        );

        ResponseEntity<SellerMeView> response2 = client.exchange(
            get("/seller/me")
                .header("Authorization", "Bearer " + token2)
                .build(),
            SellerMeView.class
        );

        // Assert
        assertThat(requireNonNull(response1.getBody()).id())
            .isNotEqualTo(requireNonNull(response2.getBody()).id());
    }

    @DisplayName("같은 판매자의 식별자는 항상 같다")
    @Test
    void test4(@Autowired TestRestTemplate client) {
        // Arrange
        String email = EmailGenerator.generate();
        String username = UsernameGenerator.generate();
        String password = PasswordGenerator.generate();

        var command = new CreateSellerCommand(email, username, password);
        client.postForEntity("/seller/signUp", command, Void.class);

        AccessTokenCarrier carrier1 = client.postForObject(
            "/seller/issueToken",
            new IssueSellerToken(email, password),
            AccessTokenCarrier.class
        );

        String token1 = carrier1.accessToken();

        AccessTokenCarrier carrier2 = client.postForObject(
            "/seller/issueToken",
            new IssueSellerToken(email, password),
            AccessTokenCarrier.class
        );
        String token2 = carrier2.accessToken();

        // Act
        ResponseEntity<SellerMeView> response1 = client.exchange(
            get("/seller/me")
                .header("Authorization", "Bearer " + token1)
                .build(),
            SellerMeView.class
        );

        ResponseEntity<SellerMeView> response2 = client.exchange(
            get("/seller/me")
                .header("Authorization", "Bearer " + token2)
                .build(),
            SellerMeView.class
        );

        // Assert
        assertThat(requireNonNull(response1.getBody()).id())
            .isEqualTo(requireNonNull(response2.getBody()).id());
    }

    @DisplayName("판매자의 기본 정보가 올바르게 설정된다")
    @Test
    void test5(@Autowired TestRestTemplate client) {
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
        SellerMeView sellerMeView = requireNonNull(response.getBody());
        assertThat(sellerMeView.username()).isEqualTo(username);
        assertThat(sellerMeView.email()).isEqualTo(email);
    }

    @DisplayName("문의 이메일 주소를 올바르게 설정한다")
    @Test
    void test6(@Autowired TestRestTemplate client) {
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
        SellerMeView sellerMeView = requireNonNull(response.getBody());
        assertThat(sellerMeView.email()).isEqualTo(email);
    }
}
