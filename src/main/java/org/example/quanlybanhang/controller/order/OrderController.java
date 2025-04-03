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

import java.util.List;

public class OrderController {
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
    }

    private void setupTableColumns() {
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        orderNameColumn.setCellValueFactory(new PropertyValueFactory<>("productNames"));
        shippingFeeColumn.setCellValueFactory(new PropertyValueFactory<>("shippingFee"));
        orderDateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        totalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

        statusColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStatus().getText()));

        // Gán action mở dialog chi tiết đơn hàng
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
