package org.example.quanlybanhang.controller.order;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.quanlybanhang.dao.OrderDAO;
import org.example.quanlybanhang.enums.OrderStatus;
import org.example.quanlybanhang.helpers.ButtonTableCell;
import org.example.quanlybanhang.helpers.DialogHelper;
import org.example.quanlybanhang.model.Order;
import org.example.quanlybanhang.service.SearchService;
import org.example.quanlybanhang.utils.MoneyUtils;

import java.util.List;

public class OrderController {

    @FXML
    private Button addOrderButton;
    @FXML
    private TableView<Order> ordersTable;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> statusFilterComboBox;
    @FXML private TableColumn<Order, Integer> orderIdColumn;
    @FXML private TableColumn<Order, Integer> customerIdColumn;
    @FXML private TableColumn<Order, String> customerNameColumn;
    @FXML private TableColumn<Order, String> orderNameColumn;
    @FXML private TableColumn<Order, String> orderDateColumn;
    @FXML private TableColumn<Order, Double> shippingFeeColumn;
    @FXML private TableColumn<Order, Double> totalPriceColumn;
    @FXML private TableColumn<Order, String> statusColumn;
    @FXML private TableColumn<Order, String> noteColumn;
    @FXML private TableColumn<Order, Void> actionsColumn;

    private OrderDAO orderDAO;
    private ObservableList<Order> orderList;

    public void initialize() {
        orderDAO = new OrderDAO();

        ObservableList<String> statusOptions = FXCollections.observableArrayList("Tất cả");
        for (OrderStatus status : OrderStatus.values()) {
            statusOptions.add(status.getText());
        }
        statusFilterComboBox.setItems(statusOptions);
        statusFilterComboBox.setValue("Tất cả");


        setupTableColumns();
        loadOrders();
        searchField.textProperty().addListener((obs, oldVal, newVal) -> performSearch(newVal));
        statusFilterComboBox.setOnAction(event -> filterOrdersByStatus());

        addOrderButton.setOnAction(event -> {
            DialogHelper.showDialog("/org/example/quanlybanhang/AddOrderDialog.fxml", "Thêm Đơn Hàng Mới");
        });
    }

    private void performSearch(String keyword) {
        List<Order> filtered = SearchService.search(
                orderList,
                keyword,
                order -> String.valueOf(order.getId()),
                order -> String.valueOf(order.getCustomerId()),
                Order::getCustomerName,
                Order::getProductNames,
                Order::getNote,
                order -> MoneyUtils.formatVN(order.getShippingFee()),
                order -> order.getOrderDate().toString(),
                order -> MoneyUtils.formatVN(order.getTotalPrice())
        );
        ordersTable.setItems(FXCollections.observableArrayList(filtered));
    }


    private void filterOrdersByStatus() {
        String selected = statusFilterComboBox.getValue();
        if (selected == null || selected.equals("Tất cả")) {
            ordersTable.setItems(orderList);
        } else {
            ObservableList<Order> filteredList = orderList.filtered(order ->
                    order.getStatus().getText().equalsIgnoreCase(selected)
            );
            ordersTable.setItems(filteredList);
        }
    }


    private void setupTableColumns() {
        // 🆔 ID và thông tin khách hàng
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));

        // 📦 Tên sản phẩm, ghi chú, ngày đặt
        orderNameColumn.setCellValueFactory(new PropertyValueFactory<>("productNames"));
        noteColumn.setCellValueFactory(new PropertyValueFactory<>("note"));
        orderDateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate"));

        // 💰 Định dạng số tiền
        shippingFeeColumn.setCellValueFactory(new PropertyValueFactory<>("shippingFee"));
        shippingFeeColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : MoneyUtils.formatVN(item));
            }
        });

        totalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        totalPriceColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : MoneyUtils.formatVN(item));
            }
        });

        // 🔁 Trạng thái
        statusColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStatus().getText()));

        // 📎 Nút hành động
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
