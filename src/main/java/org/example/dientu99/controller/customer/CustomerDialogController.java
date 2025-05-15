package org.example.dientu99.controller.customer;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.dientu99.model.Customer;
import org.example.dientu99.service.CustomerService;
import org.example.dientu99.utils.AlertUtils;

public class CustomerDialogController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField addressField;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private final CustomerService customerService = new CustomerService();

    @FXML
    public void initialize() {
        saveButton.setOnAction(event -> saveCustomer());
        cancelButton.setOnAction(event -> closeDialog());
        phoneField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                phoneField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    private void saveCustomer() {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String address = addressField.getText().trim();

        if (name.isEmpty()) {
            AlertUtils.showWarning("Thông tin không hợp lệ", "Vui lòng nhập tên khách hàng!");
            nameField.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            AlertUtils.showWarning("Thông tin không hợp lệ", "Vui lòng nhập số điện thoại khách hàng!");
            phoneField.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            AlertUtils.showWarning("Thông tin không hợp lệ", "Vui lòng nhập email khách hàng!");
            emailField.requestFocus();
            return;
        }

        if (address.isEmpty()) {
            AlertUtils.showWarning("Thông tin không hợp lệ", "Vui lòng nhập địa chỉ khách hàng!");
            addressField.requestFocus();
            return;
        }

        Customer newCustomer = new Customer(0, name, phone, email, address);
        boolean success = customerService.addCustomer(newCustomer);

        if (success) {
            AlertUtils.showInfo("Thành công", "Thêm khách hàng thành công!");
            closeDialog();
        } else {
            AlertUtils.showError("Lỗi", "Không thể thêm khách hàng. Vui lòng thử lại sau!");
        }
    }

    private void closeDialog() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
}