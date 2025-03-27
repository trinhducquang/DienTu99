package org.example.quanlybanhang.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.example.quanlybanhang.helpers.DialogHelper;


public class CustomerController {
    @FXML
    private Button addCustomerButton;

    @FXML
    public void initialize() {
        addCustomerButton.setOnAction(event -> DialogHelper.showDialog("/org/example/quanlybanhang/CustomerDialog.fxml", "Thêm Đơn Hàng Mới"));
    }
}
