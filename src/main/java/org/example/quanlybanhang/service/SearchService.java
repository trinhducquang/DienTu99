package org.example.quanlybanhang.service;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SearchService {

    /**
     * ✅ Hàm search cũ – Tìm theo từ khóa, không cần lọc theo ngày
     */
    @SafeVarargs
    public static <T> List<T> search(List<T> list, String keyword, Function<T, String>... fieldExtractors) {
        return search(list, keyword, null, null, null, fieldExtractors);
    }

    /**
     * ✅ Hàm search mới – Hỗ trợ tìm theo từ khóa và khoảng thời gian (nếu có)
     */
    @SafeVarargs
    public static <T> List<T> search(
            List<T> list,
            String keyword,
            LocalDate fromDate,
            LocalDate toDate,
            Function<T, LocalDate> dateExtractor,
            Function<T, String>... fieldExtractors) {

        String lowerKeyword = keyword == null ? "" : keyword.toLowerCase();

        return list.stream()
                .filter(item -> {
                    // Lọc theo từ khóa
                    boolean keywordMatch = lowerKeyword.isEmpty() ||
                            java.util.Arrays.stream(fieldExtractors)
                                    .anyMatch(extractor -> {
                                        String value = extractor.apply(item);
                                        return value != null && value.toLowerCase().contains(lowerKeyword);
                                    });

                    // Lọc theo ngày (nếu có)
                    boolean dateMatch = true;
                    if (dateExtractor != null) {
                        LocalDate itemDate = dateExtractor.apply(item);
                        if (itemDate != null) {
                            if (fromDate != null && itemDate.isBefore(fromDate)) {
                                dateMatch = false;
                            }
                            if (toDate != null && itemDate.isAfter(toDate)) {
                                dateMatch = false;
                            }
                        }
                    }

                    return keywordMatch && dateMatch;
                })
                .collect(Collectors.toList());
    }
}
