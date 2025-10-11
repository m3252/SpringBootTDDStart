package commerce.api.controller;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import commerce.Product;
import commerce.ProductRepository;
import commerce.command.RegisterProductCommand;
import commerce.commandmodel.RegisterProductCommandExecutor;
import commerce.query.FindSellerProduct;
import commerce.query.GetSellerProducts;
import commerce.querymodel.FindSellerProductQueryProcessor;
import commerce.querymodel.GetSellerProductQueryProcessor;
import commerce.view.ArrayCarrier;
import commerce.view.SellerProductView;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static java.util.Comparator.comparing;

@RestController
public record SellerProductsController(ProductRepository repository) {

    @PostMapping("/seller/products")
    ResponseEntity<?> registerProduct(
        @RequestBody RegisterProductCommand command,
        Principal user
    ) {
        UUID id = UUID.randomUUID();
        UUID sellerId = UUID.fromString(user.getName());

        var executor = new RegisterProductCommandExecutor(repository::save);
        executor.execute(id, sellerId, command);

        URI location = URI.create("/seller/products/" + id);
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/seller/products/{id}")
    ResponseEntity<?> findProduct(@PathVariable UUID id, Principal user) {
        var processor = new FindSellerProductQueryProcessor(repository::findById);
        var query = new FindSellerProduct(UUID.fromString(user.getName()), id);
        return ResponseEntity.ok(processor.process(query));
    }

    @GetMapping("/seller/products")
    ResponseEntity<?> getProducts(Principal user) {
        var processor = new GetSellerProductQueryProcessor(repository::findBySellerId);
        var query = new GetSellerProducts(UUID.fromString(user.getName()));
        return ResponseEntity.ok(processor.process(query));
    }
}
