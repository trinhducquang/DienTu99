package org.example.quanlybanhang.controller.order;

import static org.example.quanlybanhang.utils.MoneyUtils.formatVN;
import static org.example.quanlybanhang.utils.TableCellFactoryUtils.currencyCellFactory;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.quanlybanhang.enums.OrderStatus;
import org.example.quanlybanhang.helpers.ButtonTableCell;
import org.example.quanlybanhang.helpers.DialogHelper;
import org.example.quanlybanhang.model.Order;
import org.example.quanlybanhang.service.OrderService;
import org.example.quanlybanhang.service.SearchService;
import org.example.quanlybanhang.utils.PaginationUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Predicate;

public class OrderController {

    @FXML private Button addOrderButton;
    @FXML private TableView<Order> ordersTable;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilterComboBox;
    @FXML private TableColumn<Order, Integer> orderIdColumn;
    @FXML private TableColumn<Order, Integer> customerIdColumn;
    @FXML private TableColumn<Order, String> customerNameColumn;
    @FXML private TableColumn<Order, String> orderNameColumn;
    @FXML private TableColumn<Order, String> orderDateColumn;
    @FXML private TableColumn<Order, BigDecimal> shippingFeeColumn;
    @FXML private TableColumn<Order, BigDecimal> totalPriceColumn;
    @FXML private TableColumn<Order, String> statusColumn;
    @FXML private TableColumn<Order, String> noteColumn;
    @FXML private TableColumn<Order, Void> actionsColumn;
    @FXML private Pagination pagination;

    private final IntegerProperty currentPage = new SimpleIntegerProperty(0);
    private final int itemsPerPage = 18;

    private final ObservableList<Order> allOrders = FXCollections.observableArrayList();
    private final ObservableList<Order> orderList = FXCollections.observableArrayList();

    private OrderService orderService;

    public void initialize() {
        orderService = new OrderService();
        setupStatusFilter();
        setupTableColumns();
        loadOrders();

        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        statusFilterComboBox.setOnAction(event -> applyFilters());

        addOrderButton.setOnAction(event ->
                DialogHelper.showDialog(
                        "/org/example/quanlybanhang/views/order/AddOrderDialog.fxml",
                        "Thêm Đơn Hàng Mới",
                        (Stage) addOrderButton.getScene().getWindow()
                ));
    }

    private void setupStatusFilter() {
        ObservableList<String> statusOptions = FXCollections.observableArrayList("Tất cả");
        for (OrderStatus status : OrderStatus.values()) {
            statusOptions.add(status.getText());
        }
        statusFilterComboBox.setItems(statusOptions);
        statusFilterComboBox.setValue("Tất cả");
    }

    private void loadOrders() {
        List<Order> orders = orderService.getAllOrders();
        allOrders.setAll(orders);
        applyFilters();
    }

    private void applyFilters() {
        String keyword = searchField.getText().toLowerCase().trim();
        String selectedStatus = statusFilterComboBox.getValue();

        Predicate<Order> statusFilter = order -> {
            if ("Tất cả".equals(selectedStatus)) return true;
            return order.getStatus().getText().equalsIgnoreCase(selectedStatus);
        };

        List<Order> filtered = SearchService.search(
                allOrders.filtered(statusFilter),
                keyword,
                order -> String.valueOf(order.getId()),
                order -> String.valueOf(order.getCustomerId()),
                Order::getCustomerName,
                Order::getProductNames,
                Order::getNote,
                order -> formatVN(order.getShippingFee()),
                order -> order.getOrderDate().toString(),
                order -> formatVN(order.getTotalPrice())
        );

        orderList.setAll(filtered);
        setupPagination();
    }

    private void setupTableColumns() {
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        orderNameColumn.setCellValueFactory(new PropertyValueFactory<>("productNames"));
        noteColumn.setCellValueFactory(new PropertyValueFactory<>("note"));
        orderDateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate"));

        shippingFeeColumn.setCellValueFactory(new PropertyValueFactory<>("shippingFee"));
        shippingFeeColumn.setCellFactory(currencyCellFactory());

        totalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        totalPriceColumn.setCellFactory(currencyCellFactory());

        statusColumn.setCellValueFactory(cellData ->
                javafx.beans.binding.Bindings.createStringBinding(() ->
                        cellData.getValue().getStatus().getText()
                ));

        actionsColumn.setCellFactory(param ->
                new ButtonTableCell<>("Chi tiết đơn hàng", order ->
                        DialogHelper.showOrderDialog(
                                "/org/example/quanlybanhang/views/order/orderDetailsDialog.fxml",
                                "Chi tiết đơn hàng",
                                order.getId(),
                                (Stage) ordersTable.getScene().getWindow()
                        )
                )
        );
    }

    private void setupPagination() {
        PaginationUtils.setup(
                pagination,
                allOrders,
                orderList,
                currentPage,
                itemsPerPage,
                (pagedData, pageIndex) -> ordersTable.setItems(FXCollections.observableArrayList(pagedData))

        );
    }
}
