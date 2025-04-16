package org.example.quanlybanhang.controller.warehouse;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.quanlybanhang.dao.WarehouseDAO;
import org.example.quanlybanhang.dto.warehouseDTO.WarehouseDTO;
import org.example.quanlybanhang.enums.WarehouseType;
import org.example.quanlybanhang.helpers.DialogHelper;
import org.example.quanlybanhang.service.SearchService;
import org.example.quanlybanhang.utils.PaginationUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.example.quanlybanhang.utils.TableCellFactoryUtils.*;

public class WarehouseController {

    // Tab Giao Dịch
    @FXML private Button btnCreateTransaction;
    @FXML private TextField txtSearchTransaction;
    @FXML private DatePicker dpStartDateTransaction;
    @FXML private DatePicker dpEndDateTransaction;
    @FXML private TableView<WarehouseDTO> tblTransactions;
    @FXML private TableColumn<WarehouseDTO, Integer> colTransId;
    @FXML private TableColumn<WarehouseDTO, Integer> colProductCode;
    @FXML private TableColumn<WarehouseDTO, String> colTransCode;
    @FXML private TableColumn<WarehouseDTO, String> colProductName;
    @FXML private TableColumn<WarehouseDTO, String> colCategory;
    @FXML private TableColumn<WarehouseDTO, Integer> colQuantity;
    @FXML private TableColumn<WarehouseDTO, BigDecimal> colUnitPrice;
    @FXML private TableColumn<WarehouseDTO, BigDecimal> colTotalAmount;
    @FXML private TableColumn<WarehouseDTO, String> colNote;
    @FXML private TableColumn<WarehouseDTO, String> colCreatedBy;
    @FXML private TableColumn<WarehouseDTO, LocalDate> colCreatedDate;

    // Tab Kiểm Kho
    @FXML private Button btnCreateCheck;
    @FXML private TextField txtSearchCheck;
    @FXML private DatePicker dpStartDateCheck;
    @FXML private DatePicker dpEndDateCheck;
    @FXML private ComboBox<String> cboStatus;
    @FXML private TableView<WarehouseDTO> tableWarehouseCheck;
    @FXML private TableColumn<WarehouseDTO, String> colIdCheck;
    @FXML private TableColumn<WarehouseDTO, LocalDateTime> colCheckdate;
    @FXML private TableColumn<WarehouseDTO, String> checker;
    @FXML private TableColumn<WarehouseDTO, String> colcheckProduct;
    @FXML private TableColumn<WarehouseDTO, Integer> colProductNumber;
    @FXML private TableColumn<WarehouseDTO, Integer> colExcessProduct;
    @FXML private TableColumn<WarehouseDTO, Integer> colmissingProduct;
    @FXML private TableColumn<WarehouseDTO, Integer> colDefectiveProduct;
    @FXML private TableColumn<WarehouseDTO, Enum<?>> colCheckStatus;
    @FXML private TableColumn<WarehouseDTO, String> colcheckNote;

    // Tab Thống Kê
    @FXML private Label lblTotalProducts;
    @FXML private Label lblTotalValue;
    @FXML private Label lblMonthlyTransactions;
    @FXML private Label lblLowStockProducts;

    // Bảng sản phẩm sắp hết hàng
    @FXML private TableView<WarehouseDTO> tblLowStockProducts;
    @FXML private TableColumn<WarehouseDTO, Integer> colLowStockSTT;
    @FXML private TableColumn<WarehouseDTO, Integer> colLowStockProductId;
    @FXML private TableColumn<WarehouseDTO, String> colLowStockProductName;
    @FXML private TableColumn<WarehouseDTO, Integer> colLowStockQuantity;

    @FXML private TabPane tabPane;
    @FXML private Pagination pagination;
    private ObservableList<WarehouseDTO> allTransactions;
    private ObservableList<WarehouseDTO> allProducts;
    private ObservableList<WarehouseDTO> allChecks;
    private ObservableList<WarehouseDTO> lowStockProducts;
    private ObservableList<WarehouseDTO> displayedTransactions;
    private ObservableList<WarehouseDTO> displayedProducts;
    private ObservableList<WarehouseDTO> displayedChecks;
    private IntegerProperty currentTransactionPage = new SimpleIntegerProperty(0);
    private IntegerProperty currentProductPage = new SimpleIntegerProperty(0);
    private IntegerProperty currentCheckPage = new SimpleIntegerProperty(0);
    private static final int ITEMS_PER_PAGE = 18;
    private static final int LOW_STOCK_THRESHOLD = 10;
    private final WarehouseDAO warehouseDAO = new WarehouseDAO();

