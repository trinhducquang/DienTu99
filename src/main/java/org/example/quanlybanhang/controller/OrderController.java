package org.example.quanlybanhang.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.example.quanlybanhang.helpers.DialogHelper;

public class OrderController {
    @FXML
    private Button addOrderButton;

    @FXML
    public void initialize() {
        addOrderButton.setOnAction(event -> DialogHelper.showDialog("/org/example/quanlybanhang/AddOrderDialog.fxml", "Thêm Đơn Hàng Mới"));
    }
}
