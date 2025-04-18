package org.example.quanlybanhang.controller.employee;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import org.example.quanlybanhang.dao.EmployeeDAO;
import org.example.quanlybanhang.enums.UserRole;
import org.example.quanlybanhang.enums.UserStatus;
import org.example.quanlybanhang.controller.interfaces.RefreshableView;
import org.example.quanlybanhang.model.Employee;
import org.example.quanlybanhang.service.EmployeeService;
import org.example.quanlybanhang.utils.AlertUtils;
import org.example.quanlybanhang.utils.DatabaseConnection;

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

    private RefreshableView parentController;

    public void setParentController(RefreshableView controller) {
        this.parentController = controller;
    }

    @FXML
    public void initialize() {
        setPhoneFieldFormatter();
        saveButton.setOnAction(event -> saveEmployee());
        cancelButton.setOnAction(event -> closeDialog());
    }

    private void setPhoneFieldFormatter() {
        phoneField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            return newText.matches("\\+?\\d*") ? change : null;
        }));
    }

    private void saveEmployee() {
        String name = nameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String retypePassword = retypepasswordField.getText();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        if (!isInputValid(name, username, password, retypePassword, email, phone)) {
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            EmployeeDAO employeeDAO = new EmployeeDAO(connection);

            if (employeeDAO.isFullNameOrUsernameExists(name, username)) {
                AlertUtils.showError("Lỗi nhập liệu", "Tên đầy đủ hoặc tên tài khoản đã tồn tại. Vui lòng chọn tên khác.");
                return;
            }

            EmployeeService employeeService = new EmployeeService(connection);
            Employee employee = new Employee(
                    0, name, username, password, email, phone,
                    UserRole.NHAN_VIEN, UserStatus.UNLOCK
            );

            boolean success = employeeService.addEmployee(employee, retypePassword);
            if (success) {
                AlertUtils.showInfo("Thành công", "Thêm nhân viên thành công!");

                // Call refresh on parent controller before closing
                if (parentController != null) {
                    parentController.refresh();
                }

                closeDialog();
            } else {
                AlertUtils.showError("Thất bại", "Không thể thêm nhân viên. Vui lòng thử lại.");
            }

        } catch (Exception e) {
            AlertUtils.showError("Lỗi hệ thống", "Không thể kết nối cơ sở dữ liệu.");
        }
    }

    private boolean isInputValid(String name, String username, String password,
                                 String retypePassword, String email, String phone) {

        if (username.contains(" ")) {
            AlertUtils.showError("Lỗi nhập liệu", "Tên tài khoản không được chứa khoảng trắng.");
            return false;
        }

        if (!password.equals(retypePassword)) {
            AlertUtils.showError("Lỗi nhập liệu", "Mật khẩu và nhập lại mật khẩu không khớp.");
            return false;
        }

        if (password.length() > 7) {
            AlertUtils.showError("Lỗi nhập liệu", "Mật khẩu không được dài quá 7 ký tự.");
            return false;
        }

        if (!email.contains("@")) {
            AlertUtils.showError("Lỗi nhập liệu", "Email không hợp lệ. Phải chứa ký tự '@'.");
            return false;
        }

        return isPhoneValid(phone);
    }

    private boolean isPhoneValid(String phone) {
        String phoneDigits = phone.startsWith("+") ? phone.substring(1) : phone;

        if (!phoneDigits.matches("\\d+")) {
            AlertUtils.showError("Lỗi nhập liệu", "Số điện thoại chỉ được chứa chữ số (và dấu '+' ở đầu nếu có).");
            return false;
        }

        if (phoneDigits.length() < 8 || phoneDigits.length() > 11) {
            AlertUtils.showError("Lỗi nhập liệu", "Số điện thoại phải từ 8 đến 11 chữ số.");
            return false;
        }

        return true;
    }

    private void closeDialog() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}