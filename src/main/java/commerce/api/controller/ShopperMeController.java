package commerce.api.controller;

import java.security.Principal;
import java.util.UUID;

import commerce.Seller;
import commerce.SellerRepository;
import commerce.Shopper;
import commerce.ShopperRepository;
import commerce.view.SellerMeView;
import commerce.view.ShopperMeView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public record ShopperMeController(ShopperRepository shopperRepository) {

    @GetMapping("/shopper/me")
    ShopperMeView me(Principal user) {
        UUID id = UUID.fromString(user.getName());
        Shopper shopper = shopperRepository.findById(id).orElseThrow();
        return new ShopperMeView(id, shopper.getEmail(), shopper.getUsername());
    }
}
