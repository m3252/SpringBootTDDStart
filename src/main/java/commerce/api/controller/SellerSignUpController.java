package commerce.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public record SellerSignUpController() {

    @PostMapping("/sellers/signUp")
    ResponseEntity<?> signUp() {
        return ResponseEntity.noContent().build();
    }
}
