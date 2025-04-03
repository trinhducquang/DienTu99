package org.example.quanlybanhang.helpers;

import javafx.scene.control.Button;
import javafx.scene.control.TableCell;

import java.util.function.Consumer;

public class ButtonTableCell<T> extends TableCell<T, Void> {
    private final Button button;

    public ButtonTableCell(String buttonText, Consumer<T> onClickAction) {
        this.button = new Button(buttonText);

        button.setOnAction(event -> {
            T item = getTableRow().getItem();
            if (item != null) {
                onClickAction.accept(item);
            }
        });
    }

    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || getTableRow().getItem() == null) {
            setGraphic(null);
        } else {
            setGraphic(button);
        }
    }
}
