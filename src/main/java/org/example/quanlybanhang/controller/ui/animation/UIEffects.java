package org.example.quanlybanhang.controller.ui.animation;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

public class UIEffects {

    public static void applyHoverEffect(Node node, String normalStyle, String hoverStyle) {
        node.setOnMouseEntered(e -> {
            node.setStyle(hoverStyle);
            DropShadow shadow = new DropShadow();
            shadow.setColor(Color.color(0, 0, 0, 0.3));
            shadow.setRadius(10);
            node.setEffect(shadow);
        });
        node.setOnMouseExited(e -> {
            node.setStyle(normalStyle);
            DropShadow shadow = new DropShadow();
            shadow.setColor(Color.color(0, 0, 0, 0.25));
            shadow.setRadius(5);
            node.setEffect(shadow);
        });
    }

    public static void applyFocusEffect(TextField field) {
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                field.setStyle("-fx-background-radius: 8; -fx-padding: 12px; -fx-background-color: #F5F5F5; -fx-border-color: #2196F3; -fx-border-radius: 8; -fx-border-width: 2px;");
            } else {
                field.setStyle("-fx-background-radius: 8; -fx-padding: 12px; -fx-background-color: #F5F5F5; -fx-border-color: #E0E0E0; -fx-border-radius: 8;");
            }
        });
    }
}
