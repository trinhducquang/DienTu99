module org.example.dientu99 {
    requires javafx.controls;
    requires javafx.fxml;
    requires mysql.connector.j;
    requires javafx.graphics;
    requires de.mkammerer.argon2.nolibs;
    requires io.github.cdimascio.dotenv.java;
    requires com.sun.jna;
    requires com.google.gson;
    requires java.sql;
    requires java.desktop;
    requires itextpdf;
    requires java.prefs;


    exports org.example.dientu99.enums;
    opens org.example.dientu99.main to javafx.fxml;
    exports org.example.dientu99.main to javafx.graphics;
    exports org.example.dientu99.model;
    opens org.example.dientu99.controller.warehouse to javafx.fxml;
    opens org.example.dientu99.model to javafx.fxml;
    exports org.example.dientu99.controller.login;
    opens org.example.dientu99.controller.login to javafx.fxml;
    exports org.example.dientu99.controller.category;
    opens org.example.dientu99.controller.category to javafx.fxml;
    exports org.example.dientu99.controller.customer;
    opens org.example.dientu99.controller.customer to javafx.fxml;
    exports org.example.dientu99.controller.employee;
    opens org.example.dientu99.controller.employee to javafx.fxml;
    exports org.example.dientu99.controller.order;
    opens org.example.dientu99.controller.order to javafx.fxml;
    exports org.example.dientu99.controller.product;
    opens org.example.dientu99.controller.product to javafx.fxml;
    exports org.example.dientu99.controller.admin;
    opens org.example.dientu99.controller.admin to javafx.fxml;
    exports org.example.dientu99.controller.report;
    opens org.example.dientu99.controller.report to javafx.fxml;
    opens org.example.dientu99.dto.orderDTO to com.google.gson, javafx.base;
    opens org.example.dientu99.dto.warehouseDTO to com.google.gson, javafx.base;
    exports org.example.dientu99.controller.sale;  // Thêm dòng này
    opens org.example.dientu99.controller.sale to javafx.fxml;
    exports org.example.dientu99.dto.productDTO;
    opens org.example.dientu99.dto.productDTO to com.google.gson, javafx.base, javafx.fxml;


}