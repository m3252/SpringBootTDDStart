

package test.commerce.api.seller.products.id;

import java.time.LocalDateTime;
import java.util.UUID;

import commerce.command.RegisterProductCommand;
import commerce.view.SellerProductView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import test.commerce.api.CommerceApiTest;
import test.commerce.api.TestFixture;

import static test.commerce.RegisterProductCommandGenerator.generateRegisterProductCommand;
import static java.time.ZoneOffset.UTC;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;


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

        fixture.createShopperThenSetAsDefaultUser();

        // Act
        ResponseEntity<?> response = fixture.client().getForEntity("/seller/products/{id}", SellerProductView.class, id);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(403);
    }

    @DisplayName("존재하지 않는 상품 식별자를 사용하면 404 Not Found 상태코드를 반환한다")
    @Test
    void test3(@Autowired TestFixture fixture) {
        // Arrange
        fixture.createSellerThenSetAsDefaultUser();
        fixture.registerProduct();

        UUID nonExistentId = UUID.randomUUID();

        // Act
        ResponseEntity<?> response = fixture.client().getForEntity("/seller/products/{id}", SellerProductView.class, nonExistentId);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(404);
    }

    @DisplayName("다른 판매자가 등록한 상품 식별자를 사용하면 404 Not Found 상태코드를 반환한다")
    @Test
    void test4(@Autowired TestFixture fixture) {
        // Arrange
        fixture.createSellerThenSetAsDefaultUser();
        UUID id = fixture.registerProduct();

        fixture.createSellerThenSetAsDefaultUser();

        // Act
        ResponseEntity<?> response = fixture.client().getForEntity("/seller/products/{id}", SellerProductView.class, id);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(404);
    }

    @DisplayName("상품 식별자를 올바르게 반환한다")
    @Test
    void test5(@Autowired TestFixture fixture) {
        // Arrange
        fixture.createSellerThenSetAsDefaultUser();
        UUID id = fixture.registerProduct();

        // Act
        SellerProductView actual = fixture.client().getForObject(
                "/seller/products/{id}",
                SellerProductView.class,
                id
        );

        // Assert
        assertThat(actual).isNotNull();
        assertThat(actual.id()).isEqualTo(id);
    }

    @DisplayName("상품 정보를 올바르게 반환한다")
    @Test
    void test6(@Autowired TestFixture fixture) {
        // Arrange
        fixture.createSellerThenSetAsDefaultUser();
        RegisterProductCommand command = generateRegisterProductCommand();
        UUID id = fixture.registerProduct(command);

        // Act
        SellerProductView actual = fixture.client().getForObject(
                "/seller/products/" + id,
                SellerProductView.class
        );

        // Assert
        assertThat(actual).isNotNull();
        assertThat(actual.name()).isEqualTo(command.name());
        assertThat(actual.imageUri()).isEqualTo(command.imageUri());
        assertThat(actual.description()).isEqualTo(command.description());
        assertThat(actual.priceAmount()).matches(x -> x.compareTo(command.priceAmount()) == 0);
        assertThat(actual.stockQuantity()).isEqualTo(command.stockQuantity());
    }

    @DisplayName("상품 등록 시각을 올바르게 반환한다")
    @Test
    void test7(@Autowired TestFixture fixture) {
        // Arrange
        fixture.createSellerThenSetAsDefaultUser();
        LocalDateTime referenceTime = LocalDateTime.now(UTC);
        UUID id = fixture.registerProduct();

        // Act
        SellerProductView actual = fixture.client().getForObject(
                "/seller/products/{id}",
                SellerProductView.class,
                id
        );

        // Assert
        assertThat(actual.registeredTimeUtc())
                .isCloseTo(referenceTime, within(1, SECONDS));
    }
}

