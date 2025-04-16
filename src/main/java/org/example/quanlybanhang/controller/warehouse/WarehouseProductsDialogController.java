package org.example.quanlybanhang.controller.warehouse;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.quanlybanhang.dto.warehouseDTO.WarehouseDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.example.quanlybanhang.utils.TableCellFactoryUtils.currencyCellFactory;

public class WarehouseProductsDialogController {

    @FXML private TableView<WarehouseDTO> tableProducts;
    @FXML private TableColumn<WarehouseDTO, Integer> colId;
    @FXML private TableColumn<WarehouseDTO, Integer> colProductId;
    @FXML private TableColumn<WarehouseDTO, String> colProductName;
    @FXML private TableColumn<WarehouseDTO, Integer> colStock;
    @FXML private TableColumn<WarehouseDTO, BigDecimal> colImportPrice;
    @FXML private TableColumn<WarehouseDTO, BigDecimal> colSellPrice;
    @FXML private TableColumn<WarehouseDTO, String> colCategory;
    @FXML private TableColumn<WarehouseDTO, LocalDateTime> colUpdatedAt;

    @FXML private TextField txtSearchProduct;
    @FXML private DatePicker dpStartDateProduct;
    @FXML private DatePicker dpEndDateProduct;
    @FXML private ComboBox<String> cboCategory;
    @FXML private Button btnClose;

    private ObservableList<WarehouseDTO> products = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        setupSearch();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colProductId.setCellValueFactory(new PropertyValueFactory<>("productId"));
        colProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colImportPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colImportPrice.setCellFactory(currencyCellFactory());
        colSellPrice.setCellValueFactory(new PropertyValueFactory<>("sellPrice"));
        colSellPrice.setCellFactory(currencyCellFactory());
        colCategory.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        colUpdatedAt.setCellValueFactory(new PropertyValueFactory<>("updatedAt"));
    }

    private void setupSearch() {
        // Setup search functionality similar to the main controller
        if (txtSearchProduct != null) {
            txtSearchProduct.textProperty().addListener((obs, oldVal, newVal) -> {
                // Filter products based on search text
                filterProducts();
            });
        }

        // Add more search functionality as needed
    }

    private void filterProducts() {
        // Implement filtering logic here
    }

    public void setProductData(ObservableList<WarehouseDTO> productData) {
        this.products = FXCollections.observableArrayList(productData);
        tableProducts.setItems(products);
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }
}