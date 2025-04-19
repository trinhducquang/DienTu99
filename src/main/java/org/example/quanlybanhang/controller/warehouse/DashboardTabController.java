package org.example.quanlybanhang.controller.warehouse;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.quanlybanhang.controller.order.PendingOrdersDialogController;
import org.example.quanlybanhang.dao.OrderDAO;
import org.example.quanlybanhang.dto.warehouseDTO.WarehouseDTO;
import org.example.quanlybanhang.enums.OrderStatus;
import org.example.quanlybanhang.enums.WarehouseType;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;


public class DashboardTabController {
    // Constants
    private static final int LOW_STOCK_THRESHOLD = 10;

    // Main controller reference
    private WarehouseController mainController;

    // Dashboard components
    @FXML
    Label lblTotalProducts;
    @FXML
    Label lblTotalValue;
    @FXML
    Label lblMonthlyTransactions;
    @FXML
    Label lblLowStockProducts;
    @FXML
    Label lblPendingOrders;

    // Top products and low stock tables
    @FXML
    TableView<WarehouseDTO> tblLowStockProducts;
    @FXML
    TableColumn<WarehouseDTO, Integer> colLowStockSTT;
    @FXML
    TableColumn<WarehouseDTO, Integer> colLowStockProductId;
    @FXML
    TableColumn<WarehouseDTO, String> colLowStockProductName;
    @FXML
    TableColumn<WarehouseDTO, Integer> colLowStockQuantity;
    @FXML
    TableView<WarehouseDTO> tblTopExportProducts;
    @FXML
    TableColumn<WarehouseDTO, Integer> colTopExportSTT;
    @FXML
    TableColumn<WarehouseDTO, Integer> colTopExportProductId;
    @FXML
    TableColumn<WarehouseDTO, String> colTopExportProductName;
    @FXML
    TableColumn<WarehouseDTO, Integer> colTopExportQuantity;
    @FXML
    Pane chartPane;

    // Data collections
    private ObservableList<WarehouseDTO> allProducts;
    private ObservableList<WarehouseDTO> lowStockProducts;
    private ObservableList<WarehouseDTO> allTransactions;

    // Constructor
    public DashboardTabController() {
        this.lowStockProducts = FXCollections.observableArrayList();
    }

    // Initialize method
    public void initialize() {
        setupTableColumns();
        setupChartController();
    }

    private void setupChartController() {
        WarehouseChartController chartController = new WarehouseChartController();
        Pane chartContainer = chartController.getChartPane();

        // Set the chart container size to match the parent pane
        chartContainer.setPrefSize(chartPane.getPrefWidth(), chartPane.getPrefHeight());

        // Important: Pass the transaction data to the chart controller
        if (allTransactions != null) {
            chartController.setTransactionData(allTransactions);
        }

        chartPane.getChildren().clear(); // Clear old content if any
        chartPane.getChildren().add(chartContainer);
    }

    public void updateDashboard(Map<Integer, Integer> productStockMap) {
        updatePendingOrdersCount();
        int totalStockQuantity = calculateTotalStockQuantity(productStockMap);
        BigDecimal totalWarehouseValue = calculateTotalWarehouseValue(productStockMap);
        long monthlyTransactionCount = calculateMonthlyTransactions();
        long lowStockCount = calculateLowStockCount();
        updateStatisticsUI(totalStockQuantity, totalWarehouseValue, monthlyTransactionCount, lowStockCount);
        updateLowStockProducts();
        loadTopExportedProducts();

        // Re-setup chart controller after data has been set
        setupChartController();

        // Debug output
        if (allTransactions != null) {
            System.out.println("Transaction count: " + allTransactions.size());
            // Check a few sample data points
            if (!allTransactions.isEmpty()) {
                WarehouseDTO firstTrans = allTransactions.get(0);
                System.out.println("Sample: ProductID=" + firstTrans.getProductId()
                        + ", Type=" + firstTrans.getType()
                        + ", Quantity=" + firstTrans.getQuantity()
                        + ", CreatedAt=" + firstTrans.getCreatedAt());
            }
        }
        else {
            System.out.println("Chart controller or transactions is null");
        }
        System.out.println("Transaction data size: " + (allTransactions != null ? allTransactions.size() : "null"));
    }

    public void setMainController(WarehouseController controller) {
        this.mainController = controller;
    }

    public void setProductData(ObservableList<WarehouseDTO> products) {
        this.allProducts = products;
        updateLowStockProducts();
    }

    public void setTransactionData(ObservableList<WarehouseDTO> transactions) {
        this.allTransactions = transactions;
        loadTopExportedProducts();
    }

    private void setupTableColumns() {
        setupLowStockColumns();
        setupTopExportColumns();
    }

