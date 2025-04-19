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

public class WarehouseChartController {
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("MM/yyyy");
    private static final String IMPORT_COLOR = "-fx-bar-fill: #4CAF50; -fx-stroke: #4CAF50; -fx-pie-color: #4CAF50;";
    private static final String EXPORT_COLOR = "-fx-bar-fill: #FF9800; -fx-stroke: #FF9800; -fx-pie-color: #FF9800;";
    private static final String IMPORT_POINT_STYLE = "-fx-background-color: #4CAF50, white;";
    private static final String EXPORT_POINT_STYLE = "-fx-background-color: #FF9800, white;";
    private static final String IMPORT_LINE_STYLE = "-fx-stroke: #4CAF50; -fx-stroke-width: 3px;";
    private static final String EXPORT_LINE_STYLE = "-fx-stroke: #FF9800; -fx-stroke-width: 3px;";

    @FXML
    private Pane chartPane;

    @FXML
    private ComboBox<String> cboChartType;

    private ObservableList<WarehouseDTO> transactions;
    private Label lblChartTitle;

    @FXML
    public void initialize() {
        setupChartTypeComboBox();
    }

    private void setupChartTypeComboBox() {
        ObservableList<String> chartTypes = FXCollections.observableArrayList(
                "Biểu Đồ Cột - Nhập/Xuất Theo Tháng",
                "Biểu Đồ Đường - Xu Hướng Nhập/Xuất",
                "Biểu Đồ Tròn - Tỷ Lệ Nhập/Xuất Kho"
        );

        cboChartType.setItems(chartTypes);
        cboChartType.getSelectionModel().selectFirst();
        cboChartType.setOnAction(event -> updateChart());
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

    /**
     * Aggregates monthly data for imports and exports from transactions
     * @return A map containing total imports and exports by month
     */
    private Map<String, Map<YearMonth, Integer>> aggregateMonthlyData() {
        Map<YearMonth, Integer> importByMonth = new TreeMap<>();
        Map<YearMonth, Integer> exportByMonth = new TreeMap<>();

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

        Map<String, Map<YearMonth, Integer>> result = new HashMap<>();
        result.put("import", importByMonth);
        result.put("export", exportByMonth);
        return result;
    }

    private void createBarChart() {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Tháng");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Số lượng sản phẩm");

        StackedBarChart<String, Number> barChart = new StackedBarChart<>(xAxis, yAxis);
        barChart.setTitle("Biểu Đồ Nhập/Xuất Kho Theo Tháng");

        XYChart.Series<String, Number> importSeries = new XYChart.Series<>();
        importSeries.setName("Nhập Kho");

        XYChart.Series<String, Number> exportSeries = new XYChart.Series<>();
        exportSeries.setName("Xuất Kho");

        Map<String, Map<YearMonth, Integer>> monthlyData = aggregateMonthlyData();
        Map<YearMonth, Integer> importByMonth = monthlyData.get("import");
        Map<YearMonth, Integer> exportByMonth = monthlyData.get("export");

        Set<YearMonth> allMonths = new TreeSet<>();
        allMonths.addAll(importByMonth.keySet());
        allMonths.addAll(exportByMonth.keySet());

        for (YearMonth month : allMonths) {
            String monthStr = month.format(MONTH_FORMATTER);
            importSeries.getData().add(new XYChart.Data<>(monthStr, importByMonth.getOrDefault(month, 0)));
            exportSeries.getData().add(new XYChart.Data<>(monthStr, exportByMonth.getOrDefault(month, 0)));
        }

        barChart.getData().addAll(importSeries, exportSeries);
        barChart.setPrefSize(chartPane.getPrefWidth(), chartPane.getPrefHeight());
        barChart.setLegendVisible(true);

        chartPane.getChildren().add(barChart);

        // Apply styles to bars
        applySeriesStyle(importSeries, "Nhập Kho", IMPORT_COLOR);
        applySeriesStyle(exportSeries, "Xuất Kho", EXPORT_COLOR);
    }

    private void createLineChart() {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Thời gian");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Số lượng sản phẩm");

        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Xu Hướng Nhập/Xuất Kho Theo Thời Gian");
        lineChart.setCreateSymbols(true);
        lineChart.setAnimated(true);