    @FXML
    public void initialize() {
        displayedTransactions = FXCollections.observableArrayList();
        displayedProducts = FXCollections.observableArrayList();
        displayedChecks = FXCollections.observableArrayList();
        lowStockProducts = FXCollections.observableArrayList();
        setupButtons();
        setupTableColumns();
        loadAllData();
        setupTabChangeListener();
        setupSearch();
        switchPaginationToTransactions();
    }

    private void setupButtons() {
        btnCreateTransaction.setOnAction(event -> openCreateTransactionDialog());
        if (btnCreateCheck != null) {
            btnCreateCheck.setOnAction(event -> openCreateCheckDialog());
        }
    }

    private void setupTableColumns() {
        // Các cột cho tab giao dịch
        colTransId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colProductCode.setCellValueFactory(new PropertyValueFactory<>("productId"));
        colTransCode.setCellValueFactory(new PropertyValueFactory<>("transactionCode"));
        colProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colUnitPrice.setCellFactory(currencyCellFactory());
        colTotalAmount.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        colTotalAmount.setCellFactory(currencyCellFactory());
        colNote.setCellValueFactory(new PropertyValueFactory<>("note"));
        colCreatedBy.setCellValueFactory(new PropertyValueFactory<>("createdByName"));
        colCreatedDate.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        // Cột cho tab kiểm kho
        colIdCheck.setCellValueFactory(new PropertyValueFactory<>("transactionCode"));
        colCheckdate.setCellValueFactory(new PropertyValueFactory<>("inventoryDate"));
        checker.setCellValueFactory(new PropertyValueFactory<>("createdByName"));
        colcheckProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colProductNumber.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colExcessProduct.setCellValueFactory(new PropertyValueFactory<>("excessQuantity"));
        colmissingProduct.setCellValueFactory(new PropertyValueFactory<>("missing"));
        colDefectiveProduct.setCellValueFactory(new PropertyValueFactory<>("deficientQuantity"));
        colCheckStatus.setCellValueFactory(new PropertyValueFactory<>("inventoryStatus"));
        colcheckNote.setCellValueFactory(new PropertyValueFactory<>("InventoryNote"));

        if (tblLowStockProducts != null) {
            setupLowStockTable();
        }
    }

    private void setupLowStockTable() {
        colLowStockSTT.setCellValueFactory(cellData -> {
            int index = lowStockProducts.indexOf(cellData.getValue()) + 1;
            return new SimpleIntegerProperty(index).asObject();
        });
        colLowStockProductId.setCellValueFactory(new PropertyValueFactory<>("productId"));
        colLowStockProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colLowStockQuantity.setCellValueFactory(new PropertyValueFactory<>("stock"));
    }

    private void loadAllData() {
        try {
            allTransactions = FXCollections.observableArrayList(warehouseDAO.getAllWarehouseDetails());
            allProducts = FXCollections.observableArrayList(warehouseDAO.getAllWarehouseProducts());
            allChecks = FXCollections.observableArrayList(warehouseDAO.getAllWarehouseCheck());
            calculateStock();
            updateLowStockProducts();
            tblTransactions.setItems(displayedTransactions);
            tableWarehouseCheck.setItems(displayedChecks);
            if (tblLowStockProducts != null) {
                tblLowStockProducts.setItems(lowStockProducts);
            }
            updateStatistics();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void calculateStock() {
        Map<Integer, Integer> productStockMap = new HashMap<>();
        for (WarehouseDTO transaction : allTransactions) {
            int productId = transaction.getProductId();
            int currentStock = productStockMap.getOrDefault(productId, 0);

            if (transaction.getType() == WarehouseType.NHAP_KHO) {
                currentStock += transaction.getQuantity();
            } else if (transaction.getType() == WarehouseType.XUAT_KHO) {
                currentStock -= transaction.getQuantity();
            }

            productStockMap.put(productId, currentStock);
        }
        for (WarehouseDTO product : allProducts) {
            int stock = productStockMap.getOrDefault(product.getProductId(), 0);
            product.setStock(stock);
        }
    }

    private void updateLowStockProducts() {
        lowStockProducts.clear();
        for (WarehouseDTO product : allProducts) {
            if (product.getStock() <= LOW_STOCK_THRESHOLD) {
                lowStockProducts.add(product);
            }
        }

        // Sắp xếp sản phẩm theo số lượng tồn kho tăng dần
        FXCollections.sort(lowStockProducts, (p1, p2) -> Integer.compare(p1.getStock(), p2.getStock()));
    }

    private void setupTabChangeListener() {
        tabPane.getSelectionModel().selectedIndexProperty().addListener((obs, oldIndex, newIndex) -> {
            switch (newIndex.intValue()) {
                case 0:
                    switchPaginationToTransactions();
                    break;
                case 1:
                    switchPaginationToProducts();
                    break;
                case 2:
                    switchPaginationToChecks();
                    break;
                case 3:
                    updateStatistics();
                    updateLowStockProducts();
                    break;
                default:
                    break;
            }
        });
    }

    private void switchPaginationToTransactions() {
        PaginationUtils.setup(
                pagination,
                allTransactions,
                displayedTransactions,
                currentTransactionPage,
                ITEMS_PER_PAGE,
                null
        );
    }

    private void switchPaginationToProducts() {
        PaginationUtils.setup(
                pagination,
                allProducts,
                displayedProducts,
                currentProductPage,
                ITEMS_PER_PAGE,
                null
        );
    }

    private void switchPaginationToChecks() {
        PaginationUtils.setup(
                pagination,
                allChecks,
                displayedChecks,
                currentCheckPage,
                ITEMS_PER_PAGE,
                null
        );
    }

    private void setupSearch() {
        setupTransactionSearch();
        setupCheckSearch();
    }

    private void setupTransactionSearch() {
        txtSearchTransaction.textProperty().addListener((obs, oldVal, newVal) -> {
            applyTransactionFilters();
        });
        dpStartDateTransaction.valueProperty().addListener((obs, oldVal, newVal) -> applyTransactionFilters());
        dpEndDateTransaction.valueProperty().addListener((obs, oldVal, newVal) -> applyTransactionFilters());
    }

    private void setupCheckSearch() {
        if (txtSearchCheck != null) {
            txtSearchCheck.textProperty().addListener((obs, oldVal, newVal) -> {
                applyCheckFilters();
            });
        }

        if (dpStartDateCheck != null) {
            dpStartDateCheck.valueProperty().addListener((obs, oldVal, newVal) -> applyCheckFilters());
        }

        if (dpEndDateCheck != null) {
            dpEndDateCheck.valueProperty().addListener((obs, oldVal, newVal) -> applyCheckFilters());
        }

        if (cboStatus != null) {
            cboStatus.valueProperty().addListener((obs, oldVal, newVal) -> applyCheckFilters());
        }
    }

    private void applyTransactionFilters() {
        String searchText = txtSearchTransaction.getText();
        LocalDate startDate = dpStartDateTransaction.getValue();
        LocalDate endDate = dpEndDateTransaction.getValue();
        List<WarehouseDTO> allData = warehouseDAO.getAllWarehouseDetails();
        List<WarehouseDTO> filteredData = SearchService.search(
                allData,
                searchText,
                startDate,
                endDate,
                dto -> dto.getCreatedAt() != null ? dto.getCreatedAt().toLocalDate() : null,
                WarehouseDTO::getProductName,
                WarehouseDTO::getTransactionCode,
                WarehouseDTO::getCategoryName
        );
        allTransactions.setAll(filteredData);
        pagination.setCurrentPageIndex(0);
    }

    private void applyCheckFilters() {
        if (txtSearchCheck == null) return;
        String searchText = txtSearchCheck.getText();
        LocalDate startDate = dpStartDateCheck != null ? dpStartDateCheck.getValue() : null;
        LocalDate endDate = dpEndDateCheck != null ? dpEndDateCheck.getValue() : null;
        String status = cboStatus != null ? cboStatus.getValue() : null;
        List<WarehouseDTO> allData = warehouseDAO.getAllWarehouseCheck();
        List<WarehouseDTO> filteredData = SearchService.search(
                allData,
                searchText,
                startDate,
                endDate,
                dto -> dto.getInventoryDate() != null ? dto.getInventoryDate().toLocalDate() : null,
                WarehouseDTO::getProductName,
                WarehouseDTO::getTransactionCode,
                WarehouseDTO::getCreatedByName
        );

        // Apply status filter if needed
        if (status != null && !status.isEmpty()) {
            filteredData = filteredData.stream()
                    .filter(dto -> dto.getInventoryStatus() != null &&
                            dto.getInventoryStatus().toString().equals(status))
                    .collect(Collectors.toList());
        }
        allChecks.setAll(filteredData);
        pagination.setCurrentPageIndex(0);
    }

    private void openCreateTransactionDialog() {
        try {
            DialogHelper.showDialog("/org/example/quanlybanhang/views/warehouse/WarehouseImport.fxml",
                    "Tạo phiếu kho", (Stage) tblTransactions.getScene().getWindow());
            loadAllData();
            int selectedTab = tabPane.getSelectionModel().getSelectedIndex();
            switch (selectedTab) {
                case 0:
                    switchPaginationToTransactions();
                    break;
                case 1:
                    switchPaginationToProducts();
                    break;
                case 2:
                    switchPaginationToChecks();
                    break;
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi mở dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void openCreateCheckDialog() {
        try {
            DialogHelper.showDialog("/org/example/quanlybanhang/views/warehouse/WarehouseCheck.fxml",
                    "Tạo phiếu kiểm kho", (Stage) tableWarehouseCheck.getScene().getWindow());
            loadAllData();
            switchPaginationToChecks();
        } catch (Exception e) {
            System.err.println("Lỗi khi mở dialog kiểm kho: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateStatistics() {
        int totalStockQuantity = 0;
        BigDecimal totalWarehouseValue = BigDecimal.ZERO;

        for (WarehouseDTO product : allProducts) {
            totalStockQuantity += product.getStock();

            BigDecimal productValue = product.getUnitPrice().multiply(new BigDecimal(product.getStock()));
            totalWarehouseValue = totalWarehouseValue.add(productValue);
        }

        LocalDate now = LocalDate.now();
        LocalDate firstDayOfMonth = LocalDate.of(now.getYear(), now.getMonth(), 1);
        long monthlyTransactionCount = allTransactions.stream()
                .filter(t -> t.getCreatedAt() != null &&
                        t.getCreatedAt().toLocalDate().isAfter(firstDayOfMonth.minusDays(1)))
                .count();

        long lowStockCount = allProducts.stream()
                .filter(p -> p.getStock() <= LOW_STOCK_THRESHOLD)
                .count();

        String formattedTotalStock = String.format("%,d", totalStockQuantity);
        String formattedWarehouseValue = String.format("%,.0fđ", totalWarehouseValue);
        String formattedMonthlyTransactions = String.format("%,d", monthlyTransactionCount);
        String formattedLowStock = String.format("%,d", lowStockCount);

        if (lblTotalProducts != null) lblTotalProducts.setText(formattedTotalStock);
        if (lblTotalValue != null) lblTotalValue.setText(formattedWarehouseValue);
        if (lblMonthlyTransactions != null) lblMonthlyTransactions.setText(formattedMonthlyTransactions);
        if (lblLowStockProducts != null) lblLowStockProducts.setText(formattedLowStock);
    }

    @FXML
    public void openProductsDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/quanlybanhang/views/warehouse/WarehouseProductsDialog.fxml"));
            Parent dialogPane = loader.load();
            WarehouseProductsDialogController controller = loader.getController();
            controller.setProductData(allProducts);
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Sản Phẩm Trong Kho");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(lblTotalProducts.getScene().getWindow());
            dialogStage.setScene(new Scene(dialogPane));
            dialogStage.showAndWait();

        } catch (IOException e) {
            System.err.println("Lỗi khi mở dialog sản phẩm: " + e.getMessage());
            e.printStackTrace();
        }
    }
}