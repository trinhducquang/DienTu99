package org.example.quanlybanhang.controller.order;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.quanlybanhang.dao.OrderDAO;
import org.example.quanlybanhang.helpers.ButtonTableCell;
import org.example.quanlybanhang.helpers.DialogHelper;
import org.example.quanlybanhang.model.Order;
import org.example.quanlybanhang.utils.MoneyUtils; // ✅ Import MoneyUtils

import java.util.List;

public class OrderController {
    @FXML
    public Button addOrderButton;
    @FXML
    public TableColumn<Order, String> noteColumn;
    @FXML
    private TableView<Order> ordersTable;
    @FXML
    private TableColumn<Order, Integer> orderIdColumn;
    @FXML
    private TableColumn<Order, Integer> customerIdColumn;
    @FXML
    private TableColumn<Order, String> orderNameColumn;
    @FXML
    private TableColumn<Order, Double> shippingFeeColumn;
    @FXML
    private TableColumn<Order, String> orderDateColumn;
    @FXML
    private TableColumn<Order, Double> totalPriceColumn;
    @FXML
    private TableColumn<Order, String> customerNameColumn;
    @FXML
    private TableColumn<Order, String> statusColumn;
    @FXML
    private TableColumn<Order, Void> actionsColumn;

    private OrderDAO orderDAO;
    private ObservableList<Order> orderList;

    public void initialize() {
        orderDAO = new OrderDAO();

        // Cấu hình cột TableView
        setupTableColumns();

        // Nạp dữ liệu vào bảng
        loadOrders();

        addOrderButton.setOnAction(event -> {
            DialogHelper.showDialog("/org/example/quanlybanhang/AddOrderDialog.fxml", "Thêm Đơn Hàng Mới");
        });
    }

    private void setupTableColumns() {
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        orderNameColumn.setCellValueFactory(new PropertyValueFactory<>("productNames"));
        noteColumn.setCellValueFactory(new PropertyValueFactory<>("note"));
        orderDateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate"));

        // ✅ Format tiền cho cột shippingFeeColumn
        shippingFeeColumn.setCellValueFactory(new PropertyValueFactory<>("shippingFee"));
        shippingFeeColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(MoneyUtils.formatVN(item));
                }
            }
        });

        // ✅ Format tiền cho cột totalPriceColumn
        totalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        totalPriceColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(MoneyUtils.formatVN(item));
                }
            }
        });

        // Trạng thái
        statusColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStatus().getText()));

        // Nút chi tiết đơn hàng
        actionsColumn.setCellFactory(param ->
                new ButtonTableCell<>("Chi tiết đơn hàng", order -> {
                    DialogHelper.showOrderDialog("/org/example/quanlybanhang/orderDetailsDialog.fxml", "Chi tiết đơn hàng", order.getId());
                })
        );
    }

    private void loadOrders() {
        List<Order> orders = orderDAO.getAllOrders();
        orderList = FXCollections.observableArrayList(orders);
        ordersTable.setItems(orderList);
    }
}
