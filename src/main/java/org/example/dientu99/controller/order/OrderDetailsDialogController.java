package org.example.dientu99.controller.order;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.dientu99.controller.interfaces.RefreshableView;
import org.example.dientu99.dto.orderDTO.OrderSummaryDTO;
import org.example.dientu99.dto.productDTO.ProductDisplayInfoDTO;
import org.example.dientu99.enums.OrderStatus;
import org.example.dientu99.factory.OrderUIFactory;
import org.example.dientu99.model.Order;
import org.example.dientu99.printing.OrderInvoiceGenerator;
import org.example.dientu99.service.OrderService;
import org.example.dientu99.utils.AlertUtils;
import org.example.dientu99.utils.MoneyUtils;

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

        orderId.setText(String.valueOf(summary.id()));
        orderDate.setText(summary.orderDate().toString());
        customerName.setText(summary.customerName());
        currentOrderStatus = summary.status().getText();
        orderStatus.setText(currentOrderStatus);

        OrderUIFactory.applyOrderStatusStyle(orderStatus, currentOrderStatus);

        statusComboBox.setValue(currentOrderStatus);

        BigDecimal total = summary.totalPrice();
        BigDecimal shipping = summary.shippingFee();
        BigDecimal finalTotal = total.add(shipping);

        totalAmount.setText(MoneyUtils.formatVN(total));
        shippingFee.setText(MoneyUtils.formatVN(shipping));
        finalAmount.setText(MoneyUtils.formatVN(finalTotal));

        processedBy.setText("ID: " + summary.employeeId());

        loadProductDisplayList();
    }

    private void loadProductDisplayList() {
        List<ProductDisplayInfoDTO> productList = orderService.getProductDisplayInfoList(currentOrderId);
        productListContainer.getChildren().clear();

        int totalItems = 0;
        BigDecimal totalOrderValue = BigDecimal.ZERO;

        for (ProductDisplayInfoDTO product : productList) {

            HBox productBox = OrderUIFactory.createOrderProductBox(product);

            totalItems += product.quantity().intValue();
            totalOrderValue = totalOrderValue.add(product.totalPrice());
            productListContainer.getChildren().add(productBox);
        }

        totalProducts.setText(String.valueOf(totalItems));
        subtotalAmount.setText(MoneyUtils.formatVN(totalOrderValue));
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
            GridPane parent = (GridPane) orderStatus.getParent();
            Integer columnIndex = GridPane.getColumnIndex(orderStatus);
            Integer rowIndex = GridPane.getRowIndex(orderStatus);
            if (columnIndex == null) columnIndex = 0;
            if (rowIndex == null) rowIndex = 0;
            parent.getChildren().remove(orderStatus);
            statusComboBox.setValue(currentOrderStatus);
            parent.add(statusComboBox, columnIndex, rowIndex);
            updateStatusBtn.setText("Lưu trạng thái");
            isEditingStatus = true;
        } else {
            saveNewStatus();
        }
    }

    private void saveNewStatus() {
        String newStatus = statusComboBox.getValue();
        if (newStatus != null && !newStatus.equals(currentOrderStatus)) {
            Order order = new Order();
            order.setId(currentOrderId);
            order.setStatus(OrderStatus.fromString(newStatus));
            OrderSummaryDTO summary = orderService.getOrderSummaryById(currentOrderId);
            if (summary != null) {
                order.setNote(summary.note());
            }

            boolean success = orderService.updateOrderStatus(order);

            if (success) {
                currentOrderStatus = newStatus;
                GridPane parent = (GridPane) statusComboBox.getParent();
                Integer columnIndex = GridPane.getColumnIndex(statusComboBox);
                Integer rowIndex = GridPane.getRowIndex(statusComboBox);

                if (columnIndex == null) columnIndex = 0;
                if (rowIndex == null) rowIndex = 0;

                parent.getChildren().remove(statusComboBox);

                orderStatus.setText(newStatus);


                OrderUIFactory.applyOrderStatusStyle(orderStatus, newStatus);

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