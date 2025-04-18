package org.example.quanlybanhang.controller.warehouse;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.quanlybanhang.dto.warehouseDTO.WarehouseDTO;
import org.example.quanlybanhang.enums.WarehouseType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static org.example.quanlybanhang.utils.TableCellFactoryUtils.currencyCellFactory;

public class WarehouseProductsDialogController {
    @FXML private TableView<WarehouseDTO> tableProducts;
    @FXML private TableColumn<WarehouseDTO, Integer> colId;
    @FXML private TableColumn<WarehouseDTO, Integer> colProductId;
    @FXML private TableColumn<WarehouseDTO, String> colProductName;
    @FXML private TableColumn<WarehouseDTO, Integer> colStock;
    @FXML private TableColumn<WarehouseDTO, Integer> colStock1;
    @FXML private TableColumn<WarehouseDTO, BigDecimal> colImportPrice;
    @FXML private TableColumn<WarehouseDTO, BigDecimal> colSellPrice;
    @FXML private TableColumn<WarehouseDTO, String> colCategory;
    @FXML private TableColumn<WarehouseDTO, LocalDateTime> colUpdatedAt;
    @FXML private TableColumn<WarehouseDTO, BigDecimal> colTotalAmount;
    @FXML private ComboBox<String> cboCategory;
    @FXML private Button btnClose;

    private ObservableList<WarehouseDTO> products = FXCollections.observableArrayList();
    private ObservableList<WarehouseDTO> allTransactions = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        setupCategoryComboBox();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colProductId.setCellValueFactory(new PropertyValueFactory<>("productId"));
        colProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colStock1.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colImportPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colImportPrice.setCellFactory(currencyCellFactory());
        colSellPrice.setCellValueFactory(new PropertyValueFactory<>("sellPrice"));
        colSellPrice.setCellFactory(currencyCellFactory());
        colCategory.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        colUpdatedAt.setCellValueFactory(new PropertyValueFactory<>("updatedAt"));
        colTotalAmount.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        colTotalAmount.setCellFactory(currencyCellFactory());


    }

    private void setupCategoryComboBox() {
        cboCategory.getItems().clear();
        cboCategory.getItems().addAll(
                WarehouseType.NHAP_KHO.getValue(),
                WarehouseType.XUAT_KHO.getValue()
        );
        cboCategory.setPromptText("Chọn loại kho");

        cboCategory.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updateColumnVisibility(newVal);
                updateProductQuantitiesFromTransactions();
            }
        });

        cboCategory.getSelectionModel().selectFirst();

    }

    public void setProductData(ObservableList<WarehouseDTO> productData, ObservableList<WarehouseDTO> transactionData) {
        this.products = FXCollections.observableArrayList(productData);
        this.allTransactions = FXCollections.observableArrayList(transactionData);
        updateProductQuantitiesFromTransactions();
        tableProducts.setItems(this.products);
    }


    private void updateColumnVisibility(String warehouseTypeValue) {
        boolean isImport = warehouseTypeValue.equals(WarehouseType.NHAP_KHO.getValue());

        // Common columns
        colId.setVisible(true);
        colProductId.setVisible(true);
        colProductName.setVisible(true);
        colCategory.setVisible(true);
        colStock1.setVisible(true);
        colUpdatedAt.setVisible(false);

        // Conditional columns
        colStock1.setText(isImport ? "Số lượng nhập" : "Số lượng xuất");
        colStock.setVisible(!isImport);
        colSellPrice.setVisible(!isImport);
        colImportPrice.setVisible(isImport);
        colTotalAmount.setVisible(isImport);
    }

    private void updateProductQuantitiesFromTransactions() {
        String selectedType = cboCategory.getSelectionModel().getSelectedItem();
        if (selectedType == null) return;

        WarehouseType warehouseType = selectedType.equals(WarehouseType.NHAP_KHO.getValue()) ?
                WarehouseType.NHAP_KHO : WarehouseType.XUAT_KHO;

        // Calculate stock levels for all products
        Map<Integer, Integer> stockMap = calculateStockLevels();

        // Filter transactions by selected type
        List<WarehouseDTO> filteredTransactions = allTransactions.stream()
                .filter(transaction -> transaction.getType() == warehouseType)
                .toList();

        products.clear();

        if (warehouseType == WarehouseType.XUAT_KHO) {
            // Group by product ID for export transactions
            Map<Integer, WarehouseDTO> productMap = new HashMap<>();

            for (WarehouseDTO transaction : filteredTransactions) {
                int productId = transaction.getProductId();

                if (productMap.containsKey(productId)) {
                    // Update existing product entry
                    WarehouseDTO existingProduct = productMap.get(productId);
                    existingProduct.setQuantity(existingProduct.getQuantity() + transaction.getQuantity());

                    // Keep most recent transaction date
                    if (transaction.getCreatedAt().isAfter(existingProduct.getCreatedAt())) {
                        existingProduct.setCreatedAt(transaction.getCreatedAt());
                    }
                } else {
                    // Create new product entry
                    WarehouseDTO productEntry = createProductEntry(transaction);
                    productEntry.setStock(stockMap.getOrDefault(productId, 0));
                    productMap.put(productId, productEntry);
                }
            }

            products.addAll(productMap.values());
        } else {
            // For imports, show all transactions individually
            for (WarehouseDTO transaction : filteredTransactions) {
                WarehouseDTO productEntry = createProductEntry(transaction);
                productEntry.setStock(stockMap.getOrDefault(productEntry.getProductId(), 0));
                products.add(productEntry);
            }
        }
    }

    private WarehouseDTO createProductEntry(WarehouseDTO transaction) {
        WarehouseDTO entry = new WarehouseDTO();
        entry.setId(transaction.getId());
        entry.setProductId(transaction.getProductId());
        entry.setProductName(transaction.getProductName());
        entry.setQuantity(transaction.getQuantity());
        entry.setUnitPrice(transaction.getUnitPrice());
        entry.setSellPrice(transaction.getSellPrice());
        entry.setCategoryName(transaction.getCategoryName());
        entry.setCreatedAt(transaction.getCreatedAt());
        return entry;
    }

    private Map<Integer, Integer> calculateStockLevels() {
        Map<Integer, Integer> stockMap = new HashMap<>();

        for (WarehouseDTO transaction : allTransactions) {
            int productId = transaction.getProductId();
            int currentStock = stockMap.getOrDefault(productId, 0);

            if (transaction.getType() == WarehouseType.NHAP_KHO) {
                currentStock += transaction.getQuantity();
            } else if (transaction.getType() == WarehouseType.XUAT_KHO) {
                currentStock -= transaction.getQuantity();
            }

            stockMap.put(productId, currentStock);
        }

        return stockMap;
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }
}