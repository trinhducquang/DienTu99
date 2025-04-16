package org.example.quanlybanhang.controller.order;


import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.example.quanlybanhang.enums.OrderStatus;
import org.example.quanlybanhang.enums.UserRole;
import org.example.quanlybanhang.helpers.ButtonTableCell;
import org.example.quanlybanhang.helpers.DialogHelper;
import org.example.quanlybanhang.helpers.LogoutHandler;
import org.example.quanlybanhang.model.Order;
import org.example.quanlybanhang.model.User;
import org.example.quanlybanhang.security.auth.UserSession;
import org.example.quanlybanhang.service.OrderService;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.example.quanlybanhang.utils.TableCellFactoryUtils.currencyCellFactory;


public class OrderController {
    @FXML
    private Button logoutButton;
    @FXML
    private HBox headerBox;
    @FXML
    private Button addOrderButton;
    @FXML
    private TableView<Order> ordersTable;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<OrderStatus> statusFilterComboBox;
    @FXML
    private DatePicker fromDatePicker;
    @FXML
    private DatePicker toDatePicker;
    @FXML
    private TableColumn<Order, Integer> orderIdColumn;
    @FXML
    private TableColumn<Order, Integer> customerIdColumn;
    @FXML
    private TableColumn<Order, String> customerNameColumn;
    @FXML
    private TableColumn<Order, String> orderNameColumn;
    @FXML
    private TableColumn<Order, String> orderDateColumn;
    @FXML
    private TableColumn<Order, BigDecimal> shippingFeeColumn;
    @FXML
    private TableColumn<Order, BigDecimal> totalPriceColumn;
    @FXML
    private TableColumn<Order, String> statusColumn;
    @FXML
    private TableColumn<Order, String> noteColumn;
    @FXML
    private TableColumn<Order, Void> actionsColumn;
    @FXML
    private Pagination pagination;

    private final IntegerProperty currentPage = new SimpleIntegerProperty(0);
    private final int itemsPerPage = 18;

    private final ObservableList<Order> allOrders = FXCollections.observableArrayList();
    private final ObservableList<Order> orderList = FXCollections.observableArrayList();

    private OrderService orderService;

    public void initialize() {
        orderService = new OrderService();
        setupStatusFilter();
        setupTableColumns();
        setupPagination();
        setupDatePickers();
        configureHeaderVisibility();
        loadOrdersBasedOnUserRole();

        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        statusFilterComboBox.setOnAction(event -> applyFilters());
        fromDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        toDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        addOrderButton.setOnAction(event ->
                DialogHelper.showDialog(
                        "/org/example/quanlybanhang/views/order/AddOrderDialog.fxml",
                        "Thêm Đơn Hàng Mới",
                        (Stage) addOrderButton.getScene().getWindow()
                ));
    }

    private void configureHeaderVisibility() {
        User currentUser = UserSession.getCurrentUser();
        if (currentUser != null) {
            UserRole role = currentUser.getRole();

            if (role == UserRole.ADMIN || role == UserRole.BAN_HANG) {
                headerBox.setVisible(false);
                headerBox.setManaged(false);
                addOrderButton.setVisible(true); // Make sure the button is visible for admin and sales
                addOrderButton.setManaged(true);
            } else if (role == UserRole.THU_NGAN) {
                headerBox.setVisible(true);
                headerBox.setManaged(true);
                // Hide the button for cashiers
                addOrderButton.setVisible(false);
                addOrderButton.setManaged(false);
            } else {
                headerBox.setVisible(true);
                headerBox.setManaged(true);
                addOrderButton.setVisible(true);
                addOrderButton.setManaged(true);
            }
        } else {
            headerBox.setVisible(false);
            headerBox.setManaged(false);
            addOrderButton.setVisible(false);
            addOrderButton.setManaged(false);
        }
    }

