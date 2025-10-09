package test.commerce.api.seller.products.id;

import commerce.command.SellerProductView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import test.commerce.api.CommerceApiTest;
import test.commerce.api.TestFixture;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@CommerceApiTest
@DisplayName("GET /seller/products/{id}")
public class GET_specs {

    @DisplayName("올바르게 요청하면 200 OK 상태코드를 반환한다")
    @Test
    void test1(@Autowired TestFixture fixture) {
        // Arrange
        fixture.createSellerThenSetAsDefaultUser();
        UUID id = fixture.registerProduct();

        // Act
        ResponseEntity<?> response = fixture.client().getForEntity("/seller/products/{id}", Void.class, id);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }

    @DisplayName("판매자가 아닌 사용자의 접근 토큰을 사용하면 403 Forbidden 상태코드를 반환한다")
    @Test
    void test2(@Autowired TestFixture fixture) {
        // Arrange
        fixture.createSellerThenSetAsDefaultUser();
        UUID id = fixture.registerProduct();

        fixture.createShopperThenAsDefaultUser();

        // Act
        ResponseEntity<?> response = fixture.client().getForEntity("/seller/products/{id}", SellerProductView.class, id);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(403);
    }

//     @DisplayName("존재하지 않는 상품 식별자를 사용하면 404 Not Found 상태코드를 반환한다");
//     @DisplayName("다른 판매자가 등록한 상품 식별자를 사용하면 404 Not Found 상태코드를 반환한다");
//     @DisplayName("상품 식별자를 올바르게 반환한다");
//     @DisplayName("상품 정보를 올바르게 반환한다");
//     @DisplayName("상품 등록 시각을 올바르게 반환한다");
}
