package commerce.querymodel;

import commerce.Product;
import commerce.view.SellerProductView;

public class ProductMapper {

    public static SellerProductView convertToView(Product product) {
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
