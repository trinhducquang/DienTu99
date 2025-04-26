package org.example.dientu99.utils;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class MoneyUtils {

    private static final Locale LOCALE_VN = new Locale("vi", "VN");
    private static final NumberFormat FORMATTER = NumberFormat.getCurrencyInstance(LOCALE_VN);

    public static String formatVN(BigDecimal amount) {
        if (amount == null) return "";
        return FORMATTER.format(amount);
    }
    public static BigDecimal parseCurrencyText(String text) {
        if (text == null || text.trim().isEmpty()) return BigDecimal.ZERO;

        String cleaned = text.replace(".", "").trim();

        try {
            return new BigDecimal(cleaned);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }


    public static String convertToWords(BigDecimal number) {
        String[] units = {"", "một", "hai", "ba", "bốn", "năm", "sáu", "bảy", "tám", "chín"};
        String[] tensUnits = {"", "mười", "hai mươi", "ba mươi", "bốn mươi", "năm mươi", "sáu mươi", "bảy mươi", "tám mươi", "chín mươi"};
        String[] scales = {"", "nghìn", "triệu", "tỷ"};

        long amount = number.longValue();
        if (amount == 0) return "không";

        if (amount < 0) {
            return "âm " + convertToWords(number.abs());
        }

        String words = "";
        int scaleIndex = 0;

        while (amount > 0) {
            int segment = (int)(amount % 1000);
            if (segment != 0) {
                String segmentWords = convertThreeDigitSegment(segment, units, tensUnits);
                words = segmentWords + " " + scales[scaleIndex] + " " + words;
            }
            amount /= 1000;
            scaleIndex++;
        }

        return words.trim();
    }

    private static String convertThreeDigitSegment(int number, String[] units, String[] tensUnits) {
        String result = "";

        int hundreds = number / 100;
        if (hundreds > 0) {
            result += units[hundreds] + " trăm ";
        }
        int tensUnitsValue = number % 100;
        if (tensUnitsValue > 0) {
            if (tensUnitsValue < 10) {
                if (hundreds > 0) {
                    result += "lẻ ";
                }
                result += units[tensUnitsValue];
            } else if (tensUnitsValue < 20) {
                if (tensUnitsValue == 10) {
                    result += "mười";
                } else {
                    result += "mười " + units[tensUnitsValue % 10];
                }
            } else {
                int tens = tensUnitsValue / 10;
                int unitsValue = tensUnitsValue % 10;
                result += tensUnits[tens];
                if (unitsValue > 0) {
                    if (unitsValue == 5) {
                        result += " lăm";
                    } else if (unitsValue == 1) {
                        result += " mốt";
                    } else {
                        result += " " + units[unitsValue];
                    }
                }
            }
        }

        return result.trim();
    }
}
