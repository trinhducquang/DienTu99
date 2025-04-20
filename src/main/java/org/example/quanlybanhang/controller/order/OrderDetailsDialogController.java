package org.example.quanlybanhang.controller.order;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.quanlybanhang.controller.interfaces.RefreshableView;
import org.example.quanlybanhang.dto.orderDTO.OrderSummaryDTO;
import org.example.quanlybanhang.dto.productDTO.ProductDisplayInfoDTO;
import org.example.quanlybanhang.enums.OrderStatus;
import org.example.quanlybanhang.model.Order;
import org.example.quanlybanhang.printing.OrderInvoiceGenerator;
import org.example.quanlybanhang.service.OrderService;
import org.example.quanlybanhang.utils.AlertUtils;
import org.example.quanlybanhang.utils.ImagesUtils;
import org.example.quanlybanhang.utils.MoneyUtils;

import java.math.BigDecimal;
import java.util.List;

public class OrderDetailsDialogController implements RefreshableView {

    @FXML
    private Label orderId, orderDate, customerName, totalAmount, processedBy;
    @FXML
    private Label totalProducts, subtotalAmount, shippingFee, finalAmount;
    @FXML
    private Label orderStatus;
    @FXML
    private VBox productListContainer;
    @FXML
    private Button printOrderBtn, updateStatusBtn, backBtn;

    private ComboBox<String> statusComboBox;
    private int currentOrderId;
    private String currentOrderStatus;
    private final OrderService orderService = new OrderService();
    private boolean isEditingStatus = false;

    public void initialize() {
        // Khởi tạo ComboBox trạng thái đơn hàng
        statusComboBox = new ComboBox<>();
        for (OrderStatus status : OrderStatus.values()) {
            statusComboBox.getItems().add(status.getText());
        }
    }

    public void setOrderById(Integer orderId) {
        this.currentOrderId = orderId;
        loadOrderDetails();
    }

    @Override
    public void refresh() {
        loadOrderDetails();
    }

    private void loadOrderDetails() {
        OrderSummaryDTO summary = orderService.getOrderSummaryById(currentOrderId);
        if (summary == null) return;

        // Thiết lập thông tin đơn hàng
        orderId.setText(String.valueOf(summary.id()));
        orderDate.setText(summary.orderDate().toString());
        customerName.setText(summary.customerName());
        currentOrderStatus = summary.status().getText();
        orderStatus.setText(currentOrderStatus);

        // Thiết lập giá trị mặc định cho ComboBox
        statusComboBox.setValue(currentOrderStatus);

        // Đặt thông tin giá tiền
        BigDecimal total = summary.totalPrice();
        BigDecimal shipping = summary.shippingFee();
        BigDecimal finalTotal = total.add(shipping);

        totalAmount.setText(MoneyUtils.formatVN(total));
        shippingFee.setText(MoneyUtils.formatVN(shipping));
        finalAmount.setText(MoneyUtils.formatVN(finalTotal));

        // Đặt thông tin người xử lý
        processedBy.setText("ID: " + summary.employeeId());

        // Load danh sách sản phẩm
        loadProductDisplayList();
    }

    private void loadProductDisplayList() {
        List<ProductDisplayInfoDTO> productList = orderService.getProductDisplayInfoList(currentOrderId);
        productListContainer.getChildren().clear();

        int totalItems = 0;
        BigDecimal totalOrderValue = BigDecimal.ZERO;

        for (ProductDisplayInfoDTO product : productList) {
            HBox productBox = createProductBox(
                    product.id(),
                    product.name(),
                    product.imageUrl(),
                    product.quantity(),
                    product.unitPrice(),
                    product.totalPrice()
            );

            totalItems += product.quantity().intValue();
            totalOrderValue = totalOrderValue.add(product.totalPrice());
            productListContainer.getChildren().add(productBox);
        }

        totalProducts.setText(String.valueOf(totalItems));
        subtotalAmount.setText(MoneyUtils.formatVN(totalOrderValue));
    }

    private HBox createProductBox(int productId, String name, String imageUrl, BigDecimal quantity, BigDecimal price, BigDecimal total) {
        HBox productBox = new HBox();
        productBox.setSpacing(15);
        productBox.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-color: #dddddd; -fx-border-radius: 5;");

        // Thêm hình ảnh (nếu có)
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            ImageView imageView = ImagesUtils.createImageView(imageUrl, 80, 80);
            productBox.getChildren().add(imageView);
        }

        // Thông tin chi tiết sản phẩm
        VBox detailsBox = new VBox();
        detailsBox.setSpacing(5);

