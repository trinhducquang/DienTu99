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
import javafx.stage.Stage;
import org.example.quanlybanhang.dao.WarehouseDAO;
import org.example.quanlybanhang.dto.warehouseDTO.WarehouseDTO;
import org.example.quanlybanhang.enums.WarehouseType;
import org.example.quanlybanhang.helpers.DialogHelper;
import org.example.quanlybanhang.model.User;
import org.example.quanlybanhang.security.auth.UserSession;
import org.example.quanlybanhang.service.SearchService;
import org.example.quanlybanhang.utils.PaginationUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.example.quanlybanhang.enums.UserRole.NHAN_VIEN_KHO;


public class WarehouseController {
    // Constants
    private static final int ITEMS_PER_PAGE = 18;
    private static final int LOW_STOCK_THRESHOLD = 10;


    // Tab controllers
    private TransactionTabController transactionTabController;
    private ProductTabController productTabController;
    private DashboardTabController dashboardTabController;

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
    @FXML private ComboBox<WarehouseType> cboTransactionType; // Thêm dòng này


    // Products tab components
    @FXML private TableView<WarehouseDTO> tblProducts;
    @FXML private TextField txtSearchProduct;
    @FXML private DatePicker dpStartDateProduct;
    @FXML private DatePicker dpEndDateProduct;

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

    // Transaction tab components - will be initialized in TransactionTabController
    @FXML private TableView<WarehouseDTO> tblTransactions;
    @FXML private Button btnCreateTransaction;
    @FXML private TextField txtSearchTransaction;
    @FXML private DatePicker dpStartDateTransaction;
    @FXML private DatePicker dpEndDateTransaction;
    @FXML private Button btnLogout;



    // Data collections
    private ObservableList<WarehouseDTO> allProducts;
    private ObservableList<WarehouseDTO> allChecks;
    private ObservableList<WarehouseDTO> lowStockProducts;
    private ObservableList<WarehouseDTO> displayedProducts;
    private ObservableList<WarehouseDTO> displayedChecks;

    // Pagination state
    private final IntegerProperty currentProductPage = new SimpleIntegerProperty(0);
    private final IntegerProperty currentCheckPage = new SimpleIntegerProperty(0);

    // Data access
    private final WarehouseDAO warehouseDAO = new WarehouseDAO();

    @FXML
    public void initialize() {
        initializeCollections();
        initializeTabControllers();
        setupUI();
        loadData();

        // Kiểm tra vai trò người dùng và hiển thị/ẩn nút đăng xuất
        setupLogoutButton();
    }

    private void setupLogoutButton() {
        User currentUser = UserSession.getCurrentUser();
        if (currentUser != null && btnLogout != null) {
            btnLogout.setVisible(currentUser.getRole() == NHAN_VIEN_KHO);
            btnLogout.setOnAction(event -> handleLogout());
        }
    }

