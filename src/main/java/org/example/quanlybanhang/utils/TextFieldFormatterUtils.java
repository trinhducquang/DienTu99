package org.example.quanlybanhang.utils;

import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class TextFieldFormatterUtils {

    private static final DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
    private static final DecimalFormat formatter = new DecimalFormat("#,###", symbols);

    static {
        symbols.setGroupingSeparator('.');
    }

    public static void applyBlazingFastCurrencyFormat(TextField textField) {
        textField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            String character = event.getCharacter();
            if (!character.matches("[0-9]")) {
                event.consume();
                return;
            }

            // Lấy raw numeric string từ text hiện tại
            String currentText = textField.getText().replaceAll("[^\\d]", "");
            String newText = currentText + character;

            try {
                long value = Long.parseLong(newText);
                String formatted = formatter.format(value);

                textField.setText(formatted);
                textField.positionCaret(formatted.length());
                event.consume(); // ngăn thêm ký tự thô
            } catch (NumberFormatException e) {
                event.consume(); // tránh crash nếu số quá lớn
            }
        });

        // Xử lý phím Backspace
        textField.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode().toString().equals("BACK_SPACE")) {
                String raw = textField.getText().replaceAll("[^\\d]", "");
                if (!raw.isEmpty()) {
                    String trimmed = raw.substring(0, raw.length() - 1);
                    if (!trimmed.isEmpty()) {
                        long value = Long.parseLong(trimmed);
                        String formatted = formatter.format(value);
                        textField.setText(formatted);
                        textField.positionCaret(formatted.length());
                    } else {
                        textField.clear();
                    }
                    event.consume();
                }
            }
        });
    }

    public static BigDecimal parseCurrencyText(String formattedText) {
        String raw = formattedText.replaceAll("[^\\d]", "");
        if (raw.isEmpty()) return BigDecimal.ZERO;
        return new BigDecimal(raw);
    }

    public static String formatCurrency(BigDecimal value) {
        return formatter.format(value.longValue());
    }


}
