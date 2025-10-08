package test.commerce.api.shopper.me;

import commerce.query.IssueShopperToken;
import commerce.view.ShopperMeView;
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

//@DisplayName("접근 토큰을 사용하지 않으면 401 Unauthorized 상태코드를 반환한다")
//@Test
//
//@DisplayName("서로 다른 구매자의 식별자는 서로 다르다")
//@Test
//
//@DisplayName("같은 구매자의 식별자는 항상 같다")
//@Test
//
//@DisplayName("구매자의 기본 정보가 올바르게 설정된다")
//@Test
//
}
