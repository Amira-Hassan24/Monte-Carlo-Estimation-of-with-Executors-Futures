/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javafxapplication3;

/**
 *
 * @author A
 */

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.effect.DropShadow;
import javafx.stage.Stage;

import java.util.concurrent.ThreadLocalRandom;

public class MonteCarloGUI extends Application {

    private final int canvasSize = 450;
    private Canvas canvas;
    private SimulationConfig config;

    private PiExperimentRunner.Result seqResult;
    private PiExperimentRunner.Result parResult;

    private TextField tfPoints, tfTasks, tfThreads;
    private Label lblSeqPi, lblSeqError, lblSeqTime;
    private Label lblParPi, lblParError, lblParTime, lblSpeedup, lblEfficiency;

    @Override
    public void start(Stage primaryStage) {
        // Canvas للرسم مع تحسينات
        canvas = new Canvas(canvasSize, canvasSize);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        drawCanvas(gc);
        
        // إضافة ظل للـ Canvas
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.3));
        shadow.setRadius(15);
        canvas.setEffect(shadow);

        // Panel خاص بالـ Canvas
        VBox canvasBox = new VBox(canvas);
        canvasBox.setAlignment(Pos.CENTER);
        canvasBox.setPadding(new Insets(20));
        canvasBox.setStyle("-fx-background-color: white; -fx-background-radius: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 15, 0, 0, 5);");

        // Inputs مع تحسينات
        Label titleInput = createTitleLabel("⚙ Simulation Parameters");
        
        tfPoints = createStyledTextField("1000000");
        tfTasks = createStyledTextField("4");
        tfThreads = createStyledTextField("4");

        GridPane inputPane = new GridPane();
        inputPane.setPadding(new Insets(20));
        inputPane.setHgap(15);
        inputPane.setVgap(12);
        inputPane.setStyle("-fx-background-color: linear-gradient(to bottom, #667eea 0%, #764ba2 100%); -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 3);");
        
        Label lblPoints = createWhiteLabel("Total Points (N):");
        Label lblTasks = createWhiteLabel("Number of Tasks:");
        Label lblThreads = createWhiteLabel("Number of Threads:");
        
        inputPane.addRow(0, lblPoints, tfPoints);
        inputPane.addRow(1, lblTasks, tfTasks);
        inputPane.addRow(2, lblThreads, tfThreads);

        // Results Panel
        Label titleResults = createTitleLabel("📊 Results");
        
        lblSeqPi = createResultLabel("Sequential π: -", "#667eea");
        lblSeqError = createResultLabel("Error: -", "#667eea");
        lblSeqTime = createResultLabel("Time (ms): -", "#667eea");

        VBox seqBox = new VBox(8, 
            createSectionLabel("Sequential Estimation", "#667eea"),
            lblSeqPi, lblSeqError, lblSeqTime
        );
        seqBox.setPadding(new Insets(15));
        seqBox.setStyle("-fx-background-color: rgba(102, 126, 234, 0.1); -fx-background-radius: 12; -fx-border-color: #667eea; -fx-border-width: 2; -fx-border-radius: 12;");

        lblParPi = createResultLabel("Parallel π: -", "#f093fb");
        lblParError = createResultLabel("Error: -", "#f093fb");
        lblParTime = createResultLabel("Time (ms): -", "#f093fb");
        lblSpeedup = createResultLabel("Speedup: -", "#4facfe");
        lblEfficiency = createResultLabel("Efficiency: -", "#43e97b");

        VBox parBox = new VBox(8, 
            createSectionLabel("Parallel Estimation", "#f093fb"),
            lblParPi, lblParError, lblParTime, lblSpeedup, lblEfficiency
        );
        parBox.setPadding(new Insets(15));
        parBox.setStyle("-fx-background-color: rgba(240, 147, 251, 0.1); -fx-background-radius: 12; -fx-border-color: #f093fb; -fx-border-width: 2; -fx-border-radius: 12;");

        VBox resultPane = new VBox(15, titleResults, seqBox, parBox);
        resultPane.setPadding(new Insets(20));
        resultPane.setStyle("-fx-background-color: white; -fx-background-radius: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 15, 0, 0, 5);");

        // Buttons مع تحسينات
        Button btnSequential = createGradientButton("🔄 Run Sequential", "#667eea", "#764ba2");
        Button btnParallel = createGradientButton("⚡ Run Parallel", "#f093fb", "#f5576c");
        Button btnCompare = createGradientButton("📈 Compare Charts", "#4facfe", "#00f2fe");

        HBox buttons = new HBox(15, btnSequential, btnParallel, btnCompare);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(20));

        // Layout رئيسي
        HBox topPane = new HBox(20, canvasBox, resultPane);
        topPane.setAlignment(Pos.CENTER);
        
        VBox mainContent = new VBox(20, inputPane, topPane, buttons);
        mainContent.setPadding(new Insets(25));
        mainContent.setAlignment(Pos.TOP_CENTER);

        // Root مع خلفية جميلة
        StackPane root = new StackPane(mainContent);
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #ffecd2 0%, #fcb69f 100%);");

        // Button Actions
        btnSequential.setOnAction(e -> runSequential(gc));
        btnParallel.setOnAction(e -> runParallel(gc));
        btnCompare.setOnAction(e -> showComparisonCharts());

        Scene scene = new Scene(root, 1400, 850);
        primaryStage.setScene(scene);
        primaryStage.setTitle("🎯 Monte Carlo π Estimation");
        primaryStage.show();
    }

    private Label createTitleLabel(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        lbl.setTextFill(Color.web("#333"));
        return lbl;
    }

    private Label createSectionLabel(String text, String color) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        lbl.setTextFill(Color.web(color));
        return lbl;
    }

    private Label createWhiteLabel(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        lbl.setTextFill(Color.WHITE);
        return lbl;
    }

    private TextField createStyledTextField(String text){
        TextField tf = new TextField(text);
        tf.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        tf.setStyle("-fx-background-color: white; -fx-border-color: rgba(255,255,255,0.8); -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        return tf;
    }

    private Label createResultLabel(String text, String color){
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        lbl.setTextFill(Color.web(color));
        return lbl;
    }

    private Button createGradientButton(String text, String color1, String color2){
        Button btn = new Button(text);
        btn.setStyle(String.format(
            "-fx-background-color: linear-gradient(to right, %s, %s); " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 14px; " +
            "-fx-background-radius: 25; " +
            "-fx-padding: 12 30 12 30; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 3);",
            color1, color2
        ));
        
        btn.setOnMouseEntered(e -> btn.setStyle(btn.getStyle() + "-fx-scale-x: 1.05; -fx-scale-y: 1.05;"));
        btn.setOnMouseExited(e -> btn.setStyle(btn.getStyle() + "-fx-scale-x: 1; -fx-scale-y: 1;"));
        
        return btn;
    }

    private void drawCanvas(GraphicsContext gc) {
        // خلفية متدرجة
        gc.setFill(Color.web("#f7f8fc"));
        gc.fillRect(0,0,canvasSize,canvasSize);
        
        // إطار المربع الخارجي
        gc.setStroke(Color.web("#667eea"));
        gc.setLineWidth(3);
        gc.strokeRect(0,0,canvasSize,canvasSize);
        
        // الدائرة - المركز في نص المربع، نصف القطر = نصف الـ canvas
        double centerX = canvasSize / 2.0;
        double centerY = canvasSize / 2.0;
        double radius = canvasSize / 2.0;
        
        gc.setStroke(Color.web("#f093fb"));
        gc.setLineWidth(3);
        gc.strokeOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
    }

    private void drawPoints(GraphicsContext gc, long N) {
        gc.clearRect(0,0,canvasSize,canvasSize);
        drawCanvas(gc);
        
        int maxDraw = 8000; // رسم عينة أكبر
        
        double centerX = canvasSize / 2.0;
        double centerY = canvasSize / 2.0;
        double radius = canvasSize / 2.0;
        
        for (int i=0;i<Math.min(N,maxDraw);i++){
            // النقط العشوائية من 0 إلى 1
            double x = ThreadLocalRandom.current().nextDouble();
            double y = ThreadLocalRandom.current().nextDouble();
            
            // تحويلها لإحداثيات الـ Canvas
            // نضربها في canvasSize عشان تملأ كل المربع
            double canvasX = x * canvasSize;
            double canvasY = y * canvasSize;
            
            // حساب المسافة من مركز الدائرة
            double dx = canvasX - centerX;
            double dy = canvasY - centerY;
            double distance = Math.sqrt(dx * dx + dy * dy);
            
            // لو المسافة أقل من أو يساوي نصف القطر = جوا الدايرة
            if (distance <= radius) {
                gc.setFill(Color.rgb(67, 233, 123, 0.6)); // أخضر نعناعي - جوا ✅
            } else {
                gc.setFill(Color.rgb(245, 87, 108, 0.5)); // أحمر وردي - برة ❌
            }
            
            gc.fillOval(canvasX - 1.5, canvasY - 1.5, 3, 3);
        }
    }

    private void runSequential(GraphicsContext gc) {
        long N = Long.parseLong(tfPoints.getText());
        drawPoints(gc,N);
        config = new SimulationConfig(N, Integer.parseInt(tfTasks.getText()), Integer.parseInt(tfThreads.getText()));

        SequentialPiEstimator estimator = new SequentialPiEstimator();
        seqResult = PiExperimentRunner.runExperiment(estimator, config);

        lblSeqPi.setText("Sequential π: " + String.format("%.8f",seqResult.piEstimate));
        lblSeqError.setText("Error: " + String.format("%.8f",seqResult.error));
        lblSeqTime.setText("Time (ms): " + String.format("%.2f",seqResult.timeMs));
    }

    private void runParallel(GraphicsContext gc) {
        long N = Long.parseLong(tfPoints.getText());
        drawPoints(gc,N);
        config = new SimulationConfig(N, Integer.parseInt(tfTasks.getText()), Integer.parseInt(tfThreads.getText()));

        ParallelPiEstimator estimator = new ParallelPiEstimator();
        parResult = PiExperimentRunner.runExperiment(estimator, config);

        lblParPi.setText("Parallel π: " + String.format("%.8f",parResult.piEstimate));
        lblParError.setText("Error: " + String.format("%.8f",parResult.error));
        lblParTime.setText("Time (ms): " + String.format("%.2f",parResult.timeMs));

        if (seqResult!=null){
            double speedup = seqResult.timeMs / parResult.timeMs;
            double efficiency = (speedup / config.getNumThreads()) * 100;
            lblSpeedup.setText("Speedup: " + String.format("%.2fx faster 🚀",speedup));
            lblEfficiency.setText("Efficiency: " + String.format("%.2f%%",efficiency));
        }
    }

    private void showComparisonCharts() {
        if (seqResult==null || parResult==null) return;

        Stage stage = new Stage();
        stage.setTitle("📊 Performance Comparison");

        // Speedup chart
        BarChart<String,Number> chartSpeedup = createStyledBarChart("Speedup Comparison","Method","Speedup Factor", "#4facfe");
        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        series1.getData().add(new XYChart.Data<>("Sequential",1.0));
        series1.getData().add(new XYChart.Data<>("Parallel", seqResult.timeMs / parResult.timeMs));
        chartSpeedup.getData().add(series1);

        // Error chart
        BarChart<String,Number> chartError = createStyledBarChart("Error Comparison","Method","Absolute Error", "#f093fb");
        XYChart.Series<String, Number> series3 = new XYChart.Series<>();
        series3.getData().add(new XYChart.Data<>("Sequential", seqResult.error));
        series3.getData().add(new XYChart.Data<>("Parallel", parResult.error));
        chartError.getData().add(series3);

        // Time chart
        BarChart<String,Number> chartTime = createStyledBarChart("Execution Time","Method","Time (ms)", "#43e97b");
        XYChart.Series<String, Number> series4 = new XYChart.Series<>();
        series4.getData().add(new XYChart.Data<>("Sequential", seqResult.timeMs));
        series4.getData().add(new XYChart.Data<>("Parallel", parResult.timeMs));
        chartTime.getData().add(series4);

        HBox chartsRow = new HBox(20, chartSpeedup, chartError, chartTime);
        chartsRow.setAlignment(Pos.CENTER);
        
        VBox vbox = new VBox(20, chartsRow);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: linear-gradient(to bottom right, #ffecd2 0%, #fcb69f 100%);");
        
        Scene scene = new Scene(vbox,1400,500);
        stage.setScene(scene);
        stage.show();
    }

    private BarChart<String,Number> createStyledBarChart(String title, String xLabel, String yLabel, String color){
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel(xLabel);
        yAxis.setLabel(yLabel);
        
        BarChart<String,Number> chart = new BarChart<>(xAxis,yAxis);
        chart.setTitle(title);
        chart.setLegendVisible(false);
        chart.setStyle("-fx-background-color: white; -fx-background-radius: 15;");
        
        return chart;
    }

    public static void main(String[] args) {
        launch(args);
    }
}


