package org.example.quanlybanhang.controller.sale.manager;

import javafx.collections.ObservableList;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.example.quanlybanhang.factory.ProductCardFactory;
import org.example.quanlybanhang.model.Product;

public class ProductDisplayManager {

    private final GridPane gridPane;
    private final ObservableList<Product> productList;
    private final CartManager cartManager;
    private final ProductCardFactory productCardFactory;

    public ProductDisplayManager(GridPane gridPane,
                                 ObservableList<Product> productList,
                                 CartManager cartManager) {
        this.gridPane = gridPane;
        this.productList = productList;
        this.cartManager = cartManager;
        this.productCardFactory = new ProductCardFactory(cartManager);
    }

    public void displayProducts() {
        gridPane.getChildren().clear();
        int col = 0, row = 0, columns = 6;

        for (Product product : productList) {
            VBox card = productCardFactory.createProductCard(product);
            card.setPrefWidth(250);
            gridPane.add(card, col++, row);
            if (col >= columns) {
                col = 0;
                row++;
            }
        }
    }
}