package commerce.api.controller;

import commerce.Seller;
import commerce.SellerRepository;
import commerce.query.IssueSellerToken;
import commerce.result.AccessTokenCarrier;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.spec.SecretKeySpec;
import java.util.Optional;

@RestController
public record SellerIssueTokenController(
    @Value("${security.jwt.secret}") String jwtSecret,
    PasswordEncoder passwordEncoder,
    SellerRepository sellerRepository
) {

    @PostMapping("/seller/issueToken")
    ResponseEntity<AccessTokenCarrier> issueToken(@RequestBody IssueSellerToken query) {
        Optional<Seller> founded = sellerRepository.findByEmail(query.email());

        return founded
            .filter(seller -> passwordEncoder.matches(
                query.password(),
                seller.getHashedPassword())
            )
            .map(seller -> composeToken())
            .map(AccessTokenCarrier::new)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    private String composeToken() {
        return Jwts
            .builder()
            .signWith(new SecretKeySpec(jwtSecret.getBytes(), "HmacSHA256"))
            .compact();
    }
}
