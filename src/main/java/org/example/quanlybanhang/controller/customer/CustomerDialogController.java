package org.example.quanlybanhang.controller.customer;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.quanlybanhang.model.Customer;
import org.example.quanlybanhang.service.CustomerService;

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
    }

    private void saveCustomer() {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String address = addressField.getText().trim();

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || address.isEmpty()) {
            System.out.println("Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        Customer newCustomer = new Customer(0, name, phone, email, address);
        boolean success = customerService.addCustomer(newCustomer);

        if (success) {
            System.out.println("Thêm khách hàng thành công!");
            closeDialog();
        } else {
            System.out.println("Lỗi khi thêm khách hàng!");
        }
    }

    private void closeDialog() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
}