        Label lblName = new Label(name);
        lblName.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label lblProductId = new Label("Mã sản phẩm: " + productId);
        lblProductId.setStyle("-fx-text-fill: #7f8c8d;");

        Label lblQuantity = new Label("Số lượng: " + quantity);
        Label lblTotal = new Label("Thành tiền: " + MoneyUtils.formatVN(total));
        lblTotal.setStyle("-fx-font-weight: bold;");

        detailsBox.getChildren().addAll(lblName, lblProductId, lblQuantity, lblTotal);
        productBox.getChildren().add(detailsBox);

        // Hiển thị giá đơn vị
        HBox priceBox = new HBox();
        priceBox.setSpacing(10);
        Label lblPrice = new Label(MoneyUtils.formatVN(price));
        lblPrice.setStyle("-fx-font-weight: bold; -fx-text-fill: #e74c3c;");
        priceBox.getChildren().add(lblPrice);
        productBox.getChildren().add(priceBox);

        return productBox;
    }

    @FXML
    private void handlePrintOrder() {
        OrderSummaryDTO summary = orderService.getOrderSummaryById(currentOrderId);
        List<ProductDisplayInfoDTO> productList = orderService.getProductDisplayInfoList(currentOrderId);
        OrderInvoiceGenerator.exportToPdf(summary, productList);
    }

    @FXML
    private void handleUpdateStatus() {
        if (!isEditingStatus) {
            // Chuyển sang chế độ chỉnh sửa
            GridPane parent = (GridPane) orderStatus.getParent();
            // Xác định vị trí của orderStatus trong GridPane
            Integer columnIndex = GridPane.getColumnIndex(orderStatus);
            Integer rowIndex = GridPane.getRowIndex(orderStatus);

            if (columnIndex == null) columnIndex = 0;
            if (rowIndex == null) rowIndex = 0;

            // Xóa Label orderStatus khỏi parent
            parent.getChildren().remove(orderStatus);

            // Thêm ComboBox vào vị trí của Label
            statusComboBox.setValue(currentOrderStatus);
            parent.add(statusComboBox, columnIndex, rowIndex);

            // Đổi text của nút
            updateStatusBtn.setText("Lưu trạng thái");
            isEditingStatus = true;
        } else {
            // Đã nhấn "Lưu trạng thái"
            saveNewStatus();
        }
    }

    private void saveNewStatus() {
        String newStatus = statusComboBox.getValue();
        if (newStatus != null && !newStatus.equals(currentOrderStatus)) {
            // Cập nhật vào database
            Order order = new Order();
            order.setId(currentOrderId);
            order.setStatus(OrderStatus.fromString(newStatus));

            // Giữ lại note hiện tại
            OrderSummaryDTO summary = orderService.getOrderSummaryById(currentOrderId);
            if (summary != null) {
                order.setNote(summary.note());
            }

            boolean success = orderService.updateOrderStatus(order);

            if (success) {
                // Cập nhật thành công
                currentOrderStatus = newStatus;

                // Chuyển từ ComboBox về Label
                GridPane parent = (GridPane) statusComboBox.getParent();
                Integer columnIndex = GridPane.getColumnIndex(statusComboBox);
                Integer rowIndex = GridPane.getRowIndex(statusComboBox);

                if (columnIndex == null) columnIndex = 0;
                if (rowIndex == null) rowIndex = 0;

                parent.getChildren().remove(statusComboBox);

                // Cập nhật text cho Label
                orderStatus.setText(newStatus);
                parent.add(orderStatus, columnIndex, rowIndex);

                updateStatusBtn.setText("Cập nhật trạng thái");
                isEditingStatus = false;

                AlertUtils.showInfo("Thành công", "Đã cập nhật trạng thái đơn hàng thành công!");
            } else {
                AlertUtils.showError("Lỗi", "Không thể cập nhật trạng thái đơn hàng. Vui lòng thử lại!");
            }
        } else if (isEditingStatus) {

            GridPane parent = (GridPane) statusComboBox.getParent();
            Integer columnIndex = GridPane.getColumnIndex(statusComboBox);
            Integer rowIndex = GridPane.getRowIndex(statusComboBox);

            if (columnIndex == null) columnIndex = 0;
            if (rowIndex == null) rowIndex = 0;

            parent.getChildren().remove(statusComboBox);
            parent.add(orderStatus, columnIndex, rowIndex);
            updateStatusBtn.setText("Cập nhật trạng thái");
            isEditingStatus = false;
        }
    }

    @FXML
    private void handleBack() {
        Stage stage = (Stage) backBtn.getScene().getWindow();
        stage.close();
    }
}