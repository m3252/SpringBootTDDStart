package commerce.api.controller;

import java.net.URI;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import commerce.Product;
import commerce.ProductRepository;
import commerce.command.RegisterProductCommand;
import commerce.commandmodel.InvalidCommandException;
import commerce.view.ArrayCarrier;
import commerce.view.SellerProductView;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static java.time.ZoneOffset.UTC;
import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;

@RestController
public record SellerProductsController(ProductRepository repository) {

    @PostMapping("/seller/products")
    ResponseEntity<?> registerProduct(
        @RequestBody RegisterProductCommand command,
        Principal user
    ) {
        if (!isValidUri(command.imageUri())) {
            throw new InvalidCommandException();
        }

        UUID id = UUID.randomUUID();
        var product = new Product();
        product.setId(id);
        product.setSellerId(UUID.fromString(user.getName()));
        product.setName(command.name());
        product.setImageUri(command.imageUri());
        product.setDescription(command.description());
        product.setPriceAmount(command.priceAmount());
        product.setStockQuantity(command.stockQuantity());
        product.setRegisteredTimeUtc(LocalDateTime.now(UTC));
        repository.save(product);
        URI location = URI.create("/seller/products/" + id);
        return ResponseEntity.created(location).build();
    }

    private boolean isValidUri(String value) {
        try {
            URI uri = URI.create(value);
            return uri.getHost() != null;
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    @GetMapping("/seller/products/{id}")
    ResponseEntity<?> findProduct(@PathVariable UUID id, Principal user) {
        UUID sellerId = UUID.fromString(user.getName());
        return repository
            .findById(id)
            .filter(product -> product.getSellerId().equals(sellerId))
            .map(SellerProductsController::convertToView)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/seller/products")
    ResponseEntity<?> getProducts(Principal user) {
        SellerProductView[] items = repository.findBySellerId(UUID.fromString(user.getName()))
            .stream()
            .sorted(comparing(Product::getRegisteredTimeUtc, reverseOrder()))
            .map(SellerProductsController::convertToView)
            .toArray(SellerProductView[]::new);

        return ResponseEntity.ok(new ArrayCarrier<>(items));
    }

    private static SellerProductView convertToView(Product product) {
        return new SellerProductView(
            product.getId(),
            product.getName(),
            product.getImageUri(),
            product.getDescription(),
            product.getPriceAmount(),
            product.getStockQuantity(),
            product.getRegisteredTimeUtc()
        );
    }
}
