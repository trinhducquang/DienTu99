package org.example.quanlybanhang.utils;

import java.text.NumberFormat;
import java.util.Locale;

public class MoneyUtils {

    public static String formatVN(double amount) {
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(localeVN);
        return currencyFormatter.format(amount);
    }
}
