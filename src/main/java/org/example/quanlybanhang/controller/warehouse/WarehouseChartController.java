package org.example.quanlybanhang.controller.warehouse;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.example.quanlybanhang.dto.warehouseDTO.WarehouseDTO;
import org.example.quanlybanhang.enums.WarehouseType;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class WarehouseChartController {

    @FXML
    private Pane chartPane;

    @FXML
    private ComboBox<String> cboChartType;

    private ObservableList<WarehouseDTO> transactions;
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("MM/yyyy");
    private Label lblChartTitle;

    public void initialize() {
        setupChartTypeComboBox();
    }

    private void setupChartTypeComboBox() {
        ObservableList<String> chartTypes = FXCollections.observableArrayList(
                "Biểu Đồ Cột - Nhập/Xuất Theo Tháng",
                "Biểu Đồ Đường - Xu Hướng Nhập/Xuất",
                "Biểu Đồ Kết Hợp - Nhập/Xuất & Tồn Kho",
                "Biểu Đồ Tròn - Tỷ Lệ Nhập/Xuất Kho"
        );

        cboChartType.setItems(chartTypes);
        cboChartType.getSelectionModel().selectFirst();

        cboChartType.setOnAction(event -> {
            updateChart();
        });
    }

    public void setTransactionData(ObservableList<WarehouseDTO> transactions) {
        this.transactions = transactions;
        updateChart();
    }

    private void updateChart() {
        if (transactions == null || transactions.isEmpty()) {
            showNoDataMessage();
            return;
        }

        chartPane.getChildren().clear();

        String selectedChartType = cboChartType.getValue();

        switch (selectedChartType) {
            case "Biểu Đồ Cột - Nhập/Xuất Theo Tháng":
                createBarChart();
                break;
            case "Biểu Đồ Đường - Xu Hướng Nhập/Xuất":
                createLineChart();
                break;
            case "Biểu Đồ Kết Hợp - Nhập/Xuất & Tồn Kho":
                createCombinedChart();
                break;
            case "Biểu Đồ Tròn - Tỷ Lệ Nhập/Xuất Kho":
                createPieChart();
                break;
            default:
                createBarChart();
                break;
        }
    }

    private void showNoDataMessage() {
        chartPane.getChildren().clear();
        Label noDataLabel = new Label("Không có dữ liệu để hiển thị");
        noDataLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: gray;");
        StackPane.setAlignment(noDataLabel, Pos.CENTER);
        chartPane.getChildren().add(noDataLabel);
    }


    private void createBarChart() {
        // Tạo trục X
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Tháng");

        // Tạo trục Y
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Số lượng sản phẩm");

        // Tạo biểu đồ cột
        StackedBarChart<String, Number> barChart = new StackedBarChart<>(xAxis, yAxis);
        barChart.setTitle("Biểu Đồ Nhập/Xuất Kho Theo Tháng");

        // Tạo series cho nhập kho
        XYChart.Series<String, Number> importSeries = new XYChart.Series<>();
        importSeries.setName("Nhập Kho");

        // Tạo series cho xuất kho
        XYChart.Series<String, Number> exportSeries = new XYChart.Series<>();
        exportSeries.setName("Xuất Kho");

        // Dữ liệu theo tháng
        Map<YearMonth, Integer> importByMonth = new TreeMap<>();
        Map<YearMonth, Integer> exportByMonth = new TreeMap<>();

        // Phân loại giao dịch theo tháng và loại
        for (WarehouseDTO transaction : transactions) {
            if (transaction.getCreatedAt() == null) continue;

            YearMonth month = YearMonth.from(transaction.getCreatedAt().toLocalDate());
            int quantity = transaction.getQuantity();

            if (transaction.getType() == WarehouseType.NHAP_KHO) {
                importByMonth.put(month, importByMonth.getOrDefault(month, 0) + quantity);
            } else if (transaction.getType() == WarehouseType.XUAT_KHO) {
                exportByMonth.put(month, exportByMonth.getOrDefault(month, 0) + quantity);
            }
        }

        // Lấy danh sách các tháng duy nhất
        Set<YearMonth> allMonths = new TreeSet<>();
        allMonths.addAll(importByMonth.keySet());
        allMonths.addAll(exportByMonth.keySet());

        // Thêm dữ liệu vào series
        for (YearMonth month : allMonths) {
            String monthStr = month.format(MONTH_FORMATTER);
            importSeries.getData().add(new XYChart.Data<>(monthStr, importByMonth.getOrDefault(month, 0)));
            exportSeries.getData().add(new XYChart.Data<>(monthStr, exportByMonth.getOrDefault(month, 0)));
        }

        barChart.getData().addAll(importSeries, exportSeries);
        barChart.setPrefSize(chartPane.getPrefWidth(), chartPane.getPrefHeight());
        barChart.setLegendVisible(true);

        // Thêm biểu đồ vào container
        chartPane.getChildren().add(barChart);

        // Style cho từng loại cột
        for (XYChart.Series<String, Number> series : barChart.getData()) {
            if (series.getName().equals("Nhập Kho")) {
                for (XYChart.Data<String, Number> data : series.getData()) {
                    data.getNode().setStyle("-fx-bar-fill: #4CAF50;");
                }
            } else if (series.getName().equals("Xuất Kho")) {
                for (XYChart.Data<String, Number> data : series.getData()) {
                    data.getNode().setStyle("-fx-bar-fill: #FF9800;");
                }
            }
        }
    }

    private void createLineChart() {
        // Tạo trục X
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Thời gian");

        // Tạo trục Y
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Số lượng sản phẩm");

        // Tạo biểu đồ đường
        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Xu Hướng Nhập/Xuất Kho Theo Thời Gian");
        lineChart.setCreateSymbols(true);
        lineChart.setAnimated(true);

        // Tạo series cho nhập kho
        XYChart.Series<String, Number> importSeries = new XYChart.Series<>();
        importSeries.setName("Nhập Kho");

        // Tạo series cho xuất kho
        XYChart.Series<String, Number> exportSeries = new XYChart.Series<>();
        exportSeries.setName("Xuất Kho");

        // Dữ liệu theo tháng
        Map<YearMonth, Integer> importByMonth = new TreeMap<>();
        Map<YearMonth, Integer> exportByMonth = new TreeMap<>();

        // Phân loại giao dịch theo tháng và loại
        for (WarehouseDTO transaction : transactions) {
            if (transaction.getCreatedAt() == null) continue;

            YearMonth month = YearMonth.from(transaction.getCreatedAt().toLocalDate());
            int quantity = transaction.getQuantity();

            if (transaction.getType() == WarehouseType.NHAP_KHO) {
                importByMonth.put(month, importByMonth.getOrDefault(month, 0) + quantity);
            } else if (transaction.getType() == WarehouseType.XUAT_KHO) {
                exportByMonth.put(month, exportByMonth.getOrDefault(month, 0) + quantity);
            }
        }

        // Lấy danh sách các tháng duy nhất và sắp xếp
        Set<YearMonth> allMonths = new TreeSet<>();
        allMonths.addAll(importByMonth.keySet());
        allMonths.addAll(exportByMonth.keySet());

        // Thêm dữ liệu vào series
        for (YearMonth month : allMonths) {
            String monthStr = month.format(MONTH_FORMATTER);
            importSeries.getData().add(new XYChart.Data<>(monthStr, importByMonth.getOrDefault(month, 0)));
            exportSeries.getData().add(new XYChart.Data<>(monthStr, exportByMonth.getOrDefault(month, 0)));
        }

        lineChart.getData().addAll(importSeries, exportSeries);
        lineChart.setPrefSize(chartPane.getPrefWidth(), chartPane.getPrefHeight());
        lineChart.setLegendVisible(true);

        // Thêm biểu đồ vào container
        chartPane.getChildren().add(lineChart);

        // Style cho từng đường
        importSeries.getNode().setStyle("-fx-stroke: #4CAF50; -fx-stroke-width: 3px;");
        exportSeries.getNode().setStyle("-fx-stroke: #FF9800; -fx-stroke-width: 3px;");

        // Style cho các điểm dữ liệu
        for (XYChart.Data<String, Number> data : importSeries.getData()) {
            data.getNode().setStyle("-fx-background-color: #4CAF50, white;");
        }

        for (XYChart.Data<String, Number> data : exportSeries.getData()) {
            data.getNode().setStyle("-fx-background-color: #FF9800, white;");
        }
    }

    private void createCombinedChart() {
        // Tạo các trục
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis1 = new NumberAxis();
        final NumberAxis yAxis2 = new NumberAxis();

        xAxis.setLabel("Tháng");
        yAxis1.setLabel("Số lượng nhập/xuất");
        yAxis2.setLabel("Tồn kho");

        // Tạo biểu đồ cột cho nhập/xuất
        StackedBarChart<String, Number> barChart = new StackedBarChart<>(xAxis, yAxis1);
        barChart.setTitle("Biểu Đồ Nhập/Xuất & Tồn Kho");

        // Tạo biểu đồ đường cho tồn kho
        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis2);
        lineChart.setCreateSymbols(true);

        // Series cho biểu đồ cột
        XYChart.Series<String, Number> importSeries = new XYChart.Series<>();
        importSeries.setName("Nhập Kho");
        XYChart.Series<String, Number> exportSeries = new XYChart.Series<>();
        exportSeries.setName("Xuất Kho");

        // Series cho biểu đồ đường
        XYChart.Series<String, Number> stockSeries = new XYChart.Series<>();
        stockSeries.setName("Tồn Kho");

        // Dữ liệu theo tháng
        Map<YearMonth, Integer> importByMonth = new TreeMap<>();
        Map<YearMonth, Integer> exportByMonth = new TreeMap<>();
        Map<YearMonth, Integer> stockByMonth = new TreeMap<>();

        // Phân loại giao dịch theo tháng và loại
        int currentStock = 0;
        List<WarehouseDTO> sortedTransactions = transactions.stream()
                .filter(t -> t.getCreatedAt() != null)
                .sorted(Comparator.comparing(WarehouseDTO::getCreatedAt))
                .collect(Collectors.toList());

        for (WarehouseDTO transaction : sortedTransactions) {
            YearMonth month = YearMonth.from(transaction.getCreatedAt().toLocalDate());
            int quantity = transaction.getQuantity();

            if (transaction.getType() == WarehouseType.NHAP_KHO) {
                importByMonth.put(month, importByMonth.getOrDefault(month, 0) + quantity);
                currentStock += quantity;
            } else if (transaction.getType() == WarehouseType.XUAT_KHO) {
                exportByMonth.put(month, exportByMonth.getOrDefault(month, 0) + quantity);
                currentStock -= quantity;
            }

            stockByMonth.put(month, currentStock);
        }

        // Lấy danh sách các tháng duy nhất
        Set<YearMonth> allMonths = new TreeSet<>();
        allMonths.addAll(importByMonth.keySet());
        allMonths.addAll(exportByMonth.keySet());

        // Thêm dữ liệu vào series
        for (YearMonth month : allMonths) {
            String monthStr = month.format(MONTH_FORMATTER);

            importSeries.getData().add(new XYChart.Data<>(monthStr, importByMonth.getOrDefault(month, 0)));
            exportSeries.getData().add(new XYChart.Data<>(monthStr, exportByMonth.getOrDefault(month, 0)));
            stockSeries.getData().add(new XYChart.Data<>(monthStr, stockByMonth.getOrDefault(month, 0)));
        }

        barChart.getData().addAll(importSeries, exportSeries);
        lineChart.getData().add(stockSeries);

        barChart.setPrefSize(chartPane.getPrefWidth(), chartPane.getPrefHeight());
        lineChart.setPrefSize(chartPane.getPrefWidth(), chartPane.getPrefHeight());

        // Thêm cả hai biểu đồ vào cùng container
        barChart.setOpacity(0.8);
        lineChart.setOpacity(0.8);
        lineChart.setLegendVisible(false);

        chartPane.getChildren().addAll(barChart, lineChart);

        // Style cho biểu đồ cột
        for (XYChart.Series<String, Number> series : barChart.getData()) {
            if (series.getName().equals("Nhập Kho")) {
                for (XYChart.Data<String, Number> data : series.getData()) {
                    data.getNode().setStyle("-fx-bar-fill: #4CAF50;");
                }
            } else if (series.getName().equals("Xuất Kho")) {
                for (XYChart.Data<String, Number> data : series.getData()) {
                    data.getNode().setStyle("-fx-bar-fill: #FF9800;");
                }
            }
        }

        // Style cho biểu đồ đường
        stockSeries.getNode().setStyle("-fx-stroke: #2196F3; -fx-stroke-width: 3px;");
        for (XYChart.Data<String, Number> data : stockSeries.getData()) {
            data.getNode().setStyle("-fx-background-color: #2196F3, white;");
        }
    }

    private void createPieChart() {
        // Tính tổng số lượng nhập/xuất
        int totalImport = 0;
        int totalExport = 0;

        for (WarehouseDTO transaction : transactions) {
            if (transaction.getType() == WarehouseType.NHAP_KHO) {
                totalImport += transaction.getQuantity();
            } else if (transaction.getType() == WarehouseType.XUAT_KHO) {
                totalExport += transaction.getQuantity();
            }
        }

        // Tạo dữ liệu cho biểu đồ tròn
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Nhập Kho (" + totalImport + ")", totalImport),
                new PieChart.Data("Xuất Kho (" + totalExport + ")", totalExport)
        );

        // Tạo biểu đồ tròn
        PieChart pieChart = new PieChart(pieChartData);
        pieChart.setTitle("Tỷ Lệ Nhập/Xuất Kho");
        pieChart.setLabelsVisible(true);
        pieChart.setLegendVisible(true);
        pieChart.setStartAngle(90);

        pieChart.setPrefSize(chartPane.getPrefWidth(), chartPane.getPrefHeight());

        chartPane.getChildren().add(pieChart);

        // Style màu cho từng phần
        pieChartData.get(0).getNode().setStyle("-fx-pie-color: #4CAF50;");
        pieChartData.get(1).getNode().setStyle("-fx-pie-color: #FF9800;");
    }

    // Hàm để thêm vào DashboardTabController
    public Pane getChartPane() {
        // VBox chính chứa toàn bộ
        VBox chartContainer = new VBox(10);
        chartContainer.setPrefWidth(1280);
        chartContainer.setPrefHeight(300);
        chartContainer.setPadding(new Insets(5));

        // HBox chứa ComboBox và tiêu đề
        HBox headerBox = new HBox(20);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        // ComboBox chọn loại biểu đồ
        ComboBox<String> chartTypeComboBox = new ComboBox<>();
        chartTypeComboBox.setPromptText("Chọn Loại Biểu Đồ");
        chartTypeComboBox.setPrefWidth(250);
        this.cboChartType = chartTypeComboBox;

        // Label tiêu đề
        Label chartTitle = new Label();
        chartTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        this.lblChartTitle = chartTitle;

        headerBox.getChildren().addAll(chartTypeComboBox, chartTitle);

        // StackPane chứa biểu đồ (giúp tự động căn giữa)
        StackPane chartContentPane = new StackPane();
        chartContentPane.setPrefHeight(500);
        this.chartPane = chartContentPane;

        // Thêm tất cả vào VBox
        chartContainer.getChildren().addAll(headerBox, chartContentPane);

        // Gọi initialize để thiết lập ComboBox
        initialize();

        return chartContainer;
    }

}