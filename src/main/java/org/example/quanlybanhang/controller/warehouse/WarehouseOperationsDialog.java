package org.example.quanlybanhang.controller.warehouse;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.quanlybanhang.model.User;
import org.example.quanlybanhang.service.UserService;
import org.example.quanlybanhang.service.WarehouseService;

import java.time.LocalDate;
import java.util.List;

public class WarehouseOperationsDialog {

    private final WarehouseService warehouseService = new WarehouseService();
    private final UserService userService = new UserService();

    // --- Thông Tin Giao Dịch ---
    @FXML
    private TextField transactionCodeField;

    @FXML
    private DatePicker createdAtDatePicker;

    @FXML
    private ComboBox<User> createdByComboBox;

    @FXML
    private ComboBox<String> transactionTypeComboBox;

    // --- Chi Tiết Sản Phẩm ---
    @FXML
    private ComboBox<String> productComboBox;

    @FXML
    private TextField quantityField;

    @FXML
    private TextField unitPriceField;

    @FXML
    private TableView<?> productTableView;

    @FXML
    private TableColumn<?, Integer> indexColumn;

    @FXML
    private TableColumn<?, String> productIdColumn;

    @FXML
    private TableColumn<?, String> productNameColumn;

    @FXML
    private TableColumn<?, Integer> quantityColumn;

    @FXML
    private TableColumn<?, Double> unitPriceColumn;

    @FXML
    private TableColumn<?, Double> totalColumn;

    @FXML
    private TableColumn<?, Void> actionColumn;

    // --- Thông Tin Bổ Sung ---
    @FXML
    private TextField totalAmountField;

    @FXML
    private TextField excessQuantityField;

    @FXML
    private TextField deficientQuantityField;

    @FXML
    private TextArea noteTextArea;

    @FXML
    private void initialize() {
        // Gọi từ service thay vì viết hàm thủ công
        String generatedCode = warehouseService.generateTransactionCode();
        transactionCodeField.setText(generatedCode);

        createdAtDatePicker.setValue(LocalDate.now());

        List<User> staffNames = userService.getWarehouseStaffNames();
        createdByComboBox.getItems().addAll(staffNames);
    }
}
