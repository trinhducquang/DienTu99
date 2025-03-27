package org.example.quanlybanhang.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.example.quanlybanhang.helpers.LogoutHandler;

public class ReportController {
    @FXML
    private Button logoutButton;

    @FXML
    private void initialize() {
        logoutButton.setOnAction(event -> LogoutHandler.handleLogout(logoutButton));
    }
}
