package test.commerce.api.seller.products;

import commerce.view.ArrayCarrier;
import commerce.view.SellerProductView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import test.commerce.api.CommerceApiTest;
import test.commerce.api.TestFixture;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.RequestEntity.*;

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

    //@DisplayName("다른 판매자가 등록한 상품이 포함되지 않는다")
    //@DisplayName("상품 정보를 올바르게 반환한다")
    //@DisplayName("상품 등록 시각을 올바르게 반환한다")
    //@DisplayName("상품 목록을 등록 시점 역순으로 정렬한다")
}
