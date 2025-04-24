package org.example.dientu99.controller.report;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.example.dientu99.helpers.LogoutHandler;

public class ReportController {
    @FXML
    private Button logoutButton;

    @FXML
    private void initialize() {
        logoutButton.setOnAction(event -> LogoutHandler.handleLogout(logoutButton));
    }
}