        XYChart.Series<String, Number> importSeries = new XYChart.Series<>();
        importSeries.setName("Nhập Kho");

        XYChart.Series<String, Number> exportSeries = new XYChart.Series<>();
        exportSeries.setName("Xuất Kho");

        Map<String, Map<YearMonth, Integer>> monthlyData = aggregateMonthlyData();
        Map<YearMonth, Integer> importByMonth = monthlyData.get("import");
        Map<YearMonth, Integer> exportByMonth = monthlyData.get("export");

        Set<YearMonth> allMonths = new TreeSet<>();
        allMonths.addAll(importByMonth.keySet());
        allMonths.addAll(exportByMonth.keySet());

        for (YearMonth month : allMonths) {
            String monthStr = month.format(MONTH_FORMATTER);
            importSeries.getData().add(new XYChart.Data<>(monthStr, importByMonth.getOrDefault(month, 0)));
            exportSeries.getData().add(new XYChart.Data<>(monthStr, exportByMonth.getOrDefault(month, 0)));
        }

        lineChart.getData().addAll(importSeries, exportSeries);
        lineChart.setPrefSize(chartPane.getPrefWidth(), chartPane.getPrefHeight());
        lineChart.setLegendVisible(true);

        chartPane.getChildren().add(lineChart);

        // Style for lines
        importSeries.getNode().setStyle(IMPORT_LINE_STYLE);
        exportSeries.getNode().setStyle(EXPORT_LINE_STYLE);

        // Style for data points
        for (XYChart.Data<String, Number> data : importSeries.getData()) {
            data.getNode().setStyle(IMPORT_POINT_STYLE);
        }

        for (XYChart.Data<String, Number> data : exportSeries.getData()) {
            data.getNode().setStyle(EXPORT_POINT_STYLE);
        }
    }

    private void createPieChart() {
        int totalImport = calculateTotal(WarehouseType.NHAP_KHO);
        int totalExport = calculateTotal(WarehouseType.XUAT_KHO);

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Nhập Kho (" + totalImport + ")", totalImport),
                new PieChart.Data("Xuất Kho (" + totalExport + ")", totalExport)
        );

        PieChart pieChart = new PieChart(pieChartData);
        pieChart.setTitle("Tỷ Lệ Nhập/Xuất Kho");
        pieChart.setLabelsVisible(true);
        pieChart.setLegendVisible(true);
        pieChart.setStartAngle(90);
        pieChart.setPrefSize(chartPane.getPrefWidth(), chartPane.getPrefHeight());

        chartPane.getChildren().add(pieChart);

        // Apply styles to pie slices
        pieChartData.get(0).getNode().setStyle(IMPORT_COLOR);
        pieChartData.get(1).getNode().setStyle(EXPORT_COLOR);
    }

    private int calculateTotal(WarehouseType type) {
        return transactions.stream()
                .filter(transaction -> transaction.getType() == type)
                .mapToInt(WarehouseDTO::getQuantity)
                .sum();
    }

    private void applySeriesStyle(XYChart.Series<String, Number> series, String seriesName, String style) {
        if (series.getName().equals(seriesName)) {
            for (XYChart.Data<String, Number> data : series.getData()) {
                data.getNode().setStyle(style);
            }
        }
    }

    // Method for use in DashboardTabController
    public Pane getChartPane() {
        VBox chartContainer = new VBox(10);
        chartContainer.setPrefWidth(1280);
        chartContainer.setPrefHeight(300);
        chartContainer.setPadding(new Insets(5));

        HBox headerBox = new HBox(20);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        ComboBox<String> chartTypeComboBox = new ComboBox<>();
        chartTypeComboBox.setPromptText("Chọn Loại Biểu Đồ");
        chartTypeComboBox.setPrefWidth(250);
        this.cboChartType = chartTypeComboBox;

        Label chartTitle = new Label();
        chartTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        this.lblChartTitle = chartTitle;

        headerBox.getChildren().addAll(chartTypeComboBox, chartTitle);

        StackPane chartContentPane = new StackPane();
        chartContentPane.setPrefHeight(500);
        this.chartPane = chartContentPane;

        chartContainer.getChildren().addAll(headerBox, chartContentPane);

        initialize();

        return chartContainer;
    }
}