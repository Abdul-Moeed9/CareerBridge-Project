package ui;

import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.stage.*;
import controller.AdminController;
import domain.TrendingSkill;
import java.util.List;

public class TrendingSkillsScreen extends Application {

    private BarChart<String, Number> skillsBarChart;
    private TableView<TrendingSkill> skillsTable;
    private Button refreshButton;

    private Stage primaryStage;
    private AdminController adminController;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        this.adminController = new AdminController();
        initialize();
    }

    public void initialize() {
        primaryStage.setTitle("CareerBridge - Trending Skills");
        primaryStage.setResizable(true);
        Scene scene = new Scene(buildRoot(), 1280, 800);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint("");
        primaryStage.setFullScreenExitKeyCombination(javafx.scene.input.KeyCombination.NO_MATCH);
        primaryStage.show();
        loadTrendingSkills();
    }

    private Parent buildRoot() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #111111;");
        root.setTop(buildTopBar());
        root.setCenter(buildCenterPanel());
        root.setRight(buildRightPanel());
        return root;
    }

    private HBox buildTopBar() {
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(14, 28, 14, 28));
        topBar.setSpacing(14);
        topBar.setStyle(
            "-fx-background-color: #1e1e1e;" +
            "-fx-border-color: #2e2e2e;" +
            "-fx-border-width: 0 0 1 0;"
        );

        Label rocketIcon = new Label("\uD83D\uDE80");
        rocketIcon.setStyle("-fx-font-size: 26px; -fx-text-fill: #FFD700;");

        Text brand = new Text("CareerBridge");
        brand.setStyle(
            "-fx-font-family: 'Segoe UI', 'Helvetica', Arial, sans-serif;" +
            "-fx-font-size: 22px;" +
            "-fx-font-weight: bold;" +
            "-fx-fill: #ffffff;"
        );

        Label separator = new Label("|");
        separator.setStyle("-fx-text-fill: #2e2e2e; -fx-font-size: 22px;");

        Label screenTitle = new Label("Trending Skills Analytics");
        screenTitle.setStyle(
            "-fx-text-fill: #888888;" +
            "-fx-font-size: 14px;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        refreshButton = new Button("Refresh Data");
        refreshButton.setPrefHeight(36);
        applyPrimaryButtonStyle(refreshButton, false);
        refreshButton.setOnMouseEntered(e -> applyPrimaryButtonStyle(refreshButton, true));
        refreshButton.setOnMouseExited(e -> applyPrimaryButtonStyle(refreshButton, false));
        refreshButton.setOnAction(e -> handleRefresh());

        Button backButton = new Button("Back to Dashboard");
        backButton.setPrefHeight(36);
        backButton.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #666666;" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: #2e2e2e;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 8;" +
            "-fx-cursor: hand;"
        );
        backButton.setOnMouseEntered(e -> backButton.setStyle(
            "-fx-background-color: #1e1e1e;" +
            "-fx-text-fill: #aaaaaa;" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: #444444;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 8;" +
            "-fx-cursor: hand;"
        ));
        backButton.setOnMouseExited(e -> backButton.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #666666;" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: #2e2e2e;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 8;" +
            "-fx-cursor: hand;"
        ));
        backButton.setOnAction(e -> {
            AdminDashboard dashboard = new AdminDashboard();
            dashboard.start(primaryStage);
        });

        topBar.getChildren().addAll(rocketIcon, brand, separator, screenTitle, spacer, refreshButton, backButton);
        return topBar;
    }

    private VBox buildCenterPanel() {
        VBox centerPanel = new VBox(16);
        centerPanel.setPadding(new Insets(20, 24, 20, 24));
        centerPanel.setStyle("-fx-background-color: #111111;");

        Label chartHeading = new Label("Skill Demand Overview");
        chartHeading.setStyle(
            "-fx-text-fill: #ffffff;" +
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Skill");
        xAxis.setTickLabelFill(Color.web("#888888"));
        xAxis.setTickLabelRotation(35);

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Frequency");
        yAxis.setTickLabelFill(Color.web("#888888"));

        skillsBarChart = new BarChart<>(xAxis, yAxis);
        skillsBarChart.setTitle("Top Trending Skills by Frequency");
        skillsBarChart.setLegendVisible(false);
        skillsBarChart.setPrefHeight(350);
        skillsBarChart.setAnimated(true);
        skillsBarChart.setStyle("-fx-background-color: transparent;");
        skillsBarChart.setCategoryGap(8);
        skillsBarChart.setBarGap(2);
        VBox.setVgrow(skillsBarChart, Priority.ALWAYS);

        VBox chartCard = new VBox(12);
        chartCard.setPadding(new Insets(16));
        chartCard.setStyle(
            "-fx-background-color: #1e1e1e;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #2e2e2e;" +
            "-fx-border-radius: 12;" +
            "-fx-border-width: 1;"
        );
        VBox.setVgrow(chartCard, Priority.ALWAYS);
        chartCard.getChildren().addAll(chartHeading, skillsBarChart);

        centerPanel.getChildren().add(chartCard);
        return centerPanel;
    }

    @SuppressWarnings("unchecked")
    private VBox buildRightPanel() {
        VBox rightPanel = new VBox(16);
        rightPanel.setPrefWidth(380);
        rightPanel.setPadding(new Insets(20));
        rightPanel.setStyle(
            "-fx-background-color: #1e1e1e;" +
            "-fx-border-color: #2e2e2e;" +
            "-fx-border-width: 0 0 0 1;"
        );

        Label heading = new Label("SKILLS TABLE");
        heading.setStyle(
            "-fx-text-fill: #888888;" +
            "-fx-font-size: 10px;" +
            "-fx-font-weight: bold;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );

        skillsTable = new TableView<>();
        skillsTable.setStyle(
            "-fx-background-color: #1e1e1e;" +
            "-fx-border-color: #2e2e2e;" +
            "-fx-table-cell-border-color: #2a2a2a;"
        );
        skillsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        skillsTable.setPlaceholder(new Label("No trending skills data"));
        VBox.setVgrow(skillsTable, Priority.ALWAYS);

        TableColumn<TrendingSkill, Integer> rankCol = new TableColumn<>("#");
        rankCol.setPrefWidth(40);
        rankCol.setCellFactory(col -> new TableCell<TrendingSkill, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.valueOf(getIndex() + 1));
                    setStyle("-fx-text-fill: #00d4ff; -fx-font-weight: bold;");
                }
            }
        });

        TableColumn<TrendingSkill, String> nameCol = new TableColumn<>("Skill Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("skillName"));
        nameCol.setPrefWidth(160);

        TableColumn<TrendingSkill, Integer> freqCol = new TableColumn<>("Frequency");
        freqCol.setCellValueFactory(new PropertyValueFactory<>("frequency"));
        freqCol.setPrefWidth(80);
        freqCol.setCellFactory(col -> new TableCell<TrendingSkill, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox barContainer = new HBox(8);
                    barContainer.setAlignment(Pos.CENTER_LEFT);

                    Label valueLabel = new Label(String.valueOf(item));
                    valueLabel.setMinWidth(30);
                    valueLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 11px;");

                    double maxFreq = 150.0;
                    double ratio = Math.min(item / maxFreq, 1.0);

                    Region bar = new Region();
                    bar.setPrefHeight(8);
                    bar.setPrefWidth(ratio * 80);
                    bar.setMaxWidth(80);
                    bar.setStyle(
                        "-fx-background-color: linear-gradient(to right, #00d4ff, #00b8d9);" +
                        "-fx-background-radius: 4;"
                    );

                    barContainer.getChildren().addAll(valueLabel, bar);
                    setGraphic(barContainer);
                    setText(null);
                }
            }
        });

        TableColumn<TrendingSkill, java.util.Date> dateCol = new TableColumn<>("Calculated");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("calculatedDate"));
        dateCol.setPrefWidth(90);
        dateCol.setCellFactory(col -> new TableCell<TrendingSkill, java.util.Date>() {
            @Override
            protected void updateItem(java.util.Date item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMM dd, yyyy");
                    setText(sdf.format(item));
                    setStyle("-fx-text-fill: #666666; -fx-font-size: 10px;");
                }
            }
        });

        skillsTable.getColumns().addAll(rankCol, nameCol, freqCol, dateCol);

        VBox summaryCard = new VBox(10);
        summaryCard.setPadding(new Insets(16));
        summaryCard.setStyle(
            "-fx-background-color: #252525;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #2e2e2e;" +
            "-fx-border-radius: 12;" +
            "-fx-border-width: 1;"
        );

        Label summaryHeading = new Label("SUMMARY");
        summaryHeading.setStyle(
            "-fx-text-fill: #888888;" +
            "-fx-font-size: 10px;" +
            "-fx-font-weight: bold;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );

        Label totalSkillsLabel = new Label("Total Skills Tracked: --");
        totalSkillsLabel.setStyle(
            "-fx-text-fill: #cccccc;" +
            "-fx-font-size: 13px;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );

        Label topSkillLabel = new Label("Most In-Demand: --");
        topSkillLabel.setStyle(
            "-fx-text-fill: #00d4ff;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );

        summaryCard.getChildren().addAll(summaryHeading, totalSkillsLabel, topSkillLabel);

        rightPanel.getChildren().addAll(heading, skillsTable, summaryCard);
        return rightPanel;
    }

    public void loadTrendingSkills() {
        try {
            List<TrendingSkill> skills = adminController.getTrendingSkills();
            renderChart(skills);

            skillsTable.getItems().clear();
            skillsTable.getItems().addAll(skills);

            VBox rightPanel = (VBox) ((BorderPane) primaryStage.getScene().getRoot()).getRight();
            if (rightPanel.getChildren().size() >= 3) {
                Node summaryNode = rightPanel.getChildren().get(2);
                if (summaryNode instanceof VBox) {
                    VBox summaryCard = (VBox) summaryNode;
                    if (summaryCard.getChildren().size() >= 3) {
                        Label totalLabel = (Label) summaryCard.getChildren().get(1);
                        totalLabel.setText("Total Skills Tracked: " + skills.size());

                        Label topLabel = (Label) summaryCard.getChildren().get(2);
                        if (!skills.isEmpty()) {
                            topLabel.setText("Most In-Demand: " + skills.get(0).getSkillName() +
                                " (" + skills.get(0).getFrequency() + ")");
                        } else {
                            topLabel.setText("Most In-Demand: --");
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void renderChart(List<TrendingSkill> skills) {
        skillsBarChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        int count = 0;
        for (TrendingSkill skill : skills) {
            if (count >= 15) break;
            series.getData().add(new XYChart.Data<>(skill.getSkillName(), skill.getFrequency()));
            count++;
        }
        skillsBarChart.getData().add(series);

        for (XYChart.Data<String, Number> data : series.getData()) {
            if (data.getNode() != null) {
                data.getNode().setStyle("-fx-bar-fill: #00d4ff;");
            }
        }
    }

    public void handleRefresh() {
        adminController.refreshTrendingSkills();
        loadTrendingSkills();
    }

    private void applyPrimaryButtonStyle(Button btn, boolean hovered) {
        if (hovered) {
            btn.setStyle(
                "-fx-background-color: linear-gradient(to right, #00b8d9, #0098b3);" +
                "-fx-text-fill: #ffffff;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 9;" +
                "-fx-cursor: hand;"
            );
            DropShadow glow = new DropShadow();
            glow.setColor(Color.web("#00d4ff", 0.45));
            glow.setRadius(16);
            btn.setEffect(glow);
        } else {
            btn.setStyle(
                "-fx-background-color: linear-gradient(to right, #00d4ff, #00b8d9);" +
                "-fx-text-fill: #0a0a0a;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 9;" +
                "-fx-cursor: hand;"
            );
            btn.setEffect(null);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}