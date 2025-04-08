package org.example.quanlybanhang.controller.warehouse;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.quanlybanhang.dto.WarehouseDTO;
import org.example.quanlybanhang.dao.WarehouseDAO;

import java.time.LocalDate;

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
    private TableColumn<WarehouseDTO, Double> colUnitPrice;

    @FXML
    private TableColumn<WarehouseDTO, Double> colTotalAmount;

//    @FXML
//    private TableColumn<WarehouseDTO, String> colType;

    @FXML
    private TableColumn<WarehouseDTO, String> colNote;

    @FXML
    private TableColumn<WarehouseDTO, String> colCreatedBy;

    @FXML
    private TableColumn<WarehouseDTO, LocalDate> colCreatedDate;

    @FXML
    public void initialize() {
        loadTransactions();
    }

    private void loadTransactions() {
        WarehouseDAO dao = new WarehouseDAO();
        ObservableList<WarehouseDTO> warehouseList = FXCollections.observableArrayList(dao.getAllWarehouseDetails());

        colTransId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colProductCode.setCellValueFactory(new PropertyValueFactory<>("productId"));
        colTransCode.setCellValueFactory(new PropertyValueFactory<>("transactionCode"));
        colProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colTotalAmount.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
//        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colNote.setCellValueFactory(new PropertyValueFactory<>("note"));
        colCreatedBy.setCellValueFactory(new PropertyValueFactory<>("createdByName"));
        colCreatedDate.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        tblTransactions.setItems(warehouseList);
    }
}
