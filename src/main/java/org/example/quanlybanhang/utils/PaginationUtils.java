package org.example.quanlybanhang.utils;

import javafx.beans.property.IntegerProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Pagination;

import java.util.List;
import java.util.function.BiConsumer;

public class PaginationUtils {

    public static <T> void setup(
            Pagination pagination,
            List<T> source,
            ObservableList<T> target,
            IntegerProperty currentPage,
            int itemsPerPage,
            BiConsumer<List<T>, Integer> onPageChanged
    ) {
        int pageCount = Math.max(1, (int) Math.ceil((double) source.size() / itemsPerPage)); // pageCount tối thiểu là 1
        pagination.setPageCount(pageCount);

        // Đảm bảo currentPageIndex không vượt quá pageCount - 1
        int initialPageIndex = Math.min(currentPage.get(), pageCount - 1);
        pagination.setCurrentPageIndex(initialPageIndex);
        currentPage.set(initialPageIndex);

        Runnable update = () -> {
            int index = currentPage.get();
            int from = index * itemsPerPage;
            int to = Math.min(from + itemsPerPage, source.size());

            List<T> page = from <= to ? source.subList(from, to) : List.of();
            target.setAll(page);
            if (onPageChanged != null) onPageChanged.accept(page, index);
        };

        pagination.setPageFactory(i -> {
            int validIndex = Math.min(i, pagination.getPageCount() - 1);
            currentPage.set(validIndex);
            update.run();
            return new javafx.scene.layout.VBox(); // hoặc có thể trả về node content nếu cần
        });

        currentPage.addListener((obs, oldVal, newVal) -> update.run());

        update.run(); // Khởi tạo trang đầu
    }
}
