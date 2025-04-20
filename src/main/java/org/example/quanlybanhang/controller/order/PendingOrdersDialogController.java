package org.example.quanlybanhang.controller.order;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.example.quanlybanhang.dao.OrderDAO;
import org.example.quanlybanhang.dto.orderDTO.OrderSummaryDTO;
import org.example.quanlybanhang.enums.ExportStatus;
import org.example.quanlybanhang.enums.OrderStatus;
import org.example.quanlybanhang.helpers.ButtonTableCell;
import org.example.quanlybanhang.helpers.DialogHelper;
import org.example.quanlybanhang.model.Order;
import org.example.quanlybanhang.utils.PaginationUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PendingOrdersDialogController {

    @FXML
    private TableView<OrderSummaryDTO> ordersTable;

    @FXML
    private TableColumn<OrderSummaryDTO, Integer> orderIdColumn;

    @FXML
    private TableColumn<OrderSummaryDTO, Integer> customerIdColumn;

    @FXML
    private TableColumn<OrderSummaryDTO, String> customerNameColumn;

    @FXML
    private TableColumn<OrderSummaryDTO, String> orderNameColumn;

    @FXML
    private TableColumn<OrderSummaryDTO, BigDecimal> shippingFeeColumn;

    @FXML
    private TableColumn<OrderSummaryDTO, LocalDateTime> orderDateColumn;

    @FXML
    private TableColumn<OrderSummaryDTO, BigDecimal> totalPriceColumn;

    @FXML
    private TableColumn<OrderSummaryDTO, String> statusColumn;

    @FXML
    private TableColumn<OrderSummaryDTO, String> noteColumn;

    @FXML
    private TableColumn<OrderSummaryDTO, Void> actionsColumn;

    @FXML
    private Button closeButton;

    @FXML
    private Pagination pagination;

    private ObservableList<OrderSummaryDTO> allOrders;
    private ObservableList<OrderSummaryDTO> displayedOrders;
    private final OrderDAO orderDAO = new OrderDAO();
    private final IntegerProperty currentPage = new SimpleIntegerProperty(0);

    @FXML
    public void initialize() {
        displayedOrders = FXCollections.observableArrayList();
        allOrders = FXCollections.observableArrayList();
        setupTableColumns();
        loadAllOrders();

        if (closeButton != null) {
            closeButton.setOnAction(event -> {
                Stage stage = (Stage) closeButton.getScene().getWindow();
                stage.close();
            });
        }
    }

    private void setupTableColumns() {
        // For Java records, we need to use lambdas instead of PropertyValueFactory
        orderIdColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().id()));
        customerIdColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().customerId()));
        customerNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().customerName()));
        orderNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().productNames()));
        shippingFeeColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().shippingFee()));
        orderDateColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().orderDate()));
        totalPriceColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().totalPrice()));
        statusColumn.setCellValueFactory(cellData -> {
            ExportStatus exportStatus = cellData.getValue().exportStatus();
            return new SimpleStringProperty(exportStatus != null ? exportStatus.getValue() : "");
        });
        noteColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().note()));

        setupActionsColumn();
    }

    private void setupActionsColumn() {
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button detailsButton = new Button("Chi tiết đơn hàng");
            private final Button exportButton = new Button("Xuất kho");
            private final HBox buttonsBox = new HBox(5, detailsButton, exportButton); // HBox với khoảng cách 5px

            {
                detailsButton.setOnAction(event -> {
                    OrderSummaryDTO order = getTableView().getItems().get(getIndex());
                    DialogHelper.showOrderDialog(
                            "/org/example/quanlybanhang/views/order/orderDetailsDialog.fxml",
                            "Chi tiết đơn hàng",
                            order.id(),
                            (Stage) ordersTable.getScene().getWindow()
                    );
                });

                exportButton.setOnAction(event -> {
                    OrderSummaryDTO order = getTableView().getItems().get(getIndex());
                    DialogHelper.showOrderDialog(
                            "/org/example/quanlybanhang/views/warehouse/warehouseImport.fxml",
                            "Xuất kho đơn hàng",
                            order.id(),
                            (Stage) ordersTable.getScene().getWindow()
                    );
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty || getTableRow() == null || getTableRow().getItem() == null ? null : buttonsBox);
            }
        });
    }

    private void loadAllOrders() {
        try {
            // Using existing methods to get orders and convert them to OrderSummaryDTO
            List<Order> orders = orderDAO.getAll();
            List<OrderSummaryDTO> orderSummaries = new ArrayList<>();

            // For each order, get its OrderSummaryDTO by ID
            for (Order order : orders) {
                OrderSummaryDTO summary = orderDAO.getOrderSummaryById(order.getId());
                if (summary != null) {
                    orderSummaries.add(summary);
                }
            }

            allOrders.setAll(orderSummaries);
            ordersTable.setItems(displayedOrders);

            if (pagination != null) {
                PaginationUtils.setup(
                        pagination,
                        allOrders,
                        displayedOrders,
                        currentPage,
                        18,
                        null
                );
            } else {
                displayedOrders.setAll(allOrders);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void filterPendingOrders() {
        if (allOrders == null) {
            loadAllOrders();
        }

        List<OrderSummaryDTO> pendingOrders = allOrders.stream()
                .filter(order -> order.status() == OrderStatus.DANG_XU_LY)
                .collect(Collectors.toList());

        allOrders.setAll(pendingOrders);

        if (pagination != null) {
            pagination.setCurrentPageIndex(0);
        } else {
            displayedOrders.setAll(pendingOrders);
        }
    }
}