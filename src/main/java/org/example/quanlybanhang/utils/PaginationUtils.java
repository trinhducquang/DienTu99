package org.example.quanlybanhang.utils;

import javafx.beans.property.IntegerProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Pagination;
import org.example.quanlybanhang.utils.PaginationUtils;


import java.util.List;
import java.util.function.BiConsumer;

public class PaginationUtils {

    public static <T> void setup(
            Pagination pagination,
            ObservableList<T> source,  // Change this to ObservableList
            ObservableList<T> target,
            IntegerProperty currentPage,
            int itemsPerPage,
            BiConsumer<List<T>, Integer> onPageChanged
    ) {
        // Update page count whenever source changes
        updatePageCount(pagination, source, itemsPerPage);

        // Listen for changes to the source list
        source.addListener((javafx.collections.ListChangeListener<T>) c -> {
            updatePageCount(pagination, source, itemsPerPage);
            // When source changes, reset to first page
            pagination.setCurrentPageIndex(0);
            currentPage.set(0);
        });

        // Set up the page factory
        pagination.currentPageIndexProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                currentPage.set(newVal.intValue());
            }
        });

        pagination.setPageFactory(pageIndex -> {
            int from = pageIndex * itemsPerPage;
            int to = Math.min(from + itemsPerPage, source.size());

            List<T> page = from < to ? source.subList(from, to) : List.of();
            target.setAll(page);
            if (onPageChanged != null) onPageChanged.accept(page, pageIndex);
            return new javafx.scene.layout.VBox();
        });

        // Initialize the first page
        int initialPage = Math.min(currentPage.get(), Math.max(0, pagination.getPageCount() - 1));
        pagination.setCurrentPageIndex(initialPage);
    }

    private static <T> void updatePageCount(Pagination pagination, List<T> source, int itemsPerPage) {
        int pageCount = Math.max(1, (int) Math.ceil((double) source.size() / itemsPerPage));
        pagination.setPageCount(pageCount);
    }
}