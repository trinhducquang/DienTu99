package org.example.quanlybanhang.controller.warehouse;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.example.quanlybanhang.dao.WarehouseDAO;
import org.example.quanlybanhang.dto.warehouseDTO.WarehouseDTO;
import org.example.quanlybanhang.enums.WarehouseType;
import org.example.quanlybanhang.service.SearchService;
import org.example.quanlybanhang.utils.PaginationUtils;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductTabController {
    // Constants
    private static final int ITEMS_PER_PAGE = 18;

    // Products tab components
    @FXML private TableView<WarehouseDTO> tblProducts;
    @FXML private TextField txtSearchProduct;
    @FXML private DatePicker dpStartDateProduct;
    @FXML private DatePicker dpEndDateProduct;

    // Pagination
    private Pagination pagination;

    // Data collections
    private ObservableList<WarehouseDTO> allProducts;
    private ObservableList<WarehouseDTO> displayedProducts;

    // Parent controller reference
    private WarehouseController mainController;

    // Pagination state
    private final IntegerProperty currentProductPage = new SimpleIntegerProperty(0);

    // Data access
    private final WarehouseDAO warehouseDAO = new WarehouseDAO();

    // Transaction controller reference for stock calculations
    private TransactionTabController transactionTabController;

    public ProductTabController() {
        initializeCollections();
    }

    private void initializeCollections() {
        allProducts = FXCollections.observableArrayList();
        displayedProducts = FXCollections.observableArrayList();
    }

    public void initialize() {
        setupUI();
        loadData();
    }

    public void setMainController(WarehouseController controller) {
        this.mainController = controller;
    }

    public void setTransactionController(TransactionTabController controller) {
        this.transactionTabController = controller;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    private void setupUI() {
        setupProductSearch();
    }

    private void setupProductSearch() {
        if (txtSearchProduct != null) {
            txtSearchProduct.textProperty().addListener((obs, oldVal, newVal) -> applyProductFilters());
        }

        if (dpStartDateProduct != null) {
            dpStartDateProduct.valueProperty().addListener((obs, oldVal, newVal) -> applyProductFilters());
        }

        if (dpEndDateProduct != null) {
            dpEndDateProduct.valueProperty().addListener((obs, oldVal, newVal) -> applyProductFilters());
        }
    }

    private void applyProductFilters() {
        if (txtSearchProduct == null) return;

        String searchText = txtSearchProduct.getText();
        LocalDate startDate = dpStartDateProduct != null ? dpStartDateProduct.getValue() : null;
        LocalDate endDate = dpEndDateProduct != null ? dpEndDateProduct.getValue() : null;

        List<WarehouseDTO> filteredData = SearchService.search(
                warehouseDAO.getAllWarehouseProducts(),
                searchText,
                startDate,
                endDate,
                dto -> dto.getInventoryDate() != null ? dto.getInventoryDate().toLocalDate() : null,
                WarehouseDTO::getProductName,
                WarehouseDTO::getTransactionCode,
                WarehouseDTO::getCreatedByName
        );

        allProducts.setAll(filteredData);
        updateProductStockLevels();
        setupPagination();
    }

    public void loadData() {
        try {
            allProducts.setAll(warehouseDAO.getAllWarehouseProducts());
            updateProductStockLevels();
            setupPagination();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setupPagination() {
        if (pagination != null) {
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

    public void updateProductStockLevels() {
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

        // Calculate stock based on transactions from the transaction controller
        if (transactionTabController != null) {
            ObservableList<WarehouseDTO> allTransactions = transactionTabController.getAllTransactions();

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
        }

        return productStockMap;
    }

    public ObservableList<WarehouseDTO> getAllProducts() {
        return allProducts;
    }

    // Method to set table reference from main controller
    public void setProductTable(TableView<WarehouseDTO> table) {
        this.tblProducts = table;
    }

    // Method to set search fields from main controller
    public void setSearchFields(TextField txtSearch, DatePicker dpStart, DatePicker dpEnd) {
        this.txtSearchProduct = txtSearch;
        this.dpStartDateProduct = dpStart;
        this.dpEndDateProduct = dpEnd;
    }
}