module org.example.quanlybanhang {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;


    opens org.example.quanlybanhang.controller to javafx.fxml;
    opens org.example.quanlybanhang.main to javafx.fxml;
    exports org.example.quanlybanhang.main to javafx.graphics;
    exports org.example.quanlybanhang.controller;
    exports org.example.quanlybanhang.model;
    opens org.example.quanlybanhang.model to javafx.fxml;

}