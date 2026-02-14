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
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.effect.DropShadow;
import javafx.stage.Stage;

import java.util.concurrent.ThreadLocalRandom;

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

public class EnhancedMonteCarloGUI extends Application {

    private final int canvasSize = 410;
    
  
    private Canvas piCanvas;
    private TextField tfPiPoints, tfPiTasks, tfPiThreads;
    private Label lblPiSeqEstimate, lblPiSeqError, lblPiSeqTime;
    private Label lblPiParEstimate, lblPiParError, lblPiParTime, lblPiSpeedup, lblPiEfficiency;
    private PiExperimentRunner.Result piSeqResult, piParResult;
    
   
    private Canvas eulerCanvas;
    private TextField tfEulerPoints, tfEulerTasks, tfEulerThreads;
    private Label lblEulerSeqEstimate, lblEulerSeqError, lblEulerSeqTime;
    private Label lblEulerParEstimate, lblEulerParError, lblEulerParTime, lblEulerSpeedup, lblEulerEfficiency;
    private EulerExperimentRunner.Result eulerSeqResult, eulerParResult;
    
    
    @Override
    public void start(Stage primaryStage) {
        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-background-color: transparent;");
        
        Tab piTab = new Tab("🔵 π (Pi) Estimation");
        piTab.setClosable(false);
        piTab.setContent(createPiTab());
        
        Tab eulerTab = new Tab("🟢 e (Euler) Estimation");
        eulerTab.setClosable(false);
        eulerTab.setContent(createEulerTab());
        tabPane.getTabs().addAll(piTab, eulerTab);
        StackPane root = new StackPane(tabPane);
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #ffecd2 0%, #fcb69f 100%);");
        
        Scene scene = new Scene(root, 1400, 800);
        primaryStage.setScene(scene);
        primaryStage.setTitle("🎯 Monte Carlo Estimator - π & e");
        primaryStage.show();
    }
    
  
    private VBox createPiTab() {
        
        piCanvas = new Canvas(canvasSize, canvasSize);
        GraphicsContext gc = piCanvas.getGraphicsContext2D();
        drawPiCanvas(gc);
        
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.3));
        shadow.setRadius(15);
        piCanvas.setEffect(shadow);

        VBox canvasBox = new VBox(piCanvas);
        canvasBox.setAlignment(Pos.CENTER);
        canvasBox.setPadding(new Insets(20));
        canvasBox.setStyle("-fx-background-color: white; -fx-background-radius: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 15, 0, 0, 5);");

        
        Label titleInput = createTitleLabel("⚙ π Simulation Parameters");
        
        tfPiPoints = createStyledTextField("10000000");
        tfPiTasks = createStyledTextField("8");
        tfPiThreads = createStyledTextField("4");

        GridPane inputPane = new GridPane();
        inputPane.setPadding(new Insets(20));
        inputPane.setHgap(15);
        inputPane.setVgap(12);
        inputPane.setStyle("-fx-background-color: linear-gradient(to bottom, #667eea 0%, #764ba2 100%); -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 3);");
        
        inputPane.addRow(0, createWhiteLabel("Total Points (N):"), tfPiPoints);
        inputPane.addRow(1, createWhiteLabel("Number of Tasks:"), tfPiTasks);
        inputPane.addRow(2, createWhiteLabel("Number of Threads:"), tfPiThreads);

        
        Label titleResults = createTitleLabel("📊 π Results");
        
        lblPiSeqEstimate = createResultLabel("Sequential π: -", "#667eea");
        lblPiSeqError = createResultLabel("Error: -", "#667eea");
        lblPiSeqTime = createResultLabel("Time (ms): -", "#667eea");

        VBox seqBox = new VBox(8, 
            createSectionLabel("Sequential Estimation", "#667eea"),
            lblPiSeqEstimate, lblPiSeqError, lblPiSeqTime
        );
        seqBox.setPadding(new Insets(15));
        seqBox.setStyle("-fx-background-color: rgba(102, 126, 234, 0.1); -fx-background-radius: 12; -fx-border-color: #667eea; -fx-border-width: 2; -fx-border-radius: 12;");

        lblPiParEstimate = createResultLabel("Parallel π: -", "#f093fb");
        lblPiParError = createResultLabel("Error: -", "#f093fb");
        lblPiParTime = createResultLabel("Time (ms): -", "#f093fb");
        lblPiSpeedup = createResultLabel("Speedup: -", "#4facfe");
        lblPiEfficiency = createResultLabel("Efficiency: -", "#43e97b");

        VBox parBox = new VBox(8, 
            createSectionLabel("Parallel Estimation", "#f093fb"),
            lblPiParEstimate, lblPiParError, lblPiParTime, lblPiSpeedup, lblPiEfficiency
        );
        parBox.setPadding(new Insets(15));
        parBox.setStyle("-fx-background-color: rgba(240, 147, 251, 0.1); -fx-background-radius: 12; -fx-border-color: #f093fb; -fx-border-width: 2; -fx-border-radius: 12;");

        VBox resultPane = new VBox(15, titleResults, seqBox, parBox);
        resultPane.setPadding(new Insets(20));
        resultPane.setStyle("-fx-background-color: white; -fx-background-radius: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 15, 0, 0, 5);");

        Button btnPiSeq = createGradientButton("🔄 Run Sequential", "#667eea", "#764ba2");
        Button btnPiPar = createGradientButton("⚡ Run Parallel", "#f093fb", "#f5576c");
        Button btnPiCompare = createGradientButton("📈 Compare Charts", "#4facfe", "#00f2fe");

        HBox buttons = new HBox(15, btnPiSeq, btnPiPar, btnPiCompare);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(20));

        btnPiSeq.setOnAction(e -> runPiSequential(gc));
        btnPiPar.setOnAction(e -> runPiParallel(gc));
        btnPiCompare.setOnAction(e -> showPiComparisonCharts());

        HBox topPane = new HBox(20, canvasBox, resultPane);
        topPane.setAlignment(Pos.CENTER);
        
        VBox mainContent = new VBox(20, inputPane, topPane, buttons);
        mainContent.setPadding(new Insets(25));
        mainContent.setAlignment(Pos.TOP_CENTER);
        
        return mainContent;
        
    }

    
    private VBox createEulerTab() {
        
        eulerCanvas = new Canvas(canvasSize, canvasSize);
        GraphicsContext gc = eulerCanvas.getGraphicsContext2D();
        drawEulerCanvas(gc);
        
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.3));
        shadow.setRadius(15);
        eulerCanvas.setEffect(shadow);

        VBox canvasBox = new VBox(eulerCanvas);
        canvasBox.setAlignment(Pos.CENTER);
        canvasBox.setPadding(new Insets(20));
        canvasBox.setStyle("-fx-background-color: white; -fx-background-radius: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 15, 0, 0, 5);");

        
        Label titleInput = createTitleLabel("⚙ e Simulation Parameters");
        
        tfEulerPoints = createStyledTextField("10000000");
        tfEulerTasks = createStyledTextField("8");
        tfEulerThreads = createStyledTextField("4");

        GridPane inputPane = new GridPane();
        inputPane.setPadding(new Insets(20));
        inputPane.setHgap(15);
        inputPane.setVgap(12);
        inputPane.setStyle("-fx-background-color: linear-gradient(to bottom, #43e97b 0%, #38f9d7 100%); -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 3);");
        
        inputPane.addRow(0, createWhiteLabel("Total Points (N):"), tfEulerPoints);
        inputPane.addRow(1, createWhiteLabel("Number of Tasks:"), tfEulerTasks);
        inputPane.addRow(2, createWhiteLabel("Number of Threads:"), tfEulerThreads);

       
        Label titleResults = createTitleLabel("📊 e Results");
        
        lblEulerSeqEstimate = createResultLabel("Sequential e: -", "#43e97b");
        lblEulerSeqError = createResultLabel("Error: -", "#43e97b");
        lblEulerSeqTime = createResultLabel("Time (ms): -", "#43e97b");

        VBox seqBox = new VBox(8, 
            createSectionLabel("Sequential Estimation", "#43e97b"),
            lblEulerSeqEstimate, lblEulerSeqError, lblEulerSeqTime
        );
        seqBox.setPadding(new Insets(15));
        seqBox.setStyle("-fx-background-color: rgba(67, 233, 123, 0.1); -fx-background-radius: 12; -fx-border-color: #43e97b; -fx-border-width: 2; -fx-border-radius: 12;");

        lblEulerParEstimate = createResultLabel("Parallel e: -", "#fa709a");
        lblEulerParError = createResultLabel("Error: -", "#fa709a");
        lblEulerParTime = createResultLabel("Time (ms): -", "#fa709a");
        lblEulerSpeedup = createResultLabel("Speedup: -", "#4facfe");
        lblEulerEfficiency = createResultLabel("Efficiency: -", "#fee140");

        VBox parBox = new VBox(8, 
            createSectionLabel("Parallel Estimation", "#fa709a"),
            lblEulerParEstimate, lblEulerParError, lblEulerParTime, lblEulerSpeedup, lblEulerEfficiency
        );
        parBox.setPadding(new Insets(15));
        parBox.setStyle("-fx-background-color: rgba(250, 112, 154, 0.1); -fx-background-radius: 12; -fx-border-color: #fa709a; -fx-border-width: 2; -fx-border-radius: 12;");

        VBox resultPane = new VBox(15, titleResults, seqBox, parBox);
        resultPane.setPadding(new Insets(20));
        resultPane.setStyle("-fx-background-color: white; -fx-background-radius: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 15, 0, 0, 5);");

        
        Button btnEulerSeq = createGradientButton("🔄 Run Sequential", "#43e97b", "#38f9d7");
        Button btnEulerPar = createGradientButton("⚡ Run Parallel", "#fa709a", "#fee140");
        Button btnEulerCompare = createGradientButton("📈 Compare Charts", "#4facfe", "#00f2fe");

        HBox buttons = new HBox(15, btnEulerSeq, btnEulerPar, btnEulerCompare);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(20));

        btnEulerSeq.setOnAction(e -> runEulerSequential(gc));
        btnEulerPar.setOnAction(e -> runEulerParallel(gc));
        btnEulerCompare.setOnAction(e -> showEulerComparisonCharts());

        HBox topPane = new HBox(20, canvasBox, resultPane);
        topPane.setAlignment(Pos.CENTER);
        
        VBox mainContent = new VBox(15, inputPane, topPane, buttons);
        mainContent.setPadding(new Insets(20));
        mainContent.setAlignment(Pos.TOP_CENTER);
        
        return mainContent;
    }

    
    private void drawPiCanvas(GraphicsContext gc) {
        gc.setFill(Color.web("#f7f8fc"));
        gc.fillRect(0,0,canvasSize,canvasSize);
        gc.setStroke(Color.web("#667eea"));
        gc.setLineWidth(3);
        gc.strokeRect(0,0,canvasSize,canvasSize);
        gc.setStroke(Color.web("#f093fb"));
        gc.setLineWidth(3);
        gc.strokeOval(0,0,canvasSize,canvasSize);
    }

    private void drawPiPoints(GraphicsContext gc, long N) {
        gc.clearRect(0,0,canvasSize,canvasSize);
        drawPiCanvas(gc);
        
        int maxDraw = 8000;
        for (int i=0;i<Math.min(N,maxDraw);i++){
            double x = ThreadLocalRandom.current().nextDouble();
            double y = ThreadLocalRandom.current().nextDouble();
            
            if (x*x + y*y <=1) {
                gc.setFill(Color.rgb(67, 233, 123, 0.6));
            } else {
                gc.setFill(Color.rgb(245, 87, 108, 0.5));
            }
            gc.fillOval(x*canvasSize, y*canvasSize, 3, 3);
        }
    }

    private void runPiSequential(GraphicsContext gc) {
        long N = Long.parseLong(tfPiPoints.getText());
        drawPiPoints(gc,N);
        SimulationConfig config = new SimulationConfig(N, Integer.parseInt(tfPiTasks.getText()), Integer.parseInt(tfPiThreads.getText()));

        SequentialPiEstimator estimator = new SequentialPiEstimator();
        piSeqResult = PiExperimentRunner.runExperiment(estimator, config);

        lblPiSeqEstimate.setText("Sequential π: " + String.format("%.8f",piSeqResult.piEstimate));
        lblPiSeqError.setText("Error: " + String.format("%.8f",piSeqResult.error));
        lblPiSeqTime.setText("Time (ms): " + String.format("%.2f",piSeqResult.timeMs));
    }
   

    private void runPiParallel(GraphicsContext gc) {
        long N = Long.parseLong(tfPiPoints.getText());
        drawPiPoints(gc,N);
        SimulationConfig config = new SimulationConfig(N, Integer.parseInt(tfPiTasks.getText()), Integer.parseInt(tfPiThreads.getText()));

        ParallelPiEstimator estimator = new ParallelPiEstimator();
        piParResult = PiExperimentRunner.runExperiment(estimator, config);

        lblPiParEstimate.setText("Parallel π: " + String.format("%.8f",piParResult.piEstimate));
        lblPiParError.setText("Error: " + String.format("%.8f",piParResult.error));
        lblPiParTime.setText("Time (ms): " + String.format("%.2f",piParResult.timeMs));

        if (piSeqResult!=null){
            double speedup = piSeqResult.timeMs / piParResult.timeMs;
            double efficiency = (speedup / Integer.parseInt(tfPiThreads.getText())) * 100;
            lblPiSpeedup.setText("Speedup: " + String.format("%.2fx faster 🚀",speedup));
            lblPiEfficiency.setText("Efficiency: " + String.format("%.2f%%",efficiency));
        }
    }

    
    private void showPiComparisonCharts() {
        if (piSeqResult==null || piParResult==null) return;
        showComparisonCharts("π", piSeqResult.piEstimate, piSeqResult.error, piSeqResult.timeMs, 
                            piParResult.piEstimate, piParResult.error, piParResult.timeMs);
    }

        private void drawEulerCanvas(GraphicsContext gc) {
        gc.setFill(Color.web("#f0fff4"));
        gc.fillRect(0,0,canvasSize,canvasSize);
        
        gc.setStroke(Color.web("#43e97b"));
        gc.setLineWidth(3);
        gc.strokeRect(0,0,canvasSize,canvasSize);
        gc.setFill(Color.web("#43e97b"));
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        gc.fillText("e ≈ 2.71828", canvasSize/2 - 60, canvasSize/2);
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        gc.fillText("Random Sum Method", canvasSize/2 - 80, canvasSize/2 + 30);
    }

    private void runEulerSequential(GraphicsContext gc) {
        long N = Long.parseLong(tfEulerPoints.getText());
        SimulationConfig config = new SimulationConfig(N, Integer.parseInt(tfEulerTasks.getText()), Integer.parseInt(tfEulerThreads.getText()));

        SequentialEulerEstimator estimator = new SequentialEulerEstimator();
        eulerSeqResult = EulerExperimentRunner.runExperiment(estimator, config);

        lblEulerSeqEstimate.setText("Sequential e: " + String.format("%.8f",eulerSeqResult.eEstimate));
        lblEulerSeqError.setText("Error: " + String.format("%.8f",eulerSeqResult.error));
        lblEulerSeqTime.setText("Time (ms): " + String.format("%.2f",eulerSeqResult.timeMs));
    }

    private void runEulerParallel(GraphicsContext gc) {
        long N = Long.parseLong(tfEulerPoints.getText());
        SimulationConfig config = new SimulationConfig(N, Integer.parseInt(tfEulerTasks.getText()), Integer.parseInt(tfEulerThreads.getText()));

        ParallelEulerEstimator estimator = new ParallelEulerEstimator();
        eulerParResult = EulerExperimentRunner.runExperiment(estimator, config);

        lblEulerParEstimate.setText("Parallel e: " + String.format("%.8f",eulerParResult.eEstimate));
        lblEulerParError.setText("Error: " + String.format("%.8f",eulerParResult.error));
        lblEulerParTime.setText("Time (ms): " + String.format("%.2f",eulerParResult.timeMs));

        if (eulerSeqResult!=null){
            double speedup = eulerSeqResult.timeMs / eulerParResult.timeMs;
            double efficiency = (speedup / Integer.parseInt(tfEulerThreads.getText())) * 100;
            lblEulerSpeedup.setText("Speedup: " + String.format("%.2fx faster 🚀",speedup));
            lblEulerEfficiency.setText("Efficiency: " + String.format("%.2f%%",efficiency));
        }
    }

    private void showEulerComparisonCharts() {
        if (eulerSeqResult==null || eulerParResult==null) return;
        showComparisonCharts("e", eulerSeqResult.eEstimate, eulerSeqResult.error, eulerSeqResult.timeMs, 
                            eulerParResult.eEstimate, eulerParResult.error, eulerParResult.timeMs);
    }

    
    private void showComparisonCharts(String constantName, double seqEst, double seqErr, double seqTime,
                                     double parEst, double parErr, double parTime) {
        Stage stage = new Stage();
        stage.setTitle("📊 " + constantName + " Performance Comparison");

        BarChart<String,Number> chartSpeedup = createStyledBarChart("Speedup Comparison","Method","Speedup Factor");
        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        series1.getData().add(new XYChart.Data<>("Sequential",1.0));
        series1.getData().add(new XYChart.Data<>("Parallel", seqTime / parTime));
        chartSpeedup.getData().add(series1);

        BarChart<String,Number> chartError = createStyledBarChart("Error Comparison","Method","Absolute Error");
        XYChart.Series<String, Number> series2 = new XYChart.Series<>();
        series2.getData().add(new XYChart.Data<>("Sequential", seqErr));
        series2.getData().add(new XYChart.Data<>("Parallel", parErr));
        chartError.getData().add(series2);

        BarChart<String,Number> chartTime = createStyledBarChart("Execution Time","Method","Time (ms)");
        XYChart.Series<String, Number> series3 = new XYChart.Series<>();
        series3.getData().add(new XYChart.Data<>("Sequential", seqTime));
        series3.getData().add(new XYChart.Data<>("Parallel", parTime));
        chartTime.getData().add(series3);

        HBox chartsRow = new HBox(20, chartSpeedup, chartError, chartTime);
        chartsRow.setAlignment(Pos.CENTER);
        
        VBox vbox = new VBox(20, chartsRow);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: linear-gradient(to bottom right, #ffecd2 0%, #fcb69f 100%);");
        
        Scene scene = new Scene(vbox,1400,500);
        stage.setScene(scene);
        stage.show();
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
        tf.setStyle("-fx-background-color: white; -fx-border-color: rgba(255,255,255,0.8); -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10;");
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
            "-fx-cursor: hand;",
            color1, color2
        ));
        return btn;
    }

    private BarChart<String,Number> createStyledBarChart(String title, String xLabel, String yLabel){
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

class EulerExperimentRunner {
    public static class Result {
        double eEstimate;
        double error;
        double timeMs;
        
        public Result(double eEstimate, double error, double timeMs) {
            this.eEstimate = eEstimate;
            this.error = error;
            this.timeMs = timeMs;
        }
    }
    
    public static Result runExperiment(EulerEstimator estimator, SimulationConfig config) {
        long startTime = System.nanoTime();
        double estimate = estimator.estimateEuler(config);
        long endTime = System.nanoTime();
        
        double timeMs = (endTime - startTime) / 1_000_000.0;
        double error = Math.abs(estimate - Math.E);
        
        return new Result(estimate, error, timeMs);
    }
}