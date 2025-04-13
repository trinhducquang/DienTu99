package org.example.quanlybanhang.controller.sale;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.collections.*;
import javafx.util.*;
import org.example.quanlybanhang.dao.*;
import org.example.quanlybanhang.model.*;
import org.example.quanlybanhang.utils.*;

import java.sql.*;

public class SaleController {

    @FXML
    private StackPane cartPane;

    @FXML
    private VBox cartBox;

    @FXML
    private GridPane gridPane;

    private Timeline slideInTimeline;
    private Timeline slideOutTimeline;

    private ObservableList<Product> productList;

    @FXML
    public void initialize() {
        cartBox.setPrefWidth(300);
        cartBox.setTranslateX(300);

        slideInTimeline = new Timeline(
                new KeyFrame(Duration.millis(150),
                        new KeyValue(cartBox.translateXProperty(), 0, Interpolator.EASE_BOTH))
        );
        slideOutTimeline = new Timeline(
                new KeyFrame(Duration.millis(150),
                        new KeyValue(cartBox.translateXProperty(), 300, Interpolator.EASE_BOTH))
        );
        slideOutTimeline.setOnFinished(event -> cartPane.setVisible(false));

        // Sử dụng DatabaseConnection để lấy kết nối
        Connection connection = DatabaseConnection.getConnection();

        if (connection != null) {
            // Dùng DAO để lấy danh sách sản phẩm từ DB
            ProductDAO productDAO = new ProductDAO(connection);
            productList = FXCollections.observableArrayList(productDAO.getAllProducts());

            // Đổ lên giao diện
            displayProducts();
        } else {
            System.out.println("Không thể kết nối đến cơ sở dữ liệu.");
        }
    }

    private void displayProducts() {
        int row = 0;
        int col = 0;

        // Duyệt qua danh sách sản phẩm và hiển thị trong GridPane
        for (Product product : productList) {
            VBox productCard = createProductCard(product);
            gridPane.add(productCard, col, row);

            // Chuyển sang cột tiếp theo, nếu đã hết cột thì xuống hàng mới
            col++;
            if (col >= 4) {
                col = 0;
                row++;
            }
        }
    }

    private VBox createProductCard(Product product) {
        VBox card = new VBox();
        card.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-background-color: white;");

        Image productImage;

        // Kiểm tra nếu URL là hợp lệ, nếu không sẽ sử dụng file cục bộ
        if (product.getImageUrl().startsWith("http") || product.getImageUrl().startsWith("https")) {
            // Nếu URL hợp lệ, tải ảnh trực tiếp với kích thước cố định
            productImage = new Image(product.getImageUrl(), 230, 150, true, true);
        } else {
            // Nếu không phải URL, có thể là đường dẫn file cục bộ
            productImage = new Image("file:" + product.getImageUrl(), 230, 150, true, true);
        }

        // Kiểm tra xem ảnh có thể tải được không
        if (productImage.isError()) {
            System.out.println("Không thể tải ảnh từ URL: " + product.getImageUrl());
        }

        // Đặt ảnh vào ImageView với kích thước cố định
        ImageView imageView = new ImageView(productImage);
        imageView.setFitHeight(150);
        imageView.setFitWidth(230);
        imageView.setPreserveRatio(true);

        Label productName = new Label(product.getName());
        productName.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        Label productPrice = new Label(product.getPrice().toString() + "₫");
        productPrice.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");

        Button addToCartButton = new Button("Thêm vào giỏ");
        addToCartButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        addToCartButton.setOnAction(event -> {
            // Thêm sản phẩm vào giỏ hàng khi nhấn nút
            System.out.println("Đã thêm " + product.getName() + " vào giỏ hàng");
        });

        card.getChildren().addAll(imageView, productName, productPrice, addToCartButton);
        return card;
    }



    // Toggle hành động giỏ hàng
    @FXML
    private void toggleCart() {
        if (!cartPane.isVisible()) {
            cartPane.setVisible(true);
            slideInTimeline.play();
        } else {
            slideOutTimeline.play();
        }
    }

    // Đóng giỏ hàng
    @FXML
    private void closeCart() {
        slideOutTimeline.play();
    }
}
