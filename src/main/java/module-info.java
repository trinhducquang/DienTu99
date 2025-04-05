module org.example.quanlybanhang {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;
    requires javafx.graphics;
    requires de.mkammerer.argon2.nolibs;
    requires io.github.cdimascio.dotenv.java;
    requires com.sun.jna;


    opens org.example.quanlybanhang.main to javafx.fxml;
    exports org.example.quanlybanhang.main to javafx.graphics;
    exports org.example.quanlybanhang.model;
    opens org.example.quanlybanhang.model to javafx.fxml;
    exports org.example.quanlybanhang.controller.auth;
    opens org.example.quanlybanhang.controller.auth to javafx.fxml;
    exports org.example.quanlybanhang.controller.category;
    opens org.example.quanlybanhang.controller.category to javafx.fxml;
    exports org.example.quanlybanhang.controller.customer;
    opens org.example.quanlybanhang.controller.customer to javafx.fxml;
    exports org.example.quanlybanhang.controller.employee;
    opens org.example.quanlybanhang.controller.employee to javafx.fxml;
    exports org.example.quanlybanhang.controller.order;
    opens org.example.quanlybanhang.controller.order to javafx.fxml;
    exports org.example.quanlybanhang.controller.product;
    opens org.example.quanlybanhang.controller.product to javafx.fxml;
    exports org.example.quanlybanhang.controller.admin;
    opens org.example.quanlybanhang.controller.admin to javafx.fxml;
    exports org.example.quanlybanhang.controller.report;
    opens org.example.quanlybanhang.controller.report to javafx.fxml;

}