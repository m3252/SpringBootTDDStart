package commerce.command;

import commerce.Product;
import commerce.commandmodel.InvalidCommandException;
import commerce.commandmodel.RegisterProductCommandExecutor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static java.time.ZoneOffset.UTC;

public record RegisterProductCommand(
    String name,
    String imageUri,
    String description,
    BigDecimal priceAmount,
    int stockQuantity
) {
}
