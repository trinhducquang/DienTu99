package org.example.dientu99.controller.order;

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
import org.example.dientu99.controller.interfaces.RefreshableView;
import org.example.dientu99.dao.OrderDAO;
import org.example.dientu99.dto.orderDTO.OrderSummaryDTO;
import org.example.dientu99.enums.ExportStatus;
import org.example.dientu99.helpers.DialogHelper;
import org.example.dientu99.utils.AsyncDataLoader;
import org.example.dientu99.utils.ThreadManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

public class PendingOrdersDialogController implements RefreshableView {

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

    @FXML
    private ProgressIndicator loadingIndicator;

    private final ObservableList<OrderSummaryDTO> displayedOrders = FXCollections.observableArrayList();
    private final OrderDAO orderDAO = new OrderDAO();
    private final IntegerProperty currentPage = new SimpleIntegerProperty(0);
    private final IntegerProperty totalPages = new SimpleIntegerProperty(0);
    private final IntegerProperty totalItems = new SimpleIntegerProperty(0);
    private final int PAGE_SIZE = 18;
    private boolean isLoading = false;
    private CompletableFuture<Void> currentLoadTask = null;

    @FXML
    public void initialize() {
        setupTableColumns();
        ordersTable.setItems(displayedOrders);

        // Thiết lập sự kiện cho nút đóng
        if (closeButton != null) {
            closeButton.setOnAction(event -> {
                Stage stage = (Stage) closeButton.getScene().getWindow();
                stage.close();
            });
        }

        // Thiết lập phân trang
        if (pagination != null) {
            setupPagination();
        }

        // Tải trang đầu tiên
        loadInitialData();
    }

    private void setupPagination() {
        pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
            if (!isLoading) {
                currentPage.set(newIndex.intValue());
                loadPage(currentPage.get());
            }
        });
    }

    private void loadInitialData() {
        // Kiểm tra null trước khi sử dụng loadingIndicator
        if (loadingIndicator != null) {
            loadingIndicator.setVisible(true);
        }

        ThreadManager.runBackground(() -> {
            int count = orderDAO.countTotalOrders();
            ThreadManager.runOnUiThread(() -> {
                totalItems.set(count);
                int pages = (count + PAGE_SIZE - 1) / PAGE_SIZE;
                totalPages.set(pages);

                if (pagination != null) {
                    pagination.setPageCount(pages > 0 ? pages : 1);
                }

                // Tải trang đầu tiên
                loadPage(0);
            });
        });
    }

    private void loadPage(int pageIndex) {
        if (isLoading && currentLoadTask != null) {
            currentLoadTask.cancel(true);
        }

        isLoading = true;
        displayedOrders.clear();

        if (loadingIndicator != null) {
            loadingIndicator.setVisible(true);
        }

        currentLoadTask = AsyncDataLoader.loadPageAsync(
                pageIndex,
                PAGE_SIZE,
                orderDAO::getOrderSummariesPaginated,
                orders -> {
                    ThreadManager.runOnUiThread(() -> {
                        displayedOrders.setAll(orders);
                        if (loadingIndicator != null) {
                            loadingIndicator.setVisible(false);
                        }
                        isLoading = false;
                    });
                },
                error -> {
                    ThreadManager.runOnUiThread(() -> {
                        if (loadingIndicator != null) {
                            loadingIndicator.setVisible(false);
                        }
                        isLoading = false;
                        showErrorDialog("Không thể tải dữ liệu", error.getMessage());
                    });
                }
        );
    }

    private void setupTableColumns() {
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
            private final HBox buttonsBox = new HBox(5, detailsButton, exportButton);

            {
                detailsButton.setOnAction(event -> {
                    OrderSummaryDTO order = getTableView().getItems().get(getIndex());
                    DialogHelper.showOrderDialog(
                            "/org/example/dientu99/views/order/orderDetailsDialog.fxml",
                            "Chi tiết đơn hàng",
                            order.id(),
                            (Stage) ordersTable.getScene().getWindow()
                    );
                });

                exportButton.setOnAction(event -> {
                    OrderSummaryDTO order = getTableView().getItems().get(getIndex());
                    DialogHelper.showWarehouseExportDialog(
                            "/org/example/dientu99/views/warehouse/warehouseImport.fxml",
                            "Xuất kho đơn hàng",
                            order.id(),
                            (Stage) ordersTable.getScene().getWindow(),
                            PendingOrdersDialogController.this
                    );
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    return;
                }

                OrderSummaryDTO order = getTableRow().getItem();
                if (order.exportStatus() == ExportStatus.DA_XUAT_KHO) {
                    setGraphic(new HBox(5, detailsButton));
                } else {
                    setGraphic(buttonsBox);
                }
            }
        });
    }

    public void filterPendingOrders() {
        // Đặt lại phân trang và tải dữ liệu mới
        if (pagination != null) {
            pagination.setCurrentPageIndex(0);
        } else {
            loadPage(0);
        }
    }

    @Override
    public void refresh() {
        currentPage.set(0);
        if (pagination != null) {
            pagination.setCurrentPageIndex(0);
        } else {
            loadPage(0);
        }
    }

    private void showErrorDialog(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}