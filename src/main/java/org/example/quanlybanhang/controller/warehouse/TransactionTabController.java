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
import org.example.quanlybanhang.helpers.DialogHelper;
import org.example.quanlybanhang.service.SearchService;
import org.example.quanlybanhang.utils.PaginationUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.example.quanlybanhang.utils.TableCellFactoryUtils.currencyCellFactory;

public class TransactionTabController {
    // Constants
    private static final int ITEMS_PER_PAGE = 18;

    // Transaction tab components
    @FXML
    Button btnCreateTransaction;
    @FXML
    TextField txtSearchTransaction;
    @FXML
    DatePicker dpStartDateTransaction;
    @FXML
    DatePicker dpEndDateTransaction;
    @FXML
    TableView<WarehouseDTO> tblTransactions;
    @FXML
    TableColumn<WarehouseDTO, Integer> colTransId;
    @FXML
    TableColumn<WarehouseDTO, Integer> colProductCode;
    @FXML
    TableColumn<WarehouseDTO, String> colTransCode;
    @FXML
    TableColumn<WarehouseDTO, String> colProductName;
    @FXML
    TableColumn<WarehouseDTO, String> colCategory;
    @FXML
    TableColumn<WarehouseDTO, Integer> colQuantity;
    @FXML
    TableColumn<WarehouseDTO, BigDecimal> colUnitPrice;
    @FXML
    TableColumn<WarehouseDTO, BigDecimal> colTotalAmount;
    @FXML
    TableColumn<WarehouseDTO, String> colNote;
    @FXML
    TableColumn<WarehouseDTO, String> colCreatedBy;
    @FXML
    TableColumn<WarehouseDTO, LocalDate> colCreatedDate;

    // Pagination
    @FXML
    Pagination pagination;

    // Data collections
    private ObservableList<WarehouseDTO> allTransactions;
    private ObservableList<WarehouseDTO> displayedTransactions;

    // Pagination state
    private final IntegerProperty currentTransactionPage = new SimpleIntegerProperty(0);

    // Data access
    private final WarehouseDAO warehouseDAO = new WarehouseDAO();

    // Reference to main controller
    private WarehouseController mainController;

    public void initialize() {
        initializeCollections();
        setupTransactionColumns();
        setupTransactionSearch();
        setupButtons();
        loadTransactionData();
    }

    public void setMainController(WarehouseController controller) {
        this.mainController = controller;
    }

    private void initializeCollections() {
        allTransactions = FXCollections.observableArrayList();
        displayedTransactions = FXCollections.observableArrayList();
    }

    private void setupButtons() {
        btnCreateTransaction.setOnAction(event -> openCreateTransactionDialog());
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

        tblTransactions.setItems(displayedTransactions);
    }

    private void setupTransactionSearch() {
        txtSearchTransaction.textProperty().addListener((obs, oldVal, newVal) -> applyTransactionFilters());
        dpStartDateTransaction.valueProperty().addListener((obs, oldVal, newVal) -> applyTransactionFilters());
        dpEndDateTransaction.valueProperty().addListener((obs, oldVal, newVal) -> applyTransactionFilters());
    }

    public void loadTransactionData() {
        try {
            allTransactions.setAll(warehouseDAO.getAllWarehouseDetails());
            setupPagination();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setupPagination() {
        PaginationUtils.setup(
                pagination,
                allTransactions,
                displayedTransactions,
                currentTransactionPage,
                ITEMS_PER_PAGE,
                null
        );
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

    @FXML
    private void openCreateTransactionDialog() {
        try {
            DialogHelper.showDialog(
                    "/org/example/quanlybanhang/views/warehouse/WarehouseImport.fxml",
                    "Tạo phiếu kho",
                    (Stage) tblTransactions.getScene().getWindow()
            );

            // Sau khi dialog đóng, refresh lại dữ liệu
            loadTransactionData();

            // Thông báo cho main controller cần refresh dữ liệu
            if (mainController != null) {
                mainController.refreshData();
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi mở dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public ObservableList<WarehouseDTO> getAllTransactions() {
        return allTransactions;
    }
}