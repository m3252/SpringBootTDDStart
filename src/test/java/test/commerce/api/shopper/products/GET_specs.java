package test.commerce.api.shopper.products;

import commerce.result.PageCarrier;
import commerce.view.ProductView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import test.commerce.api.CommerceApiTest;
import test.commerce.api.TestFixture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.RequestEntity.*;

@CommerceApiTest
@DisplayName("GET /shopper/products")
public class GET_specs {

    @DisplayName("올바르게 요청하면 200 OK 상태코드를 반환한다")
    @Test
    void test1(@Autowired TestFixture fixture) {
        // Arrange
        fixture.createShopperThenAsDefaultUser();

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
//    @DisplayName("첫 번째 페이지의 상품을 반환한다")
//    @DisplayName("상품 목록을 등록 시점 역순으로 정렬한다")
//    @DisplayName("상품 정보를 올바르게 반환한다")
//    @DisplayName("판매자 정보를 올바르게 반환한다")
//    @DisplayName("두 번째 페이지를 올바르게 반환한다")
//    @DisplayName("마지막 페이지를 올바르게 반환한다")
//    @DisplayName("continuationToken 매개변수에 빈 문자열이 지정되면 첫 번째 페이지를 반환한다")
//    @DisplayName("문의 이메일 주소를 올바르게 설정한다")
}
