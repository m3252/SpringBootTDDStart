package commerce.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public record ShopperProductsController() {

    @GetMapping("/shopper/products")
    void getProducts() {}
}
