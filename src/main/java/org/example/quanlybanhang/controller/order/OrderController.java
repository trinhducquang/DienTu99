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
import org.example.quanlybanhang.controller.interfaces.RefreshableView;
import org.example.quanlybanhang.enums.ExportStatus;
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
import java.util.List;
import java.util.stream.Collectors;

import static org.example.quanlybanhang.utils.TableCellFactoryUtils.currencyCellFactory;

public class OrderController implements RefreshableView {

    @FXML private Button logoutButton;
    @FXML private HBox headerBox;
    @FXML private Button addOrderButton;
    @FXML private TableView<Order> ordersTable;
    @FXML private TextField searchField;
    @FXML private ComboBox<OrderStatus> statusFilterComboBox;
    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;
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
    @FXML private TableColumn <Order, ExportStatus> exportStatusColum;
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
        setupDatePickers();
        configureHeaderVisibility();

        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        statusFilterComboBox.setOnAction(e -> applyFilters());

        // Xử lý riêng cho DatePicker
        fromDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            // Nếu người dùng xóa ngày
            if (oldVal != null && newVal == null) {
                applyFilters();
            } else {
                applyFilters();
            }
        });

        toDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            // Nếu người dùng xóa ngày
            if (oldVal != null && newVal == null) {
                applyFilters();
            } else {
                applyFilters();
            }
        });

        pagination.currentPageIndexProperty().addListener((obs, oldVal, newVal) -> {
            currentPage.set(newVal.intValue());
            updateTableView();
        });

        addOrderButton.setOnAction(e ->
                DialogHelper.showDialog(
                        "/org/example/quanlybanhang/views/order/AddOrderDialog.fxml",
                        "Thêm Đơn Hàng Mới",
                        (Stage) addOrderButton.getScene().getWindow(),
                        this
                )
        );

        loadOrdersBasedOnUserRole();
    }

    @Override
    public void refresh() {
        System.out.println("OrderController: Đang refresh danh sách đơn hàng");
        loadOrdersBasedOnUserRole();
    }

    private void configureHeaderVisibility() {
        User user = UserSession.getCurrentUser();
        if (user != null) {
            UserRole role = user.getRole();
            boolean isCashier = role == UserRole.THU_NGAN;
            boolean canAddOrder = role == UserRole.ADMIN || role == UserRole.BAN_HANG;

            headerBox.setVisible(isCashier);
            headerBox.setManaged(isCashier);

            addOrderButton.setVisible(canAddOrder);
            addOrderButton.setManaged(canAddOrder);
        } else {
            headerBox.setVisible(false);
            addOrderButton.setVisible(false);
        }
    }

    private void setupDatePickers() {
        fromDatePicker.setPromptText("Từ ngày");
        toDatePicker.setPromptText("Đến ngày");
        fromDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            applyFilters();
        });
        toDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            applyFilters();
        });
        fromDatePicker.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isEmpty()) {
                fromDatePicker.setValue(null);
                applyFilters();
            }
        });
        toDatePicker.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isEmpty()) {
                toDatePicker.setValue(null);
                applyFilters();
            }
        });
        fromDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && toDatePicker.getValue() != null &&
                    toDatePicker.getValue().isBefore(newVal)) {
                toDatePicker.setValue(newVal);
            }
        });
    }

    private void setupStatusFilter() {
        ObservableList<OrderStatus> options = FXCollections.observableArrayList();
        options.add(null); // Tất cả
        options.addAll(OrderStatus.values());
        statusFilterComboBox.setItems(options);
        statusFilterComboBox.setPromptText("Tất cả");

        statusFilterComboBox.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(OrderStatus item, boolean empty) {
                super.updateItem(item, empty);
                setText((item == null || empty) ? "Tất cả" : item.getText());
            }
        });

        statusFilterComboBox.setCellFactory(cb -> new ListCell<>() {
            @Override protected void updateItem(OrderStatus item, boolean empty) {
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
        exportStatusColum.setCellValueFactory(new PropertyValueFactory<>("exportStatus"));

        orderDateColumn.setCellValueFactory(cell ->
                javafx.beans.binding.Bindings.createStringBinding(() ->
                        cell.getValue().getFormattedOrderDate()
                )
        );

        shippingFeeColumn.setCellValueFactory(new PropertyValueFactory<>("shippingFee"));
        shippingFeeColumn.setCellFactory(currencyCellFactory());

        totalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        totalPriceColumn.setCellFactory(currencyCellFactory());

        statusColumn.setCellValueFactory(cell ->
                javafx.beans.binding.Bindings.createStringBinding(() ->
                        cell.getValue().getStatus().getText()
                )
        );

        setupActionColumn(); // Gọi phương thức riêng biệt để thiết lập cột hành động
    }

    // Tạo phương thức riêng để thiết lập cột hành động
    private void setupActionColumn() {
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

    private void loadOrdersBasedOnUserRole() {
        User currentUser = UserSession.getCurrentUser();
        List<Order> orders = (currentUser.getRole() == UserRole.ADMIN || currentUser.getRole() == UserRole.THU_NGAN)
                ? orderService.getAllOrders()
                : orderService.getOrdersByEmployeeId(currentUser.getId());

        allOrders.setAll(orders);
        applyFilters();
    }

    private void applyFilters() {
        String keyword = searchField.getText().toLowerCase().trim();
        OrderStatus status = statusFilterComboBox.getValue();
        LocalDate from = fromDatePicker.getValue();
        LocalDate to = toDatePicker.getValue();

        List<Order> filtered = allOrders.stream()
                .filter(order -> matchesSearchKeyword(order, keyword))
                .filter(order -> status == null || order.getStatus() == status)
                .filter(order -> {
                    LocalDate date = order.getOrderDateAsLocalDate();
                    if (date == null) return true; // Thay đổi từ false thành true để không loại trừ đơn hàng không có ngày
                    boolean afterFrom = (from == null || !date.isBefore(from));
                    boolean beforeTo = (to == null || !date.isAfter(to));
                    return afterFrom && beforeTo;
                })
                .collect(Collectors.toList());

        orderList.setAll(filtered);

        int pageCount = (int) Math.ceil((double) orderList.size() / itemsPerPage);
        pagination.setPageCount(Math.max(1, pageCount));
        pagination.setCurrentPageIndex(0);
        currentPage.set(0);
        updateTableView();
    }

    private boolean matchesSearchKeyword(Order order, String keyword) {
        if (keyword.isEmpty()) return true;
        return String.valueOf(order.getId()).contains(keyword) ||
                String.valueOf(order.getCustomerId()).contains(keyword) ||
                (order.getCustomerName() != null && order.getCustomerName().toLowerCase().contains(keyword)) ||
                (order.getProductNames() != null && order.getProductNames().toLowerCase().contains(keyword)) ||
                (order.getNote() != null && order.getNote().toLowerCase().contains(keyword));
    }

    private void updateTableView() {
        int start = currentPage.get() * itemsPerPage;
        int end = Math.min(start + itemsPerPage, orderList.size());
        ordersTable.setItems(FXCollections.observableArrayList(orderList.subList(start, end)));

        setupActionColumn();
    }

    @FXML
    private void handleLogout() {
        LogoutHandler.handleLogout(logoutButton);
    }
}