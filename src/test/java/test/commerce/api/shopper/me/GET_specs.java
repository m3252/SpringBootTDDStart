package test.commerce.api.shopper.me;

import commerce.view.ShopperMeView;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import test.commerce.EmailGenerator;
import test.commerce.PasswordGenerator;
import test.commerce.UsernameGenerator;
import test.commerce.api.CommerceApiTest;
import test.commerce.api.TestFixture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.RequestEntity.*;

@CommerceApiTest
@DisplayName("GET /shopper/me")
public class GET_specs {

    @DisplayName("올바르게 요청하면 200 OK 상태코드를 반환한다")
    @Test
    void test1(@Autowired TestFixture fixture) {
        // Arrange
        String email = EmailGenerator.generate();
        String password = PasswordGenerator.generate();

        fixture.createShopper(email, UsernameGenerator.generate(), password);
        String token = fixture.issueShopperToken(email, password);

        // Act
        ResponseEntity<ShopperMeView> response = fixture.client().exchange(
            get("/shopper/me")
                .header("Authorization", "Bearer " + token)
                .build(),
            ShopperMeView.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }

    @DisplayName("접근 토큰을 사용하지 않으면 401 Unauthorized 상태코드를 반환한다")
    @Test
    void test2(@Autowired TestFixture fixture) {
        // Act
        ResponseEntity<ShopperMeView> response = fixture.client().exchange(
            get("/shopper/me").build(),
            ShopperMeView.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(401);
    }


    @DisplayName("서로 다른 구매자의 식별자는 서로 다르다")
    @Test
    void test3(@Autowired TestFixture fixture) {
        // Arrange
        String token1 = fixture.createShopperThenIssueToken();
        String token2 = fixture.createShopperThenIssueToken();

        // Act
        ResponseEntity<ShopperMeView> response1 = fixture.client().exchange(
            get("/shopper/me")
                .header("Authorization", "Bearer " + token1)
                .build(),
            ShopperMeView.class
        );

        ResponseEntity<ShopperMeView> response2 = fixture.client().exchange(
            get("/shopper/me")
                .header("Authorization", "Bearer " + token2)
                .build(),
            ShopperMeView.class
        );

        // Assert
        Assertions.assertNotNull(response1.getBody());
        Assertions.assertNotNull(response2.getBody());
        assertThat(response1.getBody().id()).isNotEqualTo(response2.getBody().id());
    }

    @DisplayName("같은 구매자의 식별자는 항상 같다")
    @Test
    void test4(@Autowired TestFixture fixture) {
        // Arrange
        String email = EmailGenerator.generate();
        String password = PasswordGenerator.generate();

        fixture.createShopper(email, UsernameGenerator.generate(), password);
        String token1 = fixture.issueShopperToken(email, password);
        String token2 = fixture.issueShopperToken(email, password);

        // Act
        ResponseEntity<ShopperMeView> response1 = fixture.client().exchange(
            get("/shopper/me")
                .header("Authorization", "Bearer " + token1)
                .build(),
            ShopperMeView.class
        );

        ResponseEntity<ShopperMeView> response2 = fixture.client().exchange(
            get("/shopper/me")
                .header("Authorization", "Bearer " + token2)
                .build(),
            ShopperMeView.class
        );

        // Assert
        Assertions.assertNotNull(response1.getBody());
        Assertions.assertNotNull(response2.getBody());
        assertThat(response1.getBody().id()).isEqualTo(response2.getBody().id());
    }


    @DisplayName("구매자의 기본 정보가 올바르게 설정된다")
    @Test
    void test5(@Autowired TestFixture fixture) {
        // Arrange
        String username = UsernameGenerator.generate();
        String email = EmailGenerator.generate();
        String password = PasswordGenerator.generate();

        fixture.createShopper(email, username, password);
        fixture.setShopperAsDefaultUser(email, password);

        // Act
        ResponseEntity<ShopperMeView> response = fixture.client().exchange(
            get("/shopper/me").build(),
            ShopperMeView.class
        );

        // Assert
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().username()).isEqualTo(username);
        assertThat(response.getBody().email()).isEqualTo(email);
    }

}