    private void setupLowStockColumns() {
        if (colLowStockSTT != null) {
            colLowStockSTT.setCellValueFactory(cellData -> {
                int index = lowStockProducts.indexOf(cellData.getValue()) + 1;
                return new SimpleIntegerProperty(index).asObject();
            });
        }

        if (colLowStockProductId != null) {
            colLowStockProductId.setCellValueFactory(new PropertyValueFactory<>("productId"));
        }

        if (colLowStockProductName != null) {
            colLowStockProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        }

        if (colLowStockQuantity != null) {
            colLowStockQuantity.setCellValueFactory(new PropertyValueFactory<>("stock"));
        }
    }

    private void setupTopExportColumns() {
        if (colTopExportSTT != null) {
            colTopExportSTT.setCellValueFactory(cellData -> {
                int index = tblTopExportProducts.getItems().indexOf(cellData.getValue()) + 1;
                return new SimpleIntegerProperty(index).asObject();
            });
        }

        if (colTopExportProductId != null) {
            colTopExportProductId.setCellValueFactory(new PropertyValueFactory<>("productId"));
        }

        if (colTopExportProductName != null) {
            colTopExportProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        }

        if (colTopExportQuantity != null) {
            colTopExportQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        }
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

    // Trong lớp DashboardTabController
    private BigDecimal calculateTotalWarehouseValue(Map<Integer, Integer> productStockMap) {
        BigDecimal totalValue = BigDecimal.ZERO;

        if (allTransactions == null) {
            return totalValue;
        }

        // Get all products with current stock
        Map<Integer, List<WarehouseDTO>> productImportBatches = new HashMap<>();

        // First, organize import transactions by product
        for (WarehouseDTO transaction : allTransactions) {
            if (transaction.getType() == WarehouseType.NHAP_KHO) {
                int productId = transaction.getProductId();
                productImportBatches.computeIfAbsent(productId, k -> new ArrayList<>()).add(transaction);
            }
        }

        // Sort each product's import batches by date (oldest first)
        for (List<WarehouseDTO> batches : productImportBatches.values()) {
            batches.sort(Comparator.comparing(WarehouseDTO::getCreatedAt));
        }

        // Process export transactions using FIFO
        for (WarehouseDTO transaction : allTransactions) {
            if (transaction.getType() == WarehouseType.XUAT_KHO) {
                int productId = transaction.getProductId();
                int exportQuantity = transaction.getQuantity();
                List<WarehouseDTO> importBatches = productImportBatches.get(productId);

                if (importBatches != null) {
                    // Track which batches to remove or reduce
                    List<Integer> batchesToRemove = new ArrayList<>();
                    for (int i = 0; i < importBatches.size() && exportQuantity > 0; i++) {
                        WarehouseDTO batch = importBatches.get(i);
                        if (batch.getQuantity() <= exportQuantity) {
                            // Use all of this batch
                            exportQuantity -= batch.getQuantity();
                            batchesToRemove.add(i);
                        } else {
                            // Use part of this batch
                            batch.setQuantity(batch.getQuantity() - exportQuantity);
                            exportQuantity = 0;
                        }
                    }

                    // Remove consumed batches (in reverse to maintain indices)
                    for (int i = batchesToRemove.size() - 1; i >= 0; i--) {
                        importBatches.remove((int) batchesToRemove.get(i));
                    }
                }
            }
        }

        // Calculate remaining value based on remaining import batches
        for (List<WarehouseDTO> batches : productImportBatches.values()) {
            for (WarehouseDTO batch : batches) {
                BigDecimal batchValue = batch.getUnitPrice().multiply(BigDecimal.valueOf(batch.getQuantity()));
                totalValue = totalValue.add(batchValue);
            }
        }

        return totalValue;
    }

    private long calculateMonthlyTransactions() {
        LocalDate now = LocalDate.now();
        LocalDate firstDayOfMonth = LocalDate.of(now.getYear(), now.getMonth(), 1);

        if (allTransactions != null) {
            return allTransactions.stream()
                    .filter(t -> t.getCreatedAt() != null &&
                            t.getCreatedAt().toLocalDate().isAfter(firstDayOfMonth.minusDays(1)))
                    .count();
        }

        return 0;
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

    public void updateLowStockProducts() {
        if (lowStockProducts == null) {
            lowStockProducts = FXCollections.observableArrayList();
        } else {
            lowStockProducts.clear();
        }

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

        if (tblLowStockProducts != null) {
            tblLowStockProducts.setItems(lowStockProducts);
        }
    }

    public void loadTopExportedProducts() {
        // Create a map to track total export quantity by product
        Map<Integer, WarehouseDTO> productExportMap = new HashMap<>();

        // Calculate export statistics for each product if transactions are available
        if (allTransactions != null) {
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
        if (tblTopExportProducts != null) {
            tblTopExportProducts.setItems(FXCollections.observableArrayList(top20ExportProducts));
        }
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