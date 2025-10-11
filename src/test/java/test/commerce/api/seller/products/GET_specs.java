package test.commerce.api.seller.products;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import commerce.command.RegisterProductCommand;
import commerce.view.ArrayCarrier;
import commerce.view.ProductView;
import commerce.view.SellerProductView;
import org.assertj.core.api.ThrowingConsumer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import test.commerce.ProductAssertion;
import test.commerce.api.CommerceApiTest;
import test.commerce.api.TestFixture;

import static java.time.ZoneOffset.UTC;
import static java.time.temporal.ChronoUnit.*;
import static java.util.Comparator.reverseOrder;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.springframework.http.RequestEntity.get;
import static test.commerce.RegisterProductCommandGenerator.generateRegisterProductCommand;

@CommerceApiTest
@DisplayName("GET /seller/products")
public class GET_specs {

    @DisplayName("올바르게 요청하면 200 OK 상태코드를 반환한다")
    @Test
    void test1(@Autowired TestFixture fixture) {
        // Arrange
        fixture.createSellerThenSetAsDefaultUser();

        // Act
        ResponseEntity<ArrayCarrier<SellerProductView>> exchange = fixture.client().exchange(
            get("/seller/products").build(),
            new ParameterizedTypeReference<>() { }
        );

        // Assert
        assertThat(exchange.getStatusCode().value()).isEqualTo(200);
    }

    @DisplayName("판매자가 등록한 모든 상품을 반환한다")
    @Test
    void test2(@Autowired TestFixture fixture) {
        // Arrange
        fixture.createSellerThenSetAsDefaultUser();
        List<UUID> ids = fixture.registerProducts();

        // Act
        ResponseEntity<ArrayCarrier<SellerProductView>> exchange =
            fixture.client().exchange(
                get("/seller/products").build(),
                new ParameterizedTypeReference<>() { }
            );

        // Assert
        assertThat(exchange.getBody()).isNotNull();
        assertThat(exchange.getBody().items())
            .extracting(SellerProductView::id)
            .containsAll(ids);
    }

    @DisplayName("다른 판매자가 등록한 상품이 포함되지 않는다")
    @Test
    void test3(@Autowired TestFixture fixture) {
        // Arrange
        fixture.createSellerThenSetAsDefaultUser();
        UUID unexpected = fixture.registerProduct();

        fixture.createSellerThenSetAsDefaultUser();
        fixture.registerProducts();

        // Act
        ResponseEntity<ArrayCarrier<SellerProductView>> exchange =
            fixture.client().exchange(
                get("/seller/products").build(),
                new ParameterizedTypeReference<>() { }
            );

        // Assert
        assertThat(exchange.getBody()).isNotNull();
        assertThat(exchange.getBody().items())
            .extracting(SellerProductView::id)
            .doesNotContain(unexpected);
    }

    @DisplayName("상품 정보를 올바르게 반환한다")
    @Test
    void test4(@Autowired TestFixture fixture) {
        // Arrange
        fixture.createSellerThenSetAsDefaultUser();
        RegisterProductCommand command = generateRegisterProductCommand();
        UUID id = fixture.registerProduct(command);

        // Act
        ResponseEntity<ArrayCarrier<SellerProductView>> response = fixture.client().exchange(
            get("/seller/products").build(),
            new ParameterizedTypeReference<>() { }
        );

        // Assert
        ArrayCarrier<SellerProductView> body = response.getBody();
        SellerProductView actual = requireNonNull(body).items()[0];
        assertThat(actual).satisfies(ProductAssertion.isDerivedFrom(command));
    }

    @DisplayName("상품 등록 시각을 올바르게 반환한다")
    @Test
    void test5(@Autowired TestFixture fixture) {
        // Arrange
        fixture.createSellerThenSetAsDefaultUser();
        LocalDateTime referenceTime = LocalDateTime.now(UTC);
        UUID id = fixture.registerProduct();

        // Act
        ResponseEntity<ArrayCarrier<SellerProductView>> response = fixture.client().exchange(
            get("/seller/products").build(),
            new ParameterizedTypeReference<>() { }
        );
        // Assert
        ArrayCarrier<SellerProductView> body = response.getBody();
        SellerProductView actual = requireNonNull(body).items()[0];
        assertThat(actual.registeredTimeUtc())
            .isCloseTo(referenceTime, within(1, SECONDS))
            .isNotNull();
    }

    @DisplayName("상품 목록을 등록 시점 역순으로 정렬한다")
    @Test
    void test6(@Autowired TestFixture fixture) {

        // Arrange
        fixture.createSellerThenSetAsDefaultUser();
        fixture.registerProducts();

        // Act
        ResponseEntity<ArrayCarrier<SellerProductView>> response =
            fixture.client().exchange(
                get("/seller/products").build(),
                new ParameterizedTypeReference<>() { }
            );

        // Assert
        assertThat(requireNonNull(response.getBody()).items())
            .extracting(SellerProductView::registeredTimeUtc)
            .isSortedAccordingTo(reverseOrder());
    }
}

