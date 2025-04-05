package org.example.quanlybanhang.controller.order;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.quanlybanhang.dao.OrderDAO;
import org.example.quanlybanhang.utils.DatabaseConnection;
import org.example.quanlybanhang.utils.MoneyUtils;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderDetailsDialogController {

    @FXML private Label orderId, orderDate, customerName, orderStatus, totalAmount, processedBy;
    @FXML private Label totalProducts, subtotalAmount, shippingFee, finalAmount;
    @FXML private VBox productListContainer;
    @FXML private Button printOrderBtn, updateStatusBtn, backBtn;

    private Connection connection;
    private int currentOrderId;
    private String currentOrderStatus;
    private OrderDAO orderDAO;

    public void initialize() {
        connection = DatabaseConnection.getConnection();
        orderDAO = new OrderDAO();
    }

    public void setOrderById(Integer orderId) {
        this.currentOrderId = orderId;
        loadOrderDetails();
    }

    private void loadOrderDetails() {
        String sql = "SELECT o.id, o.order_date, c.name AS customer_name, o.status, " +
                "o.total_price, o.shipping_fee, o.employee_id " +
                "FROM orders o " +
                "JOIN customers c ON o.customer_id = c.id " +
                "WHERE o.id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, currentOrderId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                orderId.setText(String.valueOf(resultSet.getInt("id")));
                orderDate.setText(resultSet.getString("order_date"));
                customerName.setText(resultSet.getString("customer_name"));
                currentOrderStatus = resultSet.getString("status");
                orderStatus.setText(currentOrderStatus);

                double totalPrice = resultSet.getDouble("total_price");
                double fee = resultSet.getDouble("shipping_fee");

                totalAmount.setText(MoneyUtils.formatVN(totalPrice));
                shippingFee.setText(MoneyUtils.formatVN(fee));
                finalAmount.setText(MoneyUtils.formatVN(totalPrice + fee));
                processedBy.setText("ID: " + resultSet.getInt("employee_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        loadOrderProducts();
    }

    private void loadOrderProducts() {
        String sql = "SELECT p.id, p.name, p.image_url, od.quantity, p.price " +
                "FROM order_details od " +
                "JOIN products p ON od.product_id = p.id " +
                "WHERE od.order_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, currentOrderId);
            ResultSet resultSet = statement.executeQuery();

            productListContainer.getChildren().clear();
            int totalItems = 0;
            double totalOrderValue = 0;

            while (resultSet.next()) {
                int productId = resultSet.getInt("id");
                String productName = resultSet.getString("name");
                String imageUrl = resultSet.getString("image_url");
                int quantity = resultSet.getInt("quantity");
                double price = resultSet.getDouble("price");
                double total = quantity * price;

                totalItems += quantity;
                totalOrderValue += total;

                HBox productBox = createProductBox(productId, productName, imageUrl, quantity, price, total);
                productListContainer.getChildren().add(productBox);
            }

            totalProducts.setText(String.valueOf(totalItems));
            subtotalAmount.setText(MoneyUtils.formatVN(totalOrderValue));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private HBox createProductBox(int productId, String name, String imageUrl, int quantity, double price, double total) {
        HBox productBox = new HBox();
        productBox.setSpacing(15);
        productBox.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-color: #dddddd; -fx-border-radius: 5;");

        VBox detailsBox = new VBox();
        detailsBox.setSpacing(5);

        Label lblName = new Label(name);
        lblName.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label lblProductId = new Label("Mã sản phẩm: " + productId);
        lblProductId.setStyle("-fx-text-fill: #7f8c8d;");

        Label lblPrice = new Label(MoneyUtils.formatVN(price));
        lblPrice.setStyle("-fx-font-weight: bold; -fx-text-fill: #e74c3c;");

        Label lblQuantity = new Label("Số lượng: " + quantity);
        Label lblTotal = new Label("Thành tiền: " + MoneyUtils.formatVN(total));
        lblTotal.setStyle("-fx-font-weight: bold;");

        detailsBox.getChildren().addAll(lblName, lblProductId, lblQuantity, lblTotal);

        HBox priceBox = new HBox(lblPrice);
        priceBox.setSpacing(10);

        if (currentOrderStatus.equals("Đang xử lý")) {
            Button decreaseButton = new Button("❌");
            decreaseButton.setOnAction(e -> {
                orderDAO.decreaseProductQuantity(currentOrderId, productId);
                loadOrderDetails(); // ✅ cập nhật toàn bộ đơn hàng
            });
            priceBox.getChildren().add(decreaseButton);
        }

        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            try {
                ImageView imageView = new ImageView();
                imageView.setFitHeight(80);
                imageView.setFitWidth(80);
                imageView.setPreserveRatio(true);

                if (imageUrl.startsWith("http") || imageUrl.startsWith("file:/")) {
                    imageView.setImage(new Image(imageUrl));
                } else {
                    imageView.setImage(new Image(new File(imageUrl).toURI().toString()));
                }

                productBox.getChildren().add(imageView);
            } catch (Exception e) {
                System.err.println("Không thể tải hình ảnh: " + imageUrl);
            }
        }

        productBox.getChildren().addAll(detailsBox, priceBox);
        return productBox;
    }

    @FXML
    private void handlePrintOrder() {
        System.out.println("In đơn hàng: " + currentOrderId);
    }

    @FXML
    private void handleUpdateStatus() {
        System.out.println("Cập nhật trạng thái đơn hàng: " + currentOrderId);
    }

    @FXML
    private void handleBack() {
        Stage stage = (Stage) backBtn.getScene().getWindow();
        stage.close();
    }
}