    private void setupDatePickers() {
        fromDatePicker.setPromptText("Từ ngày");
        toDatePicker.setPromptText("Đến ngày");

        fromDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && toDatePicker.getValue() != null &&
                    toDatePicker.getValue().isBefore(newVal)) {
                toDatePicker.setValue(newVal);
            }
        });
    }

    private void setupStatusFilter() {
        ObservableList<OrderStatus> statusOptions = FXCollections.observableArrayList();
        statusOptions.add(null);
        statusOptions.addAll(OrderStatus.values());
        statusFilterComboBox.setItems(statusOptions);
        statusFilterComboBox.setPromptText("Tất cả");

        statusFilterComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(OrderStatus item, boolean empty) {
                super.updateItem(item, empty);
                setText((item == null || empty) ? "Tất cả" : item.getText());
            }
        });

        statusFilterComboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(OrderStatus item, boolean empty) {
                super.updateItem(item, empty);
                setText((item == null || empty) ? "Tất cả" : item.getText());
            }
        });
    }

    private void setupTableColumns() {
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        orderNameColumn.setCellValueFactory(new PropertyValueFactory<>("productNames"));
        noteColumn.setCellValueFactory(new PropertyValueFactory<>("note"));
        orderDateColumn.setCellValueFactory(cellData ->
                javafx.beans.binding.Bindings.createStringBinding(() ->
                        cellData.getValue().getFormattedOrderDate()
                ));

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
        // Sửa lại cách thiết lập pagination
        pagination.setPageCount((int) Math.ceil((double) orderList.size() / itemsPerPage));

        pagination.currentPageIndexProperty().addListener((obs, oldVal, newVal) -> {
            currentPage.set(newVal.intValue());
            updateTableView();
        });

        // Đảm bảo cập nhật ngay lần đầu tiên
        updateTableView();
    }

    private void updateTableView() {
        int startIndex = currentPage.get() * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, orderList.size());
        List<Order> currentPageData = orderList.subList(startIndex, endIndex);
        ordersTable.setItems(FXCollections.observableArrayList(currentPageData));
        ordersTable.refresh();
    }

    private void loadOrdersBasedOnUserRole() {
        User currentUser = UserSession.getCurrentUser();
        List<Order> orders;
        if (currentUser.getRole() == UserRole.ADMIN || currentUser.getRole() == UserRole.THU_NGAN) {
            orders = orderService.getAllOrders();
        } else {
            orders = orderService.getOrdersByEmployeeId(currentUser.getId());
        }

        allOrders.setAll(orders);
        applyFilters();
    }

    private void applyFilters() {
        String keyword = searchField.getText().toLowerCase().trim();
        OrderStatus selectedStatus = statusFilterComboBox.getValue();
        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();

        List<Order> filteredOrders = new ArrayList<>(allOrders);

        // Lọc theo từ khóa
        if (!keyword.isEmpty()) {
            filteredOrders = filteredOrders.stream()
                    .filter(order -> matchesSearchKeyword(order, keyword))
                    .collect(Collectors.toList());
        }

        // Lọc theo trạng thái
        if (selectedStatus != null) {
            filteredOrders = filteredOrders.stream()
                    .filter(order -> order.getStatus() == selectedStatus)
                    .collect(Collectors.toList());
        }

        // Lọc theo ngày
        if (fromDate != null || toDate != null) {
            filteredOrders = filteredOrders.stream()
                    .filter(order -> {
                        LocalDate orderDate = order.getOrderDateAsLocalDate();
                        if (orderDate == null) return false;

                        boolean afterFromDate = fromDate == null || !orderDate.isBefore(fromDate);
                        boolean beforeToDate = toDate == null || !orderDate.isAfter(toDate);

                        return afterFromDate && beforeToDate;
                    })
                    .collect(Collectors.toList());
        }
        orderList.setAll(filteredOrders);
        pagination.setPageCount((int) Math.ceil((double) orderList.size() / itemsPerPage));
        pagination.setCurrentPageIndex(0);
        currentPage.set(0);
        updateTableView();
    }

    private boolean matchesSearchKeyword(Order order, String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return true;
        }

        return String.valueOf(order.getId()).contains(keyword) ||
                String.valueOf(order.getCustomerId()).contains(keyword) ||
                (order.getCustomerName() != null && order.getCustomerName().toLowerCase().contains(keyword)) ||
                (order.getProductNames() != null && order.getProductNames().toLowerCase().contains(keyword)) ||
                (order.getNote() != null && order.getNote().toLowerCase().contains(keyword));
    }

    @FXML
    private void handleLogout() {
        LogoutHandler.handleLogout(logoutButton);
    }
}