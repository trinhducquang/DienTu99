package org.example.quanlybanhang.controller.warehouse;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.quanlybanhang.dao.WarehouseDAO;
import org.example.quanlybanhang.dto.warehouseDTO.WarehouseDTO;
import org.example.quanlybanhang.enums.WarehouseType;
import org.example.quanlybanhang.helpers.DialogHelper;
import org.example.quanlybanhang.service.SearchService;
import org.example.quanlybanhang.utils.PaginationUtils;

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

    // Tab Sản Phẩm
    @FXML private TextField txtSearchProduct;
    @FXML private DatePicker dpStartDateProduct;
    @FXML private DatePicker dpEndDateProduct;
    @FXML private ComboBox<String> cboCategory;
    @FXML private TableView<WarehouseDTO> tableWarehouseProducts;
    @FXML private TableColumn<WarehouseDTO, Integer> colId;
    @FXML private TableColumn<WarehouseDTO, Integer> colProductId;
    @FXML private TableColumn<WarehouseDTO, String> colSku;
    @FXML private TableColumn<WarehouseDTO, String> colnameProduct;
    @FXML private TableColumn<WarehouseDTO, Integer> colStock;
    @FXML private TableColumn<WarehouseDTO, BigDecimal> colImportPrice;
    @FXML private TableColumn<WarehouseDTO, BigDecimal> colSellPrice;
    @FXML private TableColumn<WarehouseDTO, String> colNamecategory;
    @FXML private TableColumn<WarehouseDTO, LocalDateTime> colUpdatedAt;

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

    // TabPane
    @FXML private TabPane tabPane;

    // Pagination
    @FXML private Pagination pagination;

    // Source data lists
    private ObservableList<WarehouseDTO> allTransactions;
    private ObservableList<WarehouseDTO> allProducts;
    private ObservableList<WarehouseDTO> allChecks;

    // Displayed data (paginated)
    private ObservableList<WarehouseDTO> displayedTransactions;
    private ObservableList<WarehouseDTO> displayedProducts;
    private ObservableList<WarehouseDTO> displayedChecks;

    // Current page properties
    private IntegerProperty currentTransactionPage = new SimpleIntegerProperty(0);
    private IntegerProperty currentProductPage = new SimpleIntegerProperty(0);
    private IntegerProperty currentCheckPage = new SimpleIntegerProperty(0);

    // Items per page
    private static final int ITEMS_PER_PAGE = 18;

    // DAO
    private final WarehouseDAO warehouseDAO = new WarehouseDAO();

    @FXML
    public void initialize() {
        // Initialize observable lists
        displayedTransactions = FXCollections.observableArrayList();
        displayedProducts = FXCollections.observableArrayList();
        displayedChecks = FXCollections.observableArrayList();

        // Set up buttons
        setupButtons();

        // Configure table columns
        setupTableColumns();

        // Load data
        loadAllData();

        // Khai báo xử lý sự kiện khi chuyển tab
        setupTabChangeListener();

        // Add search functionality
        setupSearch();

        // Default tab pagination
        switchPaginationToTransactions();
    }

    private void setupButtons() {
        // Button create transaction
        btnCreateTransaction.setOnAction(event -> openCreateTransactionDialog());

        // Optional: Add other button setups if needed
        if (btnCreateCheck != null) {
            btnCreateCheck.setOnAction(event -> openCreateCheckDialog());
        }
    }

    private void setupTableColumns() {
        // Transactions table
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

        // Products table
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colProductId.setCellValueFactory(new PropertyValueFactory<>("productId"));
        colSku.setCellValueFactory(new PropertyValueFactory<>("sku"));
        colnameProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colImportPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colImportPrice.setCellFactory(currencyCellFactory());
        colSellPrice.setCellValueFactory(new PropertyValueFactory<>("sellPrice"));
        colSellPrice.setCellFactory(currencyCellFactory());
        colNamecategory.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        colUpdatedAt.setCellValueFactory(new PropertyValueFactory<>("updatedAt"));

        // Checks table
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
    }

    private void loadAllData() {
        try {
            System.out.println("Đang tải dữ liệu...");

            // Load all data from DAO
            allTransactions = FXCollections.observableArrayList(warehouseDAO.getAllWarehouseDetails());
            System.out.println("Số lượng giao dịch: " + allTransactions.size());

            allProducts = FXCollections.observableArrayList(warehouseDAO.getAllWarehouseProducts());
            System.out.println("Số lượng sản phẩm: " + allProducts.size());

            allChecks = FXCollections.observableArrayList(warehouseDAO.getAllWarehouseCheck());
            System.out.println("Số lượng phiếu kiểm: " + allChecks.size());

            // Tính toán tồn kho sau khi tải tất cả dữ liệu
            calculateStock();

            // Bind tables to displayed lists (which will be managed by pagination)
            tblTransactions.setItems(displayedTransactions);
            tableWarehouseProducts.setItems(displayedProducts);
            tableWarehouseCheck.setItems(displayedChecks);

        } catch (Exception e) {
            System.err.println("Lỗi khi tải dữ liệu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void calculateStock() {
        // Tạo bản đồ để lưu trữ tồn kho cho mỗi sản phẩm
        Map<Integer, Integer> productStockMap = new HashMap<>();

        // Tính tồn kho từ các giao dịch
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

        // Cập nhật tồn kho cho các sản phẩm
        for (WarehouseDTO product : allProducts) {
            int stock = productStockMap.getOrDefault(product.getProductId(), 0);
            product.setStock(stock);
        }
    }

    private void setupTabChangeListener() {
        // Add listener to switch pagination based on selected tab
        tabPane.getSelectionModel().selectedIndexProperty().addListener((obs, oldIndex, newIndex) -> {
            switch (newIndex.intValue()) {
                case 0: // Tab Giao Dịch Trong Kho
                    switchPaginationToTransactions();
                    break;
                case 1: // Tab Sản Phẩm Trong Kho
                    switchPaginationToProducts();
                    break;
                case 2: // Tab Kiểm Kho
                    switchPaginationToChecks();
                    break;
                default:
                    // Tab Thống Kê không cần phân trang
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
        // Search for transactions
        setupTransactionSearch();

        // Search for products
        setupProductSearch();

        // Search for checks
        setupCheckSearch();
    }

    private void setupTransactionSearch() {
        // Text search
        txtSearchTransaction.textProperty().addListener((obs, oldVal, newVal) -> {
            applyTransactionFilters();
        });

        // Date filters
        dpStartDateTransaction.valueProperty().addListener((obs, oldVal, newVal) -> applyTransactionFilters());
        dpEndDateTransaction.valueProperty().addListener((obs, oldVal, newVal) -> applyTransactionFilters());
    }

    private void setupProductSearch() {
        if (txtSearchProduct != null) {
            txtSearchProduct.textProperty().addListener((obs, oldVal, newVal) -> {
                applyProductFilters();
            });
        }

        if (dpStartDateProduct != null) {
            dpStartDateProduct.valueProperty().addListener((obs, oldVal, newVal) -> applyProductFilters());
        }

        if (dpEndDateProduct != null) {
            dpEndDateProduct.valueProperty().addListener((obs, oldVal, newVal) -> applyProductFilters());
        }

        if (cboCategory != null) {
            cboCategory.valueProperty().addListener((obs, oldVal, newVal) -> applyProductFilters());
        }
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
        // Get search values
        String searchText = txtSearchTransaction.getText();
        LocalDate startDate = dpStartDateTransaction.getValue();
        LocalDate endDate = dpEndDateTransaction.getValue();

        // Get all transactions from DAO
        List<WarehouseDTO> allData = warehouseDAO.getAllWarehouseDetails();

        // Use SearchService to filter data
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

        // Update the observable list
        allTransactions.setAll(filteredData);

        // Reset to first page
        pagination.setCurrentPageIndex(0);
    }

    private void applyProductFilters() {
        if (txtSearchProduct == null) return;

        // Get search values
        String searchText = txtSearchProduct.getText();
        LocalDate startDate = dpStartDateProduct != null ? dpStartDateProduct.getValue() : null;
        LocalDate endDate = dpEndDateProduct != null ? dpEndDateProduct.getValue() : null;
        String category = cboCategory != null ? cboCategory.getValue() : null;

        // Get all products from DAO
        List<WarehouseDTO> allData = warehouseDAO.getAllWarehouseProducts();

        // Use SearchService to filter data
        List<WarehouseDTO> filteredData = SearchService.search(
                allData,
                searchText,
                startDate,
                endDate,
                dto -> dto.getUpdatedAt() != null ? dto.getUpdatedAt().toLocalDate() : null,
                WarehouseDTO::getProductName,
                WarehouseDTO::getSku,
                WarehouseDTO::getCategoryName
        );

        // Apply category filter if needed
        if (category != null && !category.isEmpty()) {
            filteredData = filteredData.stream()
                    .filter(dto -> dto.getCategoryName() != null && dto.getCategoryName().equals(category))
                    .collect(Collectors.toList());
        }

        // Update the observable list
        allProducts.setAll(filteredData);

        // Reset to first page
        pagination.setCurrentPageIndex(0);
    }

    private void applyCheckFilters() {
        if (txtSearchCheck == null) return;

        // Get search values
        String searchText = txtSearchCheck.getText();
        LocalDate startDate = dpStartDateCheck != null ? dpStartDateCheck.getValue() : null;
        LocalDate endDate = dpEndDateCheck != null ? dpEndDateCheck.getValue() : null;
        String status = cboStatus != null ? cboStatus.getValue() : null;

        // Get all checks from DAO
        List<WarehouseDTO> allData = warehouseDAO.getAllWarehouseCheck();

        // Use SearchService to filter data
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

        // Update the observable list
        allChecks.setAll(filteredData);

        // Reset to first page
        pagination.setCurrentPageIndex(0);
    }

    private void openCreateTransactionDialog() {
        try {
            DialogHelper.showDialog("/org/example/quanlybanhang/views/warehouse/WarehouseImport.fxml",
                    "Tạo phiếu kho", (Stage) tblTransactions.getScene().getWindow());

            // Refresh data after dialog is closed
            loadAllData();

            // Apply current pagination
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

            // Refresh data after dialog is closed
            loadAllData();
            switchPaginationToChecks();
        } catch (Exception e) {
            System.err.println("Lỗi khi mở dialog kiểm kho: " + e.getMessage());
            e.printStackTrace();
        }
    }
}