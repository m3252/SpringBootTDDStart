package commerce.querymodel;

import commerce.Product;
import commerce.Seller;
import commerce.view.ProductView;
import commerce.view.SellerView;

record ProductSellerTuple(Product product, Seller seller) {

    static ProductView toView(ProductSellerTuple tuple) {
        return new ProductView(
            tuple.product().getId(),
            new SellerView(
                tuple.seller().getId(),
                tuple.seller().getUsername()
            ),
            tuple.product().getName(),
            tuple.product().getImageUri(),
            tuple.product().getDescription(),
            tuple.product().getPriceAmount(),
            tuple.product().getStockQuantity()
        );
    }
}
