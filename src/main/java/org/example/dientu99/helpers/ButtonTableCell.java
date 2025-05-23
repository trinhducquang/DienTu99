package org.example.dientu99.helpers;

import javafx.scene.control.Button;
import javafx.scene.control.TableCell;

import java.util.function.Consumer;

public class ButtonTableCell<T> extends TableCell<T, Void> {
    private final Button button;

    public ButtonTableCell(String buttonText, Consumer<T> onClickAction) {
        this.button = new Button(buttonText);

        button.setOnAction(event -> {
            T item = getTableRow() != null ? getTableRow().getItem() : null;
            if (item != null) {
                onClickAction.accept(item);
            }
        });
    }

    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);

        boolean noRowItem = getTableRow() == null || getTableRow().getItem() == null;

        if (empty || noRowItem) {
            setGraphic(null);
        } else {
            setGraphic(button);
        }
    }
}
