package test.commerce.api.seller.products;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import test.commerce.api.CommerceApiTest;
import test.commerce.api.TestFixture;

import java.net.URI;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static test.commerce.RegisterProductCommandGenerator.generateRegisterProductCommand;
import static test.commerce.RegisterProductCommandGenerator.generateRegisterProductCommandWithImageUri;

@CommerceApiTest
@DisplayName("POST /seller/products")
public class POST_specs {

    @DisplayName("올바르게 요청하면 201 Created 상태코드를 반환한다")
    @Test
    void test1(@Autowired TestFixture fixture) {
        // Arrange
        fixture.createSellerThenSetAsDefaultUser();

        // Act
        ResponseEntity<Void> response = fixture.client().postForEntity(
            "/seller/products",
            generateRegisterProductCommand(),
            Void.class
        );
        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(201);
    }

    @DisplayName("판매자가 아닌 사용자의 접근 토큰을 사용하면 403 Forbidden 상태코드를 반환한다")
    @Test
    void test2(@Autowired TestFixture fixture) {
        // Arrange
        fixture.createShopperThenAsDefaultUser();

        // Act
        ResponseEntity<Void> response = fixture.client().postForEntity(
            "/seller/products",
            generateRegisterProductCommand(),
            Void.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(403);
    }

    @DisplayName("imageUri 속성이 URI 형식을 따르지 않으면 400 Bad Request 상태코드를 반환한다")
    @ParameterizedTest
    @ValueSource(strings = {
        "invalid-uri",
        "http://",
        "://example.com/image.png",
    })
    void test3(String imageUri, @Autowired TestFixture fixture) {
        // Arrange
        fixture.createSellerThenSetAsDefaultUser();

        // Act
        ResponseEntity<Void> response = fixture.client().postForEntity(
            "/seller/products",
            generateRegisterProductCommandWithImageUri(imageUri),
            Void.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @DisplayName("올바르게 요청하면 등록된 상품 정보에 접근하는 Location 헤더를 반환한다")
    @Test
    void test4(@Autowired TestFixture fixture) {
        // Arrange
        fixture.createSellerThenSetAsDefaultUser();

        // Act
        ResponseEntity<Void> response = fixture.client().postForEntity(
            "/seller/products",
            generateRegisterProductCommand(),
            Void.class
        );

        // Assert
        URI actual = response.getHeaders().getLocation();
        assertThat(actual).isNotNull();
        assertThat(actual.isAbsolute()).isFalse();
        assertThat(actual.getPath()).startsWith("/seller/products/").matches(endWithUUID -> {
            String[] parts = endWithUUID.split("/");
            String lastPart = parts[parts.length - 1];
            try {
                UUID.fromString(lastPart);
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        });
    }
}
