package commerce.commandmodel;

import commerce.Product;
import commerce.command.RegisterProductCommand;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Consumer;

import static java.time.ZoneOffset.UTC;

public class RegisterProductCommandExecutor {

    private final Consumer<Product> saveProduct;

    public RegisterProductCommandExecutor(Consumer<Product> saveProduct) {
        this.saveProduct = saveProduct;
    }

    public void execute(UUID productId, UUID sellerId, RegisterProductCommand command) {
        validateCommand(command);

        var product = createProduct(productId, sellerId, command);

        saveProduct(product);
    }

    private static void validateCommand(RegisterProductCommand command) {
        if (!isValidUri(command.imageUri())) {
            throw new InvalidCommandException();
        }
    }

    private static Product createProduct(UUID productId, UUID sellerId, RegisterProductCommand command) {
        var product = new Product();
        product.setId(productId);
        product.setSellerId(sellerId);
        product.setName(command.name());
        product.setImageUri(command.imageUri());
        product.setDescription(command.description());
        product.setPriceAmount(command.priceAmount());
        product.setStockQuantity(command.stockQuantity());
        product.setRegisteredTimeUtc(LocalDateTime.now(UTC));
        return product;
    }

    private void saveProduct(Product product) {
        saveProduct.accept(product);
    }

    private static boolean isValidUri(String value) {
        try {
            URI uri = URI.create(value);
            return uri.getHost() != null;
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }
}
