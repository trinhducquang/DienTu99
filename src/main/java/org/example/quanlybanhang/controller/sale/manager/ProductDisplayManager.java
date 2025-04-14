package org.example.quanlybanhang.controller.sale.manager;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.example.quanlybanhang.dto.productDTO.ProductDisplayInfoDTO;
import org.example.quanlybanhang.controller.factory.ProductCardFactory;
import org.example.quanlybanhang.model.Product;
import org.example.quanlybanhang.utils.MoneyUtils;
import org.example.quanlybanhang.utils.PaginationUtils;

import java.math.BigDecimal;
import java.util.List;

public class ProductDisplayManager {
    private final GridPane gridPane;
    private final Pagination pagination;
    private final ObservableList<Product> allProducts = FXCollections.observableArrayList();
    private final ObservableList<Product> filteredProducts = FXCollections.observableArrayList();
    private final ObservableList<Product> productList = FXCollections.observableArrayList();
    private final IntegerProperty currentPage = new SimpleIntegerProperty(0);
    private final CartManager cartManager;
    private static final int ITEMS_PER_PAGE = 12;
    private static final int COLUMNS = 6;

    public ProductDisplayManager(GridPane gridPane, Pagination pagination, CartManager cartManager) {
        this.gridPane = gridPane;
        this.pagination = pagination;
        this.cartManager = cartManager;
        setupPagination();
    }

    public void loadProducts(List<Product> products) {
        allProducts.setAll(products);
        filteredProducts.setAll(allProducts);
        resetPagination();
    }

    public void filterByPrice(BigDecimal min, BigDecimal max) {
        filteredProducts.setAll(
                allProducts.filtered(product -> {
                    BigDecimal price = product.getPrice();
                    return price.compareTo(min) >= 0 && price.compareTo(max) <= 0;
                })
        );
        resetPagination();
    }

    private void setupPagination() {
        PaginationUtils.setup(
                pagination,
                filteredProducts,
                productList,
                currentPage,
                ITEMS_PER_PAGE,
                (pagedData, pageIndex) -> displayProducts()
        );
    }

    private void displayProducts() {
        gridPane.getChildren().clear();
        int col = 0, row = 0;

        for (Product product : productList) {
            VBox card = createProductCard(product);
            card.setPrefWidth(250);
            gridPane.add(card, col++, row);
            if (col >= COLUMNS) {
                col = 0;
                row++;
            }
        }
    }

    private VBox createProductCard(Product product) {
        return ProductCardFactory.createCard(product, dto -> cartManager.addToCart(dto));
    }

    private void resetPagination() {
        currentPage.set(0);
        pagination.setCurrentPageIndex(0);
        pagination.setPageCount(Math.max(1, (int) Math.ceil((double) filteredProducts.size() / ITEMS_PER_PAGE)));
        updatePagedItems();
    }

    private void updatePagedItems() {
        int startIndex = currentPage.get() * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, filteredProducts.size());
        productList.setAll(filteredProducts.subList(startIndex, endIndex));
        displayProducts();
    }
}