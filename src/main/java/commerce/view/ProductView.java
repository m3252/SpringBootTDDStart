package commerce.view;

import java.util.UUID;

public record ProductView(
    UUID id,
    SellerView seller,
    String name,
    String imageUri,
    String description,
    String priceAmount,
    int stockQuantity
) {
}
