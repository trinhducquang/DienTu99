package org.example.dientu99.controller.warehouse;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import org.example.dientu99.dao.WarehouseDAO;
import org.example.dientu99.dto.warehouseDTO.WarehouseDTO;
import org.example.dientu99.enums.WarehouseType;
import org.example.dientu99.helpers.LogoutHandler;
import org.example.dientu99.model.User;
import org.example.dientu99.security.auth.UserSession;

import java.time.LocalDateTime;
import java.util.*;

import static org.example.dientu99.enums.UserRole.NHAN_VIEN_KHO;


public class WarehouseController {
    // Constants
    private static final int ITEMS_PER_PAGE = 18;
    private static final int LOW_STOCK_THRESHOLD = 10;

    // Tab controllers
    private TransactionTabController transactionTabController;
    private DashboardTabController dashboardTabController;

    // Products tab components - now directly in main controller
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
    @FXML private ComboBox<WarehouseType> cboTransactionType;

    // Data collections
    private ObservableList<WarehouseDTO> allProducts;
    private ObservableList<WarehouseDTO> lowStockProducts;
    private ObservableList<WarehouseDTO> displayedProducts;

    // Pagination state
    private final IntegerProperty currentProductPage = new SimpleIntegerProperty(0);

    // Data access
    private final WarehouseDAO warehouseDAO = new WarehouseDAO();
    @FXML
    private StackPane chartPane;

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
        UserSession.clearSession();
        LogoutHandler.handleLogout(btnLogout);
    }

    private void initializeTabControllers() {
        initializeTransactionTab();
        initializeDashboardTab();
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
            dashboardTabController.chartPane = this.chartPane;
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

    private void initializeCollections() {
        displayedProducts = FXCollections.observableArrayList();
        lowStockProducts = FXCollections.observableArrayList();
        allProducts = FXCollections.observableArrayList();
    }

    private void setupUI() {
        setupTopExportColumns();
        setupTabChangeListener();
    }

    private void loadData() {
        loadNonTransactionData();
        switchPaginationToCurrentTab();
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

            updateStatistics();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    // In WarehouseController.java, modify the setupTabChangeListener method:
    private void setupTabChangeListener() {
        tabPane.getSelectionModel().selectedIndexProperty().addListener((obs, oldIndex, newIndex) -> {
            switch (newIndex.intValue()) {
                case 0:
                    // Reset transaction type filter when switching to transaction tab
                    if (cboTransactionType != null) {
                        cboTransactionType.setValue(null); // Reset to "Tất cả"
                    }
                    switchPaginationToTransactions();
                    break;
                case 1:
                    updateStatistics();
                    updateLowStockProducts();
                    loadTopExportedProducts();

                    // When switching to dashboard, ensure all data is shown regardless of filter
                    if (transactionTabController != null && transactionTabController.cboTransactionType != null) {
                        transactionTabController.cboTransactionType.setValue(null); // Reset to "Tất cả"
                        transactionTabController.applyTransactionFilters(); // Refresh with all data
                    }
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
                // No pagination needed for dashboard
                break;
        }
    }

    private void switchPaginationToTransactions() {
        if (transactionTabController != null) {
            transactionTabController.setupPagination();
        }
    }

    // Public method for child controllers to call
    public void refreshData() {
        loadNonTransactionData();
        int selectedTab = tabPane.getSelectionModel().getSelectedIndex();

        // Cập nhật dữ liệu cho tab hiện tại
        switch (selectedTab) {
            case 1:
                updateStatistics();
                updateLowStockProducts();
                loadTopExportedProducts();
                break;
        }
    }

    // In DashboardTabController.java (or within the updateStatistics method in WarehouseController):
    private void updateStatistics() {
        // Get all transactions from DAO directly instead of using filtered data
        List<WarehouseDTO> allTransactionData = warehouseDAO.getAllWarehouseDetails();

        // Use this complete data for calculations instead of relying on filtered data
        Map<Integer, Integer> productStockMap = calculateProductStock(allTransactionData);

        if (dashboardTabController != null) {
            dashboardTabController.updateDashboard(productStockMap);
        }
    }

    // Update the calculateProductStock method to accept the transaction list:
    private Map<Integer, Integer> calculateProductStock(List<WarehouseDTO> transactions) {
        Map<Integer, Integer> productStockMap = new HashMap<>();

        // Initialize all products with zero stock
        for (WarehouseDTO product : allProducts) {
            productStockMap.put(product.getProductId(), 0);
        }

        // Sort transactions by creation time
        List<WarehouseDTO> sortedTransactions = new ArrayList<>(transactions);
        sortedTransactions.sort(Comparator.comparing(
                dto -> dto.getCreatedAt() != null ? dto.getCreatedAt() : LocalDateTime.MIN
        ));

        // Calculate stock based on all transactions, ignoring current filter
        for (WarehouseDTO transaction : sortedTransactions) {
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