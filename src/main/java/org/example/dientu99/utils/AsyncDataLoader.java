package org.example.dientu99.utils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class AsyncDataLoader {

    /**
     * Tải dữ liệu theo trang một cách bất đồng bộ
     *
     * @param pageIndex Chỉ số trang cần tải
     * @param pageSize Kích thước trang
     * @param dataProvider Hàm tải dữ liệu (nhận pageIndex và pageSize, trả về danh sách kết quả)
     * @param onSuccess Callback khi tải thành công
     * @param onError Callback khi có lỗi
     * @param <T> Kiểu dữ liệu của các mục
     * @return CompletableFuture đại diện cho tác vụ đang thực hiện
     */
    public static <T> CompletableFuture<Void> loadPageAsync(
            int pageIndex,
            int pageSize,
            BiFunction<Integer, Integer, List<T>> dataProvider,
            Consumer<List<T>> onSuccess,
            Consumer<Throwable> onError) {

        return CompletableFuture.supplyAsync(() -> {
                    try {
                        return dataProvider.apply(pageIndex, pageSize);
                    } catch (Exception e) {
                        throw new RuntimeException("Lỗi khi tải dữ liệu: " + e.getMessage(), e);
                    }
                })
                .thenAccept(onSuccess)
                .exceptionally(e -> {
                    onError.accept(e);
                    return null;
                });
    }
}