package test.commerce.api.shopper.products;

import commerce.command.RegisterProductCommand;
import commerce.result.PageCarrier;
import commerce.view.ProductView;
import commerce.view.SellerMeView;
import commerce.view.SellerView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import test.commerce.api.CommerceApiTest;
import test.commerce.api.TestFixture;

import java.util.List;
import java.util.UUID;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.RequestEntity.*;
import static test.commerce.ProductAssertion.isViewDerivedFrom;
import static test.commerce.RegisterProductCommandGenerator.generateRegisterProductCommand;

@CommerceApiTest
@DisplayName("GET /shopper/products")
public class GET_specs {

    public static final int PAGE_SIZE = 10;

    @DisplayName("올바르게 요청하면 200 OK 상태코드를 반환한다")
    @Test
    void test1(@Autowired TestFixture fixture) {
        // Arrange
        fixture.createShopperThenSetAsDefaultUser();

        // Act
        ResponseEntity<PageCarrier<ProductView>> response =
            fixture.client().exchange(
                get("/shopper/products").build(),
                new ParameterizedTypeReference<>() { }
            );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }

    @DisplayName("판매자 접근 토큰을 사용하면 403 Forbidden 상태코드를 반환한다")
    @Test
    void test2(@Autowired TestFixture fixture) {
        // Arrange
        fixture.createSellerThenSetAsDefaultUser();

        // Act
        ResponseEntity<PageCarrier<ProductView>> response =
            fixture.client().exchange(
                get("/shopper/products").build(),
                new ParameterizedTypeReference<>() { }
            );
        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(403);
    }

    @DisplayName("첫 번째 페이지의 상품을 반환한다")
    @Test
    void test3(@Autowired TestFixture fixture) {
        // Arrange
        fixture.deleteAllProducts();

        fixture.createSellerThenSetAsDefaultUser();
        List<UUID> uuids = fixture.registerProducts(PAGE_SIZE);

        fixture.createShopperThenSetAsDefaultUser();

        // Act
        ResponseEntity<PageCarrier<ProductView>> response =
            fixture.client().exchange(
                get("/shopper/products").build(),
                new ParameterizedTypeReference<>() { }
            );

        // Assert
        PageCarrier<ProductView> actual = response.getBody();
        assertThat(actual).isNotNull();
        assertThat(actual.items()).extracting(ProductView::id).containsAll(uuids);
    }

    @DisplayName("상품 목록을 등록 시점 역순으로 정렬한다")
    @Test
    void test4(@Autowired TestFixture fixture) {
        // Arrange
        fixture.deleteAllProducts();

        fixture.createSellerThenSetAsDefaultUser();
        UUID uuids1 = fixture.registerProduct();
        UUID uuids2 = fixture.registerProduct();
        UUID uuids3 = fixture.registerProduct();

        fixture.createShopperThenSetAsDefaultUser();

        // Act
        ResponseEntity<PageCarrier<ProductView>> response =
            fixture.client().exchange(
                get("/shopper/products").build(),
                new ParameterizedTypeReference<>() { }
            );
        // Assert
        PageCarrier<ProductView> actual = response.getBody();
        assertThat(actual).isNotNull();
        assertThat(actual.items()).extracting(ProductView::id)
            .containsExactly(uuids3, uuids2, uuids1);
    }

    @DisplayName("상품 정보를 올바르게 반환한다")
    @Test
    void test5(@Autowired TestFixture fixture) {
        // Arrange
        fixture.deleteAllProducts();

        fixture.createSellerThenSetAsDefaultUser();
        RegisterProductCommand command = generateRegisterProductCommand();
        fixture.registerProduct(command);

        fixture.createShopperThenSetAsDefaultUser();

        // Act
        ResponseEntity<PageCarrier<ProductView>> response =
            fixture.client().exchange(
                get("/shopper/products").build(),
                new ParameterizedTypeReference<>() { }
            );

        // Assert
        ProductView actual = requireNonNull(response.getBody()).items()[0];
        assertThat(actual).satisfies(isViewDerivedFrom(command));
    }

    @DisplayName("판매자 정보를 올바르게 반환한다")
    @Test
    void test6(@Autowired TestFixture fixture) {
        // Arrange
        fixture.deleteAllProducts();
        fixture.createSellerThenSetAsDefaultUser();
        SellerMeView seller = fixture.getSeller();

        fixture.registerProduct();
        fixture.createShopperThenSetAsDefaultUser();

        // Act
        ResponseEntity<PageCarrier<ProductView>> response =
            fixture.client().exchange(
                get("/shopper/products").build(),
                new ParameterizedTypeReference<>() { }
            );

        // Assert
        PageCarrier<ProductView> body = response.getBody();
        SellerView actual = requireNonNull(body).items()[0].seller();
        assertThat(actual).isNotNull();
        assertThat(actual.id()).isEqualTo(seller.id());
        assertThat(actual.username()).isEqualTo(seller.username());
    }

    @DisplayName("두 번째 페이지를 올바르게 반환한다")
    @Test
    void test7(@Autowired TestFixture fixture) {
        // Arrange
        fixture.deleteAllProducts();

        fixture.createSellerThenSetAsDefaultUser();
        fixture.registerProducts(PAGE_SIZE / 2);
        List<UUID> ids = fixture.registerProducts(PAGE_SIZE);
        fixture.registerProducts(PAGE_SIZE);

        fixture.createShopperThenSetAsDefaultUser();
        String token = fixture.consumeProductPage();

        // Act
        ResponseEntity<PageCarrier<ProductView>> response =
            fixture.client().exchange(
                get("/shopper/products?continuationToken=" + token).build(),
                new ParameterizedTypeReference<>() { }
            );

        // Assert
        assertThat(requireNonNull(response.getBody()).items())
            .extracting(ProductView::id)
            .containsExactlyElementsOf(ids.reversed());
    }

    @DisplayName("마지막 페이지를 올바르게 반환한다")
    @ParameterizedTest
    @ValueSource(ints = { 1, PAGE_SIZE })
    void test8(
        int lastPageSize,
        @Autowired TestFixture fixture
    ) {
        // Arrange
        fixture.deleteAllProducts();

        fixture.createSellerThenSetAsDefaultUser();
        List<UUID> ids = fixture.registerProducts(lastPageSize);
        fixture.registerProducts(PAGE_SIZE * 2);

        fixture.createShopperThenSetAsDefaultUser();
        String token = fixture.consumeTwoProductPages();

        // Act
        ResponseEntity<PageCarrier<ProductView>> response =
            fixture.client().exchange(
                get("/shopper/products?continuationToken=" + token).build(),
                new ParameterizedTypeReference<>() { }
            );

        // Assert
        PageCarrier<ProductView> actual = response.getBody();
        assertThat(requireNonNull(actual).items())
            .extracting(ProductView::id)
            .containsExactlyElementsOf(ids.reversed());
        assertThat(actual.continuationToken()).isNull();
    }

    @DisplayName("continuationToken 매개변수에 빈 문자열이 지정되면 첫 번째 페이지를 반환한다")
    @Test
    void test9(@Autowired TestFixture fixture) {
        // Arrange
        fixture.deleteAllProducts();

        fixture.createSellerThenSetAsDefaultUser();
        fixture.registerProducts(PAGE_SIZE);
        List<UUID> ids = fixture.registerProducts(PAGE_SIZE);

        fixture.createShopperThenSetAsDefaultUser();

        // Act
        ResponseEntity<PageCarrier<ProductView>> response =
            fixture.client().exchange(
                get("/shopper/products?continuationToken=").build(),
                new ParameterizedTypeReference<>() { }
            );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(requireNonNull(response.getBody()).items())
            .extracting(ProductView::id)
            .containsAll(ids);
    }
//    @DisplayName("문의 이메일 주소를 올바르게 설정한다")
}
