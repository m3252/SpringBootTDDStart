package commerce.api.controller;

import commerce.command.CreateSellerCommand;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public record SellerSignUpController() {

    @PostMapping("/sellers/signUp")
    ResponseEntity<?> signUp(@RequestBody CreateSellerCommand command) {
        if (command.email() == null) {
            return ResponseEntity.badRequest().build();
        } else {
            return ResponseEntity.noContent().build();
        }
    }
}
