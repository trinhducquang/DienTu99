package org.example.quanlybanhang.controller.employee;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.quanlybanhang.dao.EmployeeDAO;
import org.example.quanlybanhang.utils.DatabaseConnection;
import org.example.quanlybanhang.model.Employee;

import java.sql.Connection;

public class EmployeeManagementDialogController {
    @FXML
    private TextField nameField;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;
    @FXML
    private TextField retypepasswordField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    @FXML
    public void initialize() {
        saveButton.setOnAction(event -> saveEmployee());
        cancelButton.setOnAction(event -> closeDialog());
    }

    private void saveEmployee() {
        String password = passwordField.getText();
        String retypePassword = retypepasswordField.getText();

        if (!password.equals(retypePassword)) {
            System.out.println("❌ Mật khẩu không khớp!");
            return;
        }

        Connection connection = DatabaseConnection.getConnection();
        if (connection != null) {
            EmployeeDAO employeeDAO = new EmployeeDAO(connection);
            Employee employee = new Employee(
                    0,
                    nameField.getText(),
                    usernameField.getText(),
                    password,
                    emailField.getText(),
                    phoneField.getText(),
                    "nhanvien"
            );
            boolean success = employeeDAO.addEmployee(employee, password);
            System.out.println(success ? "✅ Thêm nhân viên thành công!" : "❌ Thêm nhân viên thất bại!");
        }

        closeDialog();
    }


    private void closeDialog() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
