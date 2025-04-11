package org.example.quanlybanhang.test;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

public class NhapKhoDialogController {

    @FXML
    private ComboBox<String> loaiGiaoDichComboBox;

    private String loaiGiaoDich;

    public void setLoaiGiaoDich(String loaiGiaoDich) {
        this.loaiGiaoDich = loaiGiaoDich;
        System.out.println("Loại giao dịch được truyền vào: " + loaiGiaoDich);

        // Nếu ComboBox đã khởi tạo thì gán luôn giá trị được truyền
        if (loaiGiaoDichComboBox != null) {
            loaiGiaoDichComboBox.setValue(loaiGiaoDich);
        }
    }

    @FXML
    public void initialize() {
        // Có thể setup sự kiện ở đây nếu cần
        // loaiGiaoDichComboBox.setOnAction(...) nếu bạn muốn
    }
}
