package org.example.dientu99.controller.warehouse;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.chart.*;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.example.dientu99.dto.warehouseDTO.WarehouseDTO;
import org.example.dientu99.enums.WarehouseType;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class WarehouseChartController {
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("MM/yyyy");

    // Màu sắc đẹp hơn cho biểu đồ
    private static final Color IMPORT_START_COLOR = Color.web("#4CAF50");
    private static final Color IMPORT_END_COLOR = Color.web("#2E7D32");
    private static final Color EXPORT_START_COLOR = Color.web("#FF9800");
    private static final Color EXPORT_END_COLOR = Color.web("#EF6C00");

    private static final String IMPORT_POINT_STYLE = "-fx-background-color: #4CAF50, white; -fx-background-radius: 6px; -fx-padding: 5px;";
    private static final String EXPORT_POINT_STYLE = "-fx-background-color: #FF9800, white; -fx-background-radius: 6px; -fx-padding: 5px;";
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

        // Tạo style cho combobox
        cboChartType.setStyle("-fx-font-size: 14px; -fx-background-radius: 5px; -fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 5px;");

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

        VBox messageBox = new VBox();
        messageBox.setAlignment(Pos.CENTER);
        messageBox.setPrefSize(chartPane.getPrefWidth(), chartPane.getPrefHeight());
        messageBox.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 10px; -fx-border-color: #e9ecef; -fx-border-radius: 10px;");

        Label noDataLabel = new Label("Không có dữ liệu để hiển thị");
        noDataLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #6c757d; -fx-font-weight: bold;");

        messageBox.getChildren().add(noDataLabel);
        chartPane.getChildren().add(messageBox);
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
        xAxis.setTickLabelFont(Font.font("System", FontWeight.NORMAL, 12));
        xAxis.setTickLabelFill(Color.web("#495057"));

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Số lượng sản phẩm");
        yAxis.setTickLabelFont(Font.font("System", FontWeight.NORMAL, 12));
        yAxis.setTickLabelFill(Color.web("#495057"));

        StackedBarChart<String, Number> barChart = new StackedBarChart<>(xAxis, yAxis);
        barChart.setTitle("Biểu Đồ Nhập/Xuất Kho Theo Tháng");
        barChart.setStyle("-fx-background-color: transparent;");

        // Thiết lập font cho tiêu đề
        barChart.setTitleSide(Side.TOP);
        barChart.lookup(".chart-title").setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #343a40;");

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

        // Tạo hiệu ứng đổ bóng cho biểu đồ
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5.0);
        dropShadow.setOffsetX(3.0);
        dropShadow.setOffsetY(3.0);
        dropShadow.setColor(Color.rgb(0, 0, 0, 0.2));
        barChart.setEffect(dropShadow);

        // Tạo container cho biểu đồ với padding
        StackPane chartContainer = new StackPane();
        chartContainer.setPadding(new Insets(15));
        chartContainer.setStyle("-fx-background-color: white; -fx-background-radius: 10px;");
        chartContainer.getChildren().add(barChart);

        chartPane.getChildren().add(chartContainer);

        // Apply styles to bars
        applySeriesStyle(importSeries, "Nhập Kho", IMPORT_START_COLOR, IMPORT_END_COLOR);
        applySeriesStyle(exportSeries, "Xuất Kho", EXPORT_START_COLOR, EXPORT_END_COLOR);

        // Tùy chỉnh legend
        barChart.lookup(".chart-legend").setStyle("-fx-background-color: transparent; -fx-padding: 10px;");
    }

    private void createLineChart() {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Thời gian");
        xAxis.setTickLabelFont(Font.font("System", FontWeight.NORMAL, 12));
        xAxis.setTickLabelFill(Color.web("#495057"));

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Số lượng sản phẩm");
        yAxis.setTickLabelFont(Font.font("System", FontWeight.NORMAL, 12));
        yAxis.setTickLabelFill(Color.web("#495057"));

        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Xu Hướng Nhập/Xuất Kho Theo Thời Gian");
        lineChart.setCreateSymbols(true);
        lineChart.setAnimated(true);
        lineChart.setStyle("-fx-background-color: transparent;");

        // Thiết lập font cho tiêu đề
        lineChart.lookup(".chart-title").setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #343a40;");

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

        // Tạo hiệu ứng đổ bóng cho biểu đồ
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5.0);
        dropShadow.setOffsetX(3.0);
        dropShadow.setOffsetY(3.0);
        dropShadow.setColor(Color.rgb(0, 0, 0, 0.2));
        lineChart.setEffect(dropShadow);

        // Tạo container cho biểu đồ với padding
        StackPane chartContainer = new StackPane();
        chartContainer.setPadding(new Insets(15));
        chartContainer.setStyle("-fx-background-color: white; -fx-background-radius: 10px;");
        chartContainer.getChildren().add(lineChart);

        chartPane.getChildren().add(chartContainer);

        // Style for lines
        importSeries.getNode().setStyle(IMPORT_LINE_STYLE);
        exportSeries.getNode().setStyle(EXPORT_LINE_STYLE);

        // Style for data points
        for (XYChart.Data<String, Number> data : importSeries.getData()) {
            data.getNode().setStyle(IMPORT_POINT_STYLE);

            // Thêm tooltip cho điểm dữ liệu
            Tooltip tooltip = new Tooltip("Nhập kho: " + data.getYValue() + " sản phẩm\nTháng: " + data.getXValue());
            Tooltip.install(data.getNode(), tooltip);
        }

        for (XYChart.Data<String, Number> data : exportSeries.getData()) {
            data.getNode().setStyle(EXPORT_POINT_STYLE);

            // Thêm tooltip cho điểm dữ liệu
            Tooltip tooltip = new Tooltip("Xuất kho: " + data.getYValue() + " sản phẩm\nTháng: " + data.getXValue());
            Tooltip.install(data.getNode(), tooltip);
        }

        // Tùy chỉnh legend
        lineChart.lookup(".chart-legend").setStyle("-fx-background-color: transparent; -fx-padding: 10px;");
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
        pieChart.setStyle("-fx-background-color: transparent;");

        // Thiết lập font cho tiêu đề
        pieChart.lookup(".chart-title").setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #343a40;");

        // Tạo hiệu ứng đổ bóng cho biểu đồ
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5.0);
        dropShadow.setOffsetX(3.0);
        dropShadow.setOffsetY(3.0);
        dropShadow.setColor(Color.rgb(0, 0, 0, 0.2));
        pieChart.setEffect(dropShadow);

        // Tạo container cho biểu đồ với padding
        StackPane chartContainer = new StackPane();
        chartContainer.setPadding(new Insets(15));
        chartContainer.setStyle("-fx-background-color: white; -fx-background-radius: 10px;");
        chartContainer.getChildren().add(pieChart);

        chartPane.getChildren().add(chartContainer);

        // Apply gradient colors to pie slices
        applyPieChartColors(pieChartData);

        // Thêm tooltip cho các phần của biểu đồ tròn
        for (PieChart.Data data : pieChartData) {
            Tooltip tooltip = new Tooltip(data.getName() + ": " + data.getPieValue() + " sản phẩm");
            Tooltip.install(data.getNode(), tooltip);

            // Thêm hiệu ứng khi hover
            data.getNode().setOnMouseEntered(e -> {
                data.getNode().setScaleX(1.1);
                data.getNode().setScaleY(1.1);
            });

            data.getNode().setOnMouseExited(e -> {
                data.getNode().setScaleX(1);
                data.getNode().setScaleY(1);
            });
        }

        // Tùy chỉnh legend
        pieChart.lookup(".chart-legend").setStyle("-fx-background-color: transparent; -fx-padding: 10px;");
    }

    private void applyPieChartColors(ObservableList<PieChart.Data> pieChartData) {
        int index = 0;
        for (PieChart.Data data : pieChartData) {
            if (index == 0) {
                // Nhập Kho - Green gradient
                data.getNode().setStyle("-fx-pie-color: #4CAF50;");
            } else {
                // Xuất Kho - Orange gradient
                data.getNode().setStyle("-fx-pie-color: #FF9800;");
            }
            index++;
        }
    }

    private int calculateTotal(WarehouseType type) {
        return transactions.stream()
                .filter(transaction -> transaction.getType() == type)
                .mapToInt(WarehouseDTO::getQuantity)
                .sum();
    }

    private void applySeriesStyle(XYChart.Series<String, Number> series, String seriesName, Color startColor, Color endColor) {
        if (series.getName().equals(seriesName)) {
            for (XYChart.Data<String, Number> data : series.getData()) {
                // Đặt màu đơn giản thay vì gradient để tránh lỗi
                Region bar = (Region) data.getNode();
                bar.setStyle("-fx-background-color: " + toRgbString(startColor) + ";");

                // Thêm tooltip cho cột
                Tooltip tooltip = new Tooltip(seriesName + ": " + data.getYValue() + " sản phẩm");
                Tooltip.install(data.getNode(), tooltip);

                // Thêm hiệu ứng khi hover
                data.getNode().setOnMouseEntered(e -> {
                    data.getNode().setOpacity(0.8);
                });

                data.getNode().setOnMouseExited(e -> {
                    data.getNode().setOpacity(1);
                });
            }
        }
    }

    // Helper method to convert Color to RGB string
    private String toRgbString(Color color) {
        return String.format("rgb(%d, %d, %d)",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    // Method for use in DashboardTabController
    public Pane getChartPane() {
        VBox chartContainer = new VBox(15);
        chartContainer.setPrefWidth(1280);
        chartContainer.setPrefHeight(350);
        chartContainer.setPadding(new Insets(10));
        chartContainer.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 10px; -fx-border-color: #e9ecef; -fx-border-radius: 10px;");

        HBox headerBox = new HBox(20);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setPadding(new Insets(0, 0, 10, 0));

        ComboBox<String> chartTypeComboBox = new ComboBox<>();
        chartTypeComboBox.setPromptText("Chọn Loại Biểu Đồ");
        chartTypeComboBox.setPrefWidth(250);
        chartTypeComboBox.setStyle("-fx-font-size: 14px; -fx-background-radius: 5px; -fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 5px;");
        this.cboChartType = chartTypeComboBox;

        Label chartTitle = new Label("Biểu Đồ Thống Kê Kho Hàng");
        chartTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #343a40;");
        this.lblChartTitle = chartTitle;

        headerBox.getChildren().addAll(chartTypeComboBox, chartTitle);

        StackPane chartContentPane = new StackPane();
        chartContentPane.setPrefHeight(300);
        chartContentPane.setStyle("-fx-background-color: white; -fx-background-radius: 10px; -fx-border-color: #e9ecef; -fx-border-radius: 10px;");

        // Tạo hiệu ứng đổ bóng
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5.0);
        dropShadow.setOffsetX(3.0);
        dropShadow.setOffsetY(3.0);
        dropShadow.setColor(Color.rgb(0, 0, 0, 0.2));
        chartContentPane.setEffect(dropShadow);

        this.chartPane = chartContentPane;

        chartContainer.getChildren().addAll(headerBox, chartContentPane);

        initialize();

        return chartContainer;
    }

    // Thêm class Tooltip để hiển thị thông tin khi hover
    private static class Tooltip {
        private static void install(javafx.scene.Node node, Tooltip tooltip) {
            javafx.scene.control.Tooltip javaFxTooltip = new javafx.scene.control.Tooltip(tooltip.text);
            javaFxTooltip.setStyle("-fx-font-size: 12px; -fx-background-color: rgba(50,50,50,0.9); -fx-text-fill: white;");
            javafx.scene.control.Tooltip.install(node, javaFxTooltip);
        }

        private final String text;

        public Tooltip(String text) {
            this.text = text;
        }
    }
}