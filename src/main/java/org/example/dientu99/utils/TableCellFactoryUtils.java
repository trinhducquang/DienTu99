package org.example.dientu99.utils;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.util.Callback;

import java.math.BigDecimal;

public class TableCellFactoryUtils {

    /**
     * Cell hiển thị BigDecimal dạng tiền tệ (chỉ đọc).
     */
    public static <S> Callback<TableColumn<S, BigDecimal>, TableCell<S, BigDecimal>> currencyCellFactory() {
        return column -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal value, boolean empty) {
                super.updateItem(value, empty);
                setText((empty || value == null) ? "" : MoneyUtils.formatVN(value));
            }
        };
    }

    /**
     * Cell có thể chỉnh sửa, hiển thị và parse BigDecimal dạng tiền tệ.
     */
    public static <S> Callback<TableColumn<S, BigDecimal>, TableCell<S, BigDecimal>> editableCurrencyCellFactory() {
        return column -> new TableCell<>() {
            private final TextField textField = new TextField();

            {
                // Áp dụng định dạng tiền tệ khi gõ
                TextFieldFormatterUtils.applyBlazingFastCurrencyFormat(textField);

                // Xử lý commit khi Enter hoặc mất focus
                textField.setOnAction(event -> commitEdit(TextFieldFormatterUtils.parseCurrencyText(textField.getText())));
                textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                    if (!isNowFocused) {
                        commitEdit(TextFieldFormatterUtils.parseCurrencyText(textField.getText()));
                    }
                });
            }

            @Override
            public void startEdit() {
                super.startEdit();
                BigDecimal value = getItem();
                textField.setText(value != null ? TextFieldFormatterUtils.formatCurrency(value) : "");
                setGraphic(textField);
                setText(null);
                textField.selectAll();
                textField.requestFocus();
            }

            @Override
            public void cancelEdit() {
                super.cancelEdit();
                setText(getItem() != null ? MoneyUtils.formatVN(getItem()) : "");
                setGraphic(null);
            }

            @Override
            protected void updateItem(BigDecimal value, boolean empty) {
                super.updateItem(value, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else if (isEditing()) {
                    textField.setText(value != null ? TextFieldFormatterUtils.formatCurrency(value) : "");
                    setGraphic(textField);
                    setText(null);
                } else {
                    setText(value != null ? MoneyUtils.formatVN(value) : "");
                    setGraphic(null);
                }
            }
        };
    }
}
