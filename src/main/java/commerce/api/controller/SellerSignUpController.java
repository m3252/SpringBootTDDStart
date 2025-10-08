package commerce.api.controller;

import commerce.Seller;
import commerce.SellerRepository;
import commerce.command.CreateSellerCommand;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static commerce.UserPropertyValidator.*;

@RestController
public record SellerSignUpController(
    PasswordEncoder passwordEncoder,
    SellerRepository sellerRepository
) {

    @PostMapping("/seller/signUp")
    ResponseEntity<?> signUp(@RequestBody CreateSellerCommand command) {
        if (!isCommandValid(command)) {
            return ResponseEntity.badRequest().build();
        }

        Seller seller = new Seller();
        seller.setEmail(command.email());
        seller.setUsername(command.username());
        String hashedPassword = passwordEncoder.encode(command.password());
        seller.setHashedPassword(hashedPassword);

        sellerRepository.save(seller);

        return ResponseEntity.noContent().build();
    }

    private static boolean isCommandValid(CreateSellerCommand command) {
        boolean emailValid = isEmailValid(command.email());
        boolean usernameValid = isUsernameValid(command.username());
        boolean passwordValid = isPasswordValid(command.password());

        return isCommandValid(emailValid, usernameValid, passwordValid);
    }

    private static boolean isCommandValid(boolean emailValid, boolean usernameValid, boolean passwordValid) {
        return emailValid && usernameValid && passwordValid;
    }
}
