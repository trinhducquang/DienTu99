package org.example.quanlybanhang.utils;

import javafx.application.Platform;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadManager {

    // Pool 4 luồng cho task nhẹ (I/O nhẹ, JSON, hình ảnh nhỏ)
    private static final ExecutorService pool = Executors.newFixedThreadPool(4);

    // Nếu muốn task nặng hơn (phân tích dữ liệu...), dùng pool riêng
    private static final ExecutorService heavyPool = Executors.newCachedThreadPool();

    public static void runBackground(Runnable task) {
        pool.submit(task);
    }

    public static void runHeavy(Runnable task) {
        heavyPool.submit(task);
    }

    public static void runOnUiThread(Runnable task) {
        Platform.runLater(task);
    }

    public static void shutdown() {
        pool.shutdownNow();
        heavyPool.shutdownNow();
    }
}
