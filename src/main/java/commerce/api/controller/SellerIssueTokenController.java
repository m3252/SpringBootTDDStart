package commerce.api.controller;

import java.util.Optional;

import commerce.Seller;
import commerce.SellerRepository;
import commerce.api.JwtKeyHolder;
import commerce.query.IssueSellerToken;
import commerce.result.AccessTokenCarrier;
import io.jsonwebtoken.Jwts;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public record SellerIssueTokenController(
    JwtKeyHolder jwtKeyHolder,
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
            .map(this::composeToken)
            .map(AccessTokenCarrier::new)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    private String composeToken(Seller seller) {
        return Jwts
            .builder()
            .setSubject(seller.getId().toString())
            .claim("scp", "seller")
            .signWith(jwtKeyHolder.key())
            .compact();
    }
}
