package org.example.quanlybanhang.controller.warehouse;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.quanlybanhang.dao.WarehouseDAO;
import org.example.quanlybanhang.dto.warehouseDTO.WarehouseDTO;
import org.example.quanlybanhang.helpers.DialogHelper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.example.quanlybanhang.utils.TableCellFactoryUtils.*;


public class WarehouseController {


    // Button
    @FXML
    private Button btnCreateTransaction;

    // Tìm kiếm
    @FXML
    private TextField txtSearchTransaction;

    // Chọn ngày
    @FXML
    private DatePicker dpStartDateTransaction;

    @FXML
    private DatePicker dpEndDateTransaction;

    // Bảng giao dịch trong kho
    @FXML
    private TableView<WarehouseDTO> tblTransactions;

    @FXML
    private TableColumn<WarehouseDTO, Integer> colTransId;

    @FXML
    private TableColumn<WarehouseDTO, Integer> colProductCode;

    @FXML
    private TableColumn<WarehouseDTO, String> colTransCode;

    @FXML
    private TableColumn<WarehouseDTO, String> colProductName;

    @FXML
    private TableColumn<WarehouseDTO, String> colCategory;

    @FXML
    private TableColumn<WarehouseDTO, Integer> colQuantity;

    @FXML
    private TableColumn<WarehouseDTO, BigDecimal> colUnitPrice;

    @FXML
    private TableColumn<WarehouseDTO, BigDecimal> colTotalAmount;

//    @FXML
//    private TableColumn<WarehouseDTO, String> colType;

    @FXML
    private TableColumn<WarehouseDTO, String> colNote;

    @FXML
    private TableColumn<WarehouseDTO, String> colCreatedBy;

    @FXML
    private TableColumn<WarehouseDTO, LocalDate> colCreatedDate;

    @FXML
    private TableView<WarehouseDTO> tableWarehouseProducts;

    @FXML
    private TableColumn<WarehouseDTO, Integer> colId;
    @FXML
    private TableColumn<WarehouseDTO, Integer> colProductId;
    @FXML
    private TableColumn<WarehouseDTO, String> colSku;
    @FXML
    private TableColumn<WarehouseDTO, String> colnameProduct;
    @FXML
    private TableColumn<WarehouseDTO, Integer> colStock;
    @FXML
    private TableColumn<WarehouseDTO, BigDecimal> colImportPrice;
    @FXML
    private TableColumn<WarehouseDTO, BigDecimal> colSellPrice;
    @FXML
    private TableColumn<WarehouseDTO, String> colNamecategory;
    @FXML
    private TableColumn<WarehouseDTO, LocalDateTime> colUpdatedAt;

    //
    @FXML
    private TableView<WarehouseDTO> tableWarehouseCheck;
    @FXML
    private TableColumn<WarehouseDTO, String> colIdCheck;

    //
    @FXML
    private TableColumn<WarehouseDTO, LocalDateTime> colCheckdate;
    @FXML
    private TableColumn<WarehouseDTO, String> checker;
    @FXML
    private TableColumn<WarehouseDTO, String> colcheckProduct;
    @FXML
    private TableColumn<WarehouseDTO, Integer> colProductNumber;
    @FXML
    private TableColumn<WarehouseDTO, Integer> colExcessProduct;
    @FXML
    private TableColumn<WarehouseDTO, Integer> colmissingProduct;
    @FXML
    private TableColumn<WarehouseDTO, Integer> colDefectiveProduct;
    @FXML
    private TableColumn<WarehouseDTO, Enum<?>> colCheckStatus; // hoặc cụ thể hơn là <WarehouseDTO, InventoryStatus>
    @FXML
    private TableColumn<WarehouseDTO, String> colcheckNote;


    @FXML
    public void initialize() {
        loadTransactions();
        btnCreateTransaction.setOnAction(event -> openCreateTransactionDialog());
    }

    private void openCreateTransactionDialog() {
        DialogHelper.showDialog("/org/example/quanlybanhang/views/warehouse/WarehouseImport.fxml", "Tạo phiếu kiểm kho", (Stage) tblTransactions.getScene().getWindow());
    }

    private void loadTransactions() {
        WarehouseDAO dao = new WarehouseDAO();
        ObservableList<WarehouseDTO> warehouseList = FXCollections.observableArrayList(dao.getAllWarehouseDetails());
        ObservableList<WarehouseDTO> warehouseProductList = FXCollections.observableArrayList(dao.getAllWarehouseProducts());
        ObservableList<WarehouseDTO> warehouseCheckList = FXCollections.observableArrayList(dao.getAllWarehouseCheck());

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
//        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colNote.setCellValueFactory(new PropertyValueFactory<>("note"));
        colCreatedBy.setCellValueFactory(new PropertyValueFactory<>("createdByName"));
        colCreatedDate.setCellValueFactory(new PropertyValueFactory<>("createdAt"));


        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colProductId.setCellValueFactory(new PropertyValueFactory<>("productId"));
        colSku.setCellValueFactory(new PropertyValueFactory<>("sku"));
        colnameProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colImportPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colImportPrice.setCellFactory(currencyCellFactory());
        colSellPrice.setCellValueFactory(new PropertyValueFactory<>("sellPrice"));
        colSellPrice.setCellFactory(currencyCellFactory());


        colNamecategory.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        colUpdatedAt.setCellValueFactory(new PropertyValueFactory<>("updatedAt"));

//
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


        tableWarehouseCheck.setItems(warehouseCheckList);
        tblTransactions.setItems(warehouseList);
        tableWarehouseProducts.setItems(warehouseProductList);
    }
}
