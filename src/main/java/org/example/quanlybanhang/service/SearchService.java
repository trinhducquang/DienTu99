package org.example.quanlybanhang.service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SearchService {

    @SafeVarargs
    public static <T> List<T> search(List<T> list, String keyword, Function<T, String>... fieldExtractors) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return list;
        }

        String lowerKeyword = keyword.toLowerCase();
        return list.stream()
                .filter(item -> {
                    for (Function<T, String> extractor : fieldExtractors) {
                        if (extractor.apply(item).toLowerCase().contains(lowerKeyword)) {
                            return true;
                        }
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }
}
