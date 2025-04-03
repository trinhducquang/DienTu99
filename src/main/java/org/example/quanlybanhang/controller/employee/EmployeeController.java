package org.example.quanlybanhang.controller.employee;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.example.quanlybanhang.helpers.LogoutHandler;

public class EmployeeController {
    @FXML
    private Button logoutButton;

    @FXML
    private void initialize() {
        logoutButton.setOnAction(event -> LogoutHandler.handleLogout(logoutButton));
    }
}
