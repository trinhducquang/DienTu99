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
import org.example.quanlybanhang.controller.order.PendingOrdersDialogController;
import org.example.quanlybanhang.dao.OrderDAO;
import org.example.quanlybanhang.dao.WarehouseDAO;
import org.example.quanlybanhang.dto.warehouseDTO.WarehouseDTO;
import org.example.quanlybanhang.enums.OrderStatus;
import org.example.quanlybanhang.enums.WarehouseType;
import org.example.quanlybanhang.helpers.DialogHelper;
import org.example.quanlybanhang.service.SearchService;
import org.example.quanlybanhang.utils.PaginationUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.example.quanlybanhang.utils.TableCellFactoryUtils.currencyCellFactory;

public class WarehouseController {
    // Constants
    private static final int ITEMS_PER_PAGE = 18;
    private static final int LOW_STOCK_THRESHOLD = 10;

    // Transaction tab components
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

    // Check tab components
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

    // Dashboard components
    @FXML private Label lblTotalProducts;
    @FXML private Label lblTotalValue;
    @FXML private Label lblMonthlyTransactions;
    @FXML private Label lblLowStockProducts;
    @FXML private Label lblPendingOrders;

    // Top products and low stock tables
    @FXML private TableView<WarehouseDTO> tblLowStockProducts;
    @FXML private TableColumn<WarehouseDTO, Integer> colLowStockSTT;
    @FXML private TableColumn<WarehouseDTO, Integer> colLowStockProductId;
    @FXML private TableColumn<WarehouseDTO, String> colLowStockProductName;
    @FXML private TableColumn<WarehouseDTO, Integer> colLowStockQuantity;
    @FXML private TableView<WarehouseDTO> tblTopExportProducts;
    @FXML private TableColumn<WarehouseDTO, Integer> colTopExportSTT;
    @FXML private TableColumn<WarehouseDTO, Integer> colTopExportProductId;
    @FXML private TableColumn<WarehouseDTO, String> colTopExportProductName;
    @FXML private TableColumn<WarehouseDTO, Integer> colTopExportQuantity;

    // Navigation and pagination
    @FXML private TabPane tabPane;
    @FXML private Pagination pagination;

    // Data collections
    private ObservableList<WarehouseDTO> allTransactions;
    private ObservableList<WarehouseDTO> allProducts;
    private ObservableList<WarehouseDTO> allChecks;
    private ObservableList<WarehouseDTO> lowStockProducts;
    private ObservableList<WarehouseDTO> displayedTransactions;
    private ObservableList<WarehouseDTO> displayedProducts;
    private ObservableList<WarehouseDTO> displayedChecks;

    // Pagination state
    private final IntegerProperty currentTransactionPage = new SimpleIntegerProperty(0);
    private final IntegerProperty currentProductPage = new SimpleIntegerProperty(0);
    private final IntegerProperty currentCheckPage = new SimpleIntegerProperty(0);

    // Data access
    private final WarehouseDAO warehouseDAO = new WarehouseDAO();

    @FXML
    public void initialize() {
        initializeCollections();
        setupUI();
        loadData();
    }

    private void initializeCollections() {
        displayedTransactions = FXCollections.observableArrayList();
        displayedProducts = FXCollections.observableArrayList();
        displayedChecks = FXCollections.observableArrayList();
        lowStockProducts = FXCollections.observableArrayList();
        allTransactions = FXCollections.observableArrayList();
        allProducts = FXCollections.observableArrayList();
        allChecks = FXCollections.observableArrayList();
    }

    private void setupUI() {
        setupButtons();
        setupTableColumns();
        setupTabChangeListener();
        setupSearch();
    }

    private void loadData() {
        loadAllData();
        switchPaginationToTransactions();
    }

    private void setupButtons() {
        btnCreateTransaction.setOnAction(event -> openCreateTransactionDialog());
        if (btnCreateCheck != null) {
            btnCreateCheck.setOnAction(event -> openCreateCheckDialog());
        }
    }

    private void setupTableColumns() {
        setupTransactionColumns();
        setupCheckColumns();
        setupTopExportColumns();
        if (tblLowStockProducts != null) {
            setupLowStockColumns();
        }
    }

    private void setupTransactionColumns() {
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
    }

    private void setupCheckColumns() {
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

    private void setupTopExportColumns() {
        colTopExportSTT.setCellValueFactory(cellData -> {
            int index = tblTopExportProducts.getItems().indexOf(cellData.getValue()) + 1;
            return new SimpleIntegerProperty(index).asObject();
        });
        colTopExportProductId.setCellValueFactory(new PropertyValueFactory<>("productId"));
        colTopExportProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colTopExportQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
    }

    private void setupLowStockColumns() {
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
            allTransactions.setAll(warehouseDAO.getAllWarehouseDetails());
            allProducts.setAll(warehouseDAO.getAllWarehouseProducts());
            allChecks.setAll(warehouseDAO.getAllWarehouseCheck());

            updateProductStockLevels();
            updateLowStockProducts();
            updateTableData();
            updateStatistics();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateTableData() {
        tblTransactions.setItems(displayedTransactions);
        tableWarehouseCheck.setItems(displayedChecks);
        if (tblLowStockProducts != null) {
            tblLowStockProducts.setItems(lowStockProducts);
        }
    }

    private void updateProductStockLevels() {
        Map<Integer, Integer> productStockMap = calculateProductStock();

        for (WarehouseDTO product : allProducts) {
            int stock = productStockMap.getOrDefault(product.getProductId(), 0);
            product.setStock(stock);
        }
    }

    private Map<Integer, Integer> calculateProductStock() {
        Map<Integer, Integer> productStockMap = new HashMap<>();

        // Initialize all products with zero stock
        for (WarehouseDTO product : allProducts) {
            productStockMap.put(product.getProductId(), 0);
        }

        // Calculate stock based on transactions
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

        return productStockMap;
    }

    private void updateLowStockProducts() {
        lowStockProducts.clear();

        // Use a map to ensure unique products
        Map<Integer, WarehouseDTO> uniqueProducts = new HashMap<>();

        for (WarehouseDTO product : allProducts) {
            if (product.getStock() <= LOW_STOCK_THRESHOLD) {
                uniqueProducts.putIfAbsent(product.getProductId(), product);
            }
        }

        lowStockProducts.addAll(uniqueProducts.values());

        // Sort by stock level (ascending)
        FXCollections.sort(lowStockProducts, Comparator.comparing(WarehouseDTO::getStock));
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
        txtSearchTransaction.textProperty().addListener((obs, oldVal, newVal) -> applyTransactionFilters());
        dpStartDateTransaction.valueProperty().addListener((obs, oldVal, newVal) -> applyTransactionFilters());
        dpEndDateTransaction.valueProperty().addListener((obs, oldVal, newVal) -> applyTransactionFilters());
    }

    private void setupCheckSearch() {
        if (txtSearchCheck != null) {
            txtSearchCheck.textProperty().addListener((obs, oldVal, newVal) -> applyCheckFilters());
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

        List<WarehouseDTO> filteredData = SearchService.search(
                warehouseDAO.getAllWarehouseDetails(),
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

        List<WarehouseDTO> filteredData = SearchService.search(
                warehouseDAO.getAllWarehouseCheck(),
                searchText,
                startDate,
                endDate,
                dto -> dto.getInventoryDate() != null ? dto.getInventoryDate().toLocalDate() : null,
                WarehouseDTO::getProductName,
                WarehouseDTO::getTransactionCode,
                WarehouseDTO::getCreatedByName
        );

        if (status != null && !status.isEmpty()) {
            filteredData = filteredData.stream()
                    .filter(dto -> dto.getInventoryStatus() != null &&
                            dto.getInventoryStatus().toString().equals(status))
                    .collect(Collectors.toList());
        }

        allChecks.setAll(filteredData);
        pagination.setCurrentPageIndex(0);
    }

    @FXML
    private void openCreateTransactionDialog() {
        try {
            DialogHelper.showDialog(
                    "/org/example/quanlybanhang/views/warehouse/WarehouseImport.fxml",
                    "Tạo phiếu kho",
                    (Stage) tblTransactions.getScene().getWindow()
            );

            refreshData();
        } catch (Exception e) {
            System.err.println("Lỗi khi mở dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void refreshData() {
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
                loadTopExportedProducts();
                break;
            case 3:
                updateStatistics();
                break;
        }
    }

    @FXML
    private void openCreateCheckDialog() {
        try {
            DialogHelper.showDialog(
                    "/org/example/quanlybanhang/views/warehouse/WarehouseCheck.fxml",
                    "Tạo phiếu kiểm kho",
                    (Stage) tableWarehouseCheck.getScene().getWindow()
            );

            loadAllData();
            switchPaginationToChecks();
        } catch (Exception e) {
            System.err.println("Lỗi khi mở dialog kiểm kho: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateStatistics() {
        Map<Integer, Integer> productStockMap = calculateProductStock();

        // Update pending orders count
        updatePendingOrdersCount();

        // Calculate total stock quantity and value
        int totalStockQuantity = calculateTotalStockQuantity(productStockMap);
        BigDecimal totalWarehouseValue = calculateTotalWarehouseValue(productStockMap);

        // Calculate monthly transactions
        long monthlyTransactionCount = calculateMonthlyTransactions();

        // Calculate low stock products count
        long lowStockCount = calculateLowStockCount();

        // Format and update UI
        updateStatisticsUI(totalStockQuantity, totalWarehouseValue, monthlyTransactionCount, lowStockCount);

        // Load top exported products
        loadTopExportedProducts();
    }

    private void updatePendingOrdersCount() {
        OrderDAO orderDAO = new OrderDAO();
        long pendingOrdersCount = orderDAO.getAll().stream()
                .filter(order -> order.getStatus() == OrderStatus.DANG_XU_LY)
                .count();

        String formattedPendingOrders = String.format("%,d", pendingOrdersCount);
        if (lblPendingOrders != null) lblPendingOrders.setText(formattedPendingOrders);
    }

    private int calculateTotalStockQuantity(Map<Integer, Integer> productStockMap) {
        return productStockMap.values().stream()
                .filter(stock -> stock > 0)
                .mapToInt(Integer::intValue)
                .sum();
    }

    private BigDecimal calculateTotalWarehouseValue(Map<Integer, Integer> productStockMap) {
        BigDecimal totalValue = BigDecimal.ZERO;

        for (WarehouseDTO product : allProducts) {
            int stock = productStockMap.getOrDefault(product.getProductId(), 0);

            if (stock > 0 && product.getUnitPrice() != null) {
                BigDecimal productValue = product.getUnitPrice().multiply(new BigDecimal(stock));
                totalValue = totalValue.add(productValue);
            }
        }

        return totalValue;
    }

    private long calculateMonthlyTransactions() {
        LocalDate now = LocalDate.now();
        LocalDate firstDayOfMonth = LocalDate.of(now.getYear(), now.getMonth(), 1);

        return allTransactions.stream()
                .filter(t -> t.getCreatedAt() != null &&
                        t.getCreatedAt().toLocalDate().isAfter(firstDayOfMonth.minusDays(1)))
                .count();
    }

    private long calculateLowStockCount() {
        return allProducts.stream()
                .filter(product -> product.getStock() <= LOW_STOCK_THRESHOLD)
                .map(WarehouseDTO::getProductId)
                .distinct()
                .count();
    }

    private void updateStatisticsUI(int totalStockQuantity, BigDecimal totalWarehouseValue,
                                    long monthlyTransactionCount, long lowStockCount) {
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/org/example/quanlybanhang/views/warehouse/WarehouseProductsDialog.fxml"));
            Parent dialogPane = loader.load();
            WarehouseProductsDialogController controller = loader.getController();

            controller.setProductData(allProducts, allTransactions);

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

    private void loadTopExportedProducts() {
        // Create a map to track total export quantity by product
        Map<Integer, WarehouseDTO> productExportMap = new HashMap<>();

        // Calculate export statistics for each product
        for (WarehouseDTO transaction : allTransactions) {
            if (transaction.getType() == WarehouseType.XUAT_KHO) {
                int productId = transaction.getProductId();
                String productName = transaction.getProductName();

                // Create or update the record for this product
                WarehouseDTO exportSummary = productExportMap.computeIfAbsent(
                        productId,
                        k -> {
                            WarehouseDTO dto = new WarehouseDTO();
                            dto.setProductId(productId);
                            dto.setProductName(productName);
                            dto.setQuantity(0);
                            return dto;
                        }
                );

                exportSummary.setQuantity(exportSummary.getQuantity() + transaction.getQuantity());
            }
        }

        // Sort by export quantity (descending) and get top 20
        List<WarehouseDTO> topExportProducts = new ArrayList<>(productExportMap.values());
        topExportProducts.sort(Comparator.comparing(WarehouseDTO::getQuantity).reversed());

        int limit = Math.min(20, topExportProducts.size());
        List<WarehouseDTO> top20ExportProducts = topExportProducts.subList(0, limit);

        // Update table
        tblTopExportProducts.setItems(FXCollections.observableArrayList(top20ExportProducts));
    }

    @FXML
    public void openPendingOrdersDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/org/example/quanlybanhang/views/order/OrderManagementDialog.fxml"));
            Parent dialogPane = loader.load();
            PendingOrdersDialogController controller = loader.getController();
            controller.filterPendingOrders();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Giao Dịch Đang Chờ Xử Lí");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(lblTotalProducts.getScene().getWindow());
            dialogStage.setScene(new Scene(dialogPane));
            dialogStage.showAndWait();
        } catch (IOException e) {
            System.err.println("Lỗi khi mở dialog đơn hàng đang chờ: " + e.getMessage());
            e.printStackTrace();
        }
    }
}