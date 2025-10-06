package commerce.api.controller;

import commerce.command.CreateSellerCommand;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public record SellerSignUpController() {

    @PostMapping("/seller/signUp")
    ResponseEntity<?> signUp(@RequestBody CreateSellerCommand command) {
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
        String usernameRegex = "^[a-z]*$";
        if (command.email() == null) {
            return ResponseEntity.badRequest().build();
        } else if (!command.email().contains("@")) {
            return ResponseEntity.badRequest().build();
        } else if (command.email().endsWith("@")) {
            return ResponseEntity.badRequest().build();
        } else if (!command.email().matches(emailRegex)) {
            return ResponseEntity.badRequest().build();
        } else if (command.username() == null) {
            return ResponseEntity.badRequest().build();
        } else if (command.username().isBlank()) {
            return ResponseEntity.badRequest().build();
        } else if (command.username().length() < 3) {
            return ResponseEntity.badRequest().build();
        } else if (!command.username().matches(usernameRegex)) {
            return ResponseEntity.badRequest().build();
        } else {
            return ResponseEntity.noContent().build();
        }
    }
}
