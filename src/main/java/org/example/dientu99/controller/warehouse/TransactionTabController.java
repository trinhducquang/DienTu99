package org.example.dientu99.controller.warehouse;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.dientu99.dao.WarehouseDAO;
import org.example.dientu99.dto.warehouseDTO.WarehouseDTO;
import org.example.dientu99.enums.WarehouseType;
import org.example.dientu99.helpers.DialogHelper;
import org.example.dientu99.utils.SearchUtils;
import org.example.dientu99.utils.PaginationUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.example.dientu99.utils.TableCellFactoryUtils.currencyCellFactory;

public class TransactionTabController {
    private static final int ITEMS_PER_PAGE = 18;
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
    @FXML
    ComboBox<WarehouseType> cboTransactionType;
    @FXML
    Pagination pagination;
    private ObservableList<WarehouseDTO> allTransactions;
    private ObservableList<WarehouseDTO> displayedTransactions;
    private final IntegerProperty currentTransactionPage = new SimpleIntegerProperty(0);
    private final WarehouseDAO warehouseDAO = new WarehouseDAO();
    private WarehouseController mainController;

    public void initialize() {
        initializeCollections();
        setupTransactionColumns();
        setupTransactionSearch();
        setupButtons();
        setupTransactionTypeComboBox();
        loadTransactionData();
    }

    private void setupTransactionTypeComboBox() {
        cboTransactionType.getItems().clear();
        cboTransactionType.getItems().add(null);
        cboTransactionType.getItems().addAll(WarehouseType.NHAP_KHO, WarehouseType.XUAT_KHO);
        cboTransactionType.setCellFactory(cell -> new ListCell<WarehouseType>() {
            @Override
            protected void updateItem(WarehouseType item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText("");
                } else if (item == null) {
                    setText("Tất cả");
                } else {
                    setText(item.getValue());
                }
            }
        });

        // Same for button cell
        cboTransactionType.setButtonCell(new ListCell<WarehouseType>() {
            @Override
            protected void updateItem(WarehouseType item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText("");
                } else if (item == null) {
                    setText("Tất cả");
                } else {
                    setText(item.getValue());
                }
            }
        });
        cboTransactionType.setValue(null);
        cboTransactionType.valueProperty().addListener((obs, oldVal, newVal) -> {
            applyTransactionFilters();
            updateColumnVisibility(newVal);
            if (mainController != null) {
                mainController.refreshData();
            }
        });
    }

    private void updateColumnVisibility(WarehouseType type) {
        if (type == WarehouseType.NHAP_KHO) {
            colUnitPrice.setVisible(true);
            colTotalAmount.setVisible(true);
        } else if (type == WarehouseType.XUAT_KHO) {
            colUnitPrice.setVisible(false);
            colTotalAmount.setVisible(true);
        } else {
            colUnitPrice.setVisible(true);
            colTotalAmount.setVisible(true);
        }

        // Cập nhật lại giao diện của bảng
        tblTransactions.refresh();
    }

    void applyTransactionFilters() {
        String searchText = txtSearchTransaction.getText();
        LocalDate startDate = dpStartDateTransaction.getValue();
        LocalDate endDate = dpEndDateTransaction.getValue();
        WarehouseType selectedType = cboTransactionType.getValue();
        List<WarehouseDTO> allData = warehouseDAO.getAllWarehouseDetails();
        if (selectedType != null) {
            allData = allData.stream()
                    .filter(dto -> dto.getType() == selectedType)
                    .collect(Collectors.toList());
        }
        List<WarehouseDTO> filteredData = SearchUtils.search(
                allData,
                searchText,
                startDate,
                endDate,
                dto -> dto.getCreatedAt() != null ? dto.getCreatedAt().toLocalDate() : null,
                WarehouseDTO::getProductName,
                WarehouseDTO::getTransactionCode,
                WarehouseDTO::getCategoryName
        );

        // Update displayed list
        allTransactions.setAll(filteredData);
        pagination.setCurrentPageIndex(0);

        // Update pagination
        setupPagination();
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

            // Notify main controller when transaction data is loaded
            if (mainController != null) {
                mainController.refreshData();
            }
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

    @FXML
    private void openCreateTransactionDialog() {
        try {
            DialogHelper.showDialog(
                    "/org/example/dientu99/views/warehouse/WarehouseImport.fxml",
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