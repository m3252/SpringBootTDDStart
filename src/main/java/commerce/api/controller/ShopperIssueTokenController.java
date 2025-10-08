package commerce.api.controller;

import java.util.Optional;

import commerce.Shopper;
import commerce.ShopperRepository;
import commerce.api.JwtKeyHolder;
import commerce.query.IssueShopperToken;
import commerce.result.AccessTokenCarrier;
import io.jsonwebtoken.Jwts;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public record ShopperIssueTokenController(
    JwtKeyHolder jwtKeyHolder,
    PasswordEncoder passwordEncoder,
    ShopperRepository shopperRepository
) {

    @PostMapping("/shopper/issueToken")
    ResponseEntity<AccessTokenCarrier> issueToken(@RequestBody IssueShopperToken query) {
        Optional<Shopper> founded = shopperRepository.findByEmail(query.email());

        return founded
            .filter(shopper -> passwordEncoder.matches(
                query.password(),
                shopper.getHashedPassword())
            )
            .map(this::composeToken)
            .map(AccessTokenCarrier::new)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    private String composeToken(Shopper shopper) {
        return Jwts
            .builder()
            .setSubject(shopper.getId().toString())
            .signWith(jwtKeyHolder.key())
            .compact();
    }
}