    private void handleLogout() {
        try {
            UserSession.clearSession();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/quanlybanhang/views/login/Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.setMaximized(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            // Hiển thị thông báo lỗi
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText(null);
            alert.setContentText("Không thể đăng xuất: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void initializeTabControllers() {
        // Khởi tạo ProductTabController
        initializeProductTab();

        // Khởi tạo TransactionTabController
        initializeTransactionTab();

        // Khởi tạo DashboardTabController
        initializeDashboardTab(); // Thêm dòng này

        // Thiết lập tham chiếu giữa các controller
        setupControllerReferences();
    }

    private void initializeDashboardTab() {
        try {
            dashboardTabController = new DashboardTabController();
            dashboardTabController.lblTotalProducts = this.lblTotalProducts;
            dashboardTabController.lblTotalValue = this.lblTotalValue;
            dashboardTabController.lblMonthlyTransactions = this.lblMonthlyTransactions;
            dashboardTabController.lblLowStockProducts = this.lblLowStockProducts;
            dashboardTabController.lblPendingOrders = this.lblPendingOrders;
            dashboardTabController.tblLowStockProducts = this.tblLowStockProducts;
            dashboardTabController.tblTopExportProducts = this.tblTopExportProducts;
            dashboardTabController.colLowStockSTT = this.colLowStockSTT;
            dashboardTabController.colLowStockProductId = this.colLowStockProductId;
            dashboardTabController.colLowStockProductName = this.colLowStockProductName;
            dashboardTabController.colLowStockQuantity = this.colLowStockQuantity;
            dashboardTabController.colTopExportSTT = this.colTopExportSTT;
            dashboardTabController.colTopExportProductId = this.colTopExportProductId;
            dashboardTabController.colTopExportProductName = this.colTopExportProductName;
            dashboardTabController.colTopExportQuantity = this.colTopExportQuantity;
            dashboardTabController.setMainController(this);
            dashboardTabController.initialize();
            dashboardTabController.setProductData(allProducts);
            if (transactionTabController != null) {
                dashboardTabController.setTransactionData(transactionTabController.getAllTransactions());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Không thể khởi tạo DashboardTabController: " + e.getMessage());
        }
    }

    private void initializeProductTab() {
        try {
            productTabController = new ProductTabController();

            // Truyền các thành phần FXML từ main controller sang product controller
            productTabController.setProductTable(this.tblProducts);
            productTabController.setSearchFields(
                    this.txtSearchProduct,
                    this.dpStartDateProduct,
                    this.dpEndDateProduct
            );
            productTabController.setPagination(this.pagination);

            // Thiết lập tham chiếu đến main controller
            productTabController.setMainController(this);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Không thể khởi tạo ProductTabController: " + e.getMessage());
        }
    }

    private void initializeTransactionTab() {
        try {
            transactionTabController = new TransactionTabController();

            // Inject basic UI controls first
            transactionTabController.btnCreateTransaction = this.btnCreateTransaction;
            transactionTabController.txtSearchTransaction = this.txtSearchTransaction;
            transactionTabController.dpStartDateTransaction = this.dpStartDateTransaction;
            transactionTabController.dpEndDateTransaction = this.dpEndDateTransaction;
            transactionTabController.tblTransactions = this.tblTransactions;
            transactionTabController.pagination = this.pagination;
            transactionTabController.cboTransactionType = this.cboTransactionType;

            // Directly inject all table columns from the parent table to the controller
            if (tblTransactions != null) {
                for (TableColumn<WarehouseDTO, ?> column : tblTransactions.getColumns()) {
                    String colId = column.getId();
                    if (colId != null) {
                        try {
                            // Use reflection to set the field by name
                            java.lang.reflect.Field field = TransactionTabController.class.getDeclaredField(colId);
                            field.setAccessible(true);
                            field.set(transactionTabController, column);
                        } catch (NoSuchFieldException | IllegalAccessException e) {
                            System.err.println("Failed to set column field: " + colId + " - " + e.getMessage());
                        }
                    }
                }
            }

            // Set reference to main controller and initialize
            transactionTabController.setMainController(this);
            transactionTabController.initialize();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Không thể khởi tạo TransactionTabController: " + e.getMessage());
        }
    }


    private void setupControllerReferences() {
        // Thiết lập tham chiếu giữa các controller
        if (productTabController != null && transactionTabController != null) {
            productTabController.setTransactionController(transactionTabController);
        }
    }

    private void initializeCollections() {
        displayedProducts = FXCollections.observableArrayList();
        displayedChecks = FXCollections.observableArrayList();
        lowStockProducts = FXCollections.observableArrayList();
        allProducts = FXCollections.observableArrayList();
        allChecks = FXCollections.observableArrayList();
    }

    private void setupUI() {
        setupButtons();
        setupCheckColumns();
        setupTabChangeListener();
        setupCheckSearch();
        setupTopExportColumns();
    }

    private void loadData() {
        loadNonTransactionData();

        // Khởi tạo ProductTabController nếu đã được thiết lập
        if (productTabController != null) {
            productTabController.initialize();
        }

        switchPaginationToCurrentTab();
    }

    private void setupButtons() {
        if (btnCreateCheck != null) {
            btnCreateCheck.setOnAction(event -> openCreateCheckDialog());
        }
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


    private void loadNonTransactionData() {
        try {
            allProducts.setAll(warehouseDAO.getAllWarehouseProducts());
            allChecks.setAll(warehouseDAO.getAllWarehouseCheck());

            updateProductStockLevels();

            // Cập nhật dữ liệu cho Dashboard nếu đã khởi tạo
            if (dashboardTabController != null) {
                dashboardTabController.setProductData(allProducts);
                if (transactionTabController != null) {
                    dashboardTabController.setTransactionData(transactionTabController.getAllTransactions());
                }
            } else {
                // Fallback nếu chưa khởi tạo dashboard controller
                updateLowStockProducts();
            }

            updateTableData();
            updateStatistics();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateTableData() {
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

        // Cập nhật tồn kho cho ProductTabController
        if (productTabController != null) {
            productTabController.updateProductStockLevels();
        }
    }

    private Map<Integer, Integer> calculateProductStock() {
        Map<Integer, Integer> productStockMap = new HashMap<>();

        // Initialize all products with zero stock
        for (WarehouseDTO product : allProducts) {
            productStockMap.put(product.getProductId(), 0);
        }

        // Calculate stock based on transactions from transaction controller
        if (transactionTabController != null) {
            ObservableList<WarehouseDTO> allTransactions = transactionTabController.getAllTransactions();

            // Sort transactions by creation time
            allTransactions.sort(Comparator.comparing(
                    dto -> dto.getCreatedAt() != null ? dto.getCreatedAt() : LocalDateTime.MIN
            ));

            // Calculate stock based on transactions
            for (WarehouseDTO transaction : allTransactions) {
                int productId = transaction.getProductId();
                int currentStock = productStockMap.getOrDefault(productId, 0);

                // Clear distinction between import and export
                if (transaction.getType() == WarehouseType.NHAP_KHO) {
                    currentStock += transaction.getQuantity();
                    System.out.println("Nhập kho: " + productId + ", SL: " + transaction.getQuantity() + ", Tồn mới: " + currentStock);
                } else if (transaction.getType() == WarehouseType.XUAT_KHO) {
                    currentStock -= transaction.getQuantity();
                    System.out.println("Xuất kho: " + productId + ", SL: " + transaction.getQuantity() + ", Tồn mới: " + currentStock);
                }

                productStockMap.put(productId, currentStock);
            }
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

    private void switchPaginationToCurrentTab() {
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
            case 3:
                // No pagination needed for dashboard
                break;
        }
    }

    private void switchPaginationToTransactions() {
        if (transactionTabController != null) {
            transactionTabController.setupPagination();
        }
    }

    private void switchPaginationToProducts() {
        // Sử dụng productTabController để thiết lập phân trang
        if (productTabController != null) {
            productTabController.setupPagination();
        } else {
            // Fallback nếu controller chưa được khởi tạo
            PaginationUtils.setup(
                    pagination,
                    allProducts,
                    displayedProducts,
                    currentProductPage,
                    ITEMS_PER_PAGE,
                    null
            );
        }
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
    private void openCreateCheckDialog() {
        try {
            DialogHelper.showDialog(
                    "/org/example/quanlybanhang/views/warehouse/WarehouseCheck.fxml",
                    "Tạo phiếu kiểm kho",
                    (Stage) tableWarehouseCheck.getScene().getWindow()
            );

            loadNonTransactionData();
            switchPaginationToChecks();
        } catch (Exception e) {
            System.err.println("Lỗi khi mở dialog kiểm kho: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Public method for child controllers to call
    public void refreshData() {
        loadNonTransactionData();
        int selectedTab = tabPane.getSelectionModel().getSelectedIndex();

        // Cập nhật dữ liệu cho tab hiện tại
        switch (selectedTab) {
            case 1:
                // Cập nhật dữ liệu sản phẩm thông qua ProductTabController
                if (productTabController != null) {
                    productTabController.loadData();
                }
                switchPaginationToProducts();
                break;
            case 2:
                switchPaginationToChecks();
                break;
            case 3:
                updateStatistics();
                updateLowStockProducts();
                loadTopExportedProducts();
                break;
        }
    }

    private void updateStatistics() {
        Map<Integer, Integer> productStockMap = calculateProductStock();

        if (dashboardTabController != null) {
            dashboardTabController.updateDashboard(productStockMap);
        }
    }

    private void loadTopExportedProducts() {
        // Create a map to track total export quantity by product
        Map<Integer, WarehouseDTO> productExportMap = new HashMap<>();

        // Calculate export statistics for each product if transaction controller is available
        if (transactionTabController != null) {
            ObservableList<WarehouseDTO> allTransactions = transactionTabController.getAllTransactions();

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
        if (dashboardTabController != null) {
            dashboardTabController.openPendingOrdersDialog();
        }
    }

}