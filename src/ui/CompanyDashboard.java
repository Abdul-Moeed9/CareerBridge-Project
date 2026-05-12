package ui;


import javafx.collections.*;
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
import interfaces.IDashboard;
import controller.CompanyController;
import dao.CompanyDAO;
import domain.Application;
import domain.Company;
import domain.Offering;
import domain.User;
import service.AIMatchingService;
import utility.AnalyticsData;
import utility.SessionManager;
import java.util.List;
import java.util.Map;

public class CompanyDashboard extends javafx.application.Application implements IDashboard {

    private Button postOfferingButton;
    private TableView<Offering> offeringsTable;
    private TableView<Application> applicantsTable;
    private VBox analyticsPanel;
    private Label avgCGPALabel;
    private Label totalApplicantsLabel;
    private BarChart<String, Number> matchDistributionChart;

    private Stage primaryStage;
    private CompanyController companyController;
    private int resolvedCompanyID;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        this.companyController = new CompanyController(new AIMatchingService());
        this.resolvedCompanyID = resolveCompanyID();
        initialize();
    }

    private int resolveCompanyID() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) return -1;
        CompanyDAO companyDAO = new CompanyDAO();
        Company company = (Company) companyDAO.findByID(currentUser.getUserID());
        return company != null ? company.getCompanyID() : -1;
    }

    @Override
    public void initialize() {
        primaryStage.setTitle("CareerBridge - Company Dashboard");
        primaryStage.setResizable(true);
        Scene scene = new Scene(buildRoot(), 1280, 800);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint("");
        primaryStage.setFullScreenExitKeyCombination(javafx.scene.input.KeyCombination.NO_MATCH);
        primaryStage.show();
        loadData();
    }

    @Override
    public void loadData() {
        loadOfferings();
        loadAnalytics();
    }

    @Override
    public void refreshView() {
        offeringsTable.getItems().clear();
        applicantsTable.getItems().clear();
        loadData();
    }

    private Parent buildRoot() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1A0E0E;");
        root.setTop(buildTopBar());
        root.setLeft(buildLeftPanel());
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
            "-fx-background-color: #2D1515;" +
            "-fx-border-color: #4A2020;" +
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

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        User currentUser = SessionManager.getInstance().getCurrentUser();
        String userName = currentUser != null ? currentUser.getName() : "Company";

        Label welcomeLabel = new Label("Welcome, " + userName);
        welcomeLabel.setStyle(
            "-fx-text-fill: #D4A0A0;" +
            "-fx-font-size: 13px;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );

        postOfferingButton = new Button("+ Post Offering");
        postOfferingButton.setPrefHeight(36);
        applyPrimaryButtonStyle(postOfferingButton, false);
        postOfferingButton.setOnMouseEntered(e -> applyPrimaryButtonStyle(postOfferingButton, true));
        postOfferingButton.setOnMouseExited(e -> applyPrimaryButtonStyle(postOfferingButton, false));
        postOfferingButton.setOnAction(e -> handlePostOffering());

        Button logoutButton = new Button("Logout");
        logoutButton.setPrefHeight(36);
        logoutButton.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #8A6060;" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: #4A2020;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 8;" +
            "-fx-cursor: hand;"
        );
        logoutButton.setOnMouseEntered(e -> logoutButton.setStyle(
            "-fx-background-color: #3D1A1A;" +
            "-fx-text-fill: #D4A0A0;" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: #4A2020;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 8;" +
            "-fx-cursor: hand;"
        ));
        logoutButton.setOnMouseExited(e -> logoutButton.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #8A6060;" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: #4A2020;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 8;" +
            "-fx-cursor: hand;"
        ));
        logoutButton.setOnAction(e -> {
            SessionManager.getInstance().clearSession();
            new LoginScreen().start(primaryStage);
        });

        topBar.getChildren().addAll(rocketIcon, brand, spacer, welcomeLabel, postOfferingButton, logoutButton);
        return topBar;
    }

    @SuppressWarnings("unchecked")
    private VBox buildLeftPanel() {
        VBox leftPanel = new VBox(16);
        leftPanel.setPadding(new Insets(20, 0, 20, 0));
        leftPanel.setPrefWidth(420);
        leftPanel.setStyle(
            "-fx-background-color: #2D1515;" +
            "-fx-border-color: #4A2020;" +
            "-fx-border-width: 0 1 0 0;"
        );

        Label heading = new Label("YOUR OFFERINGS");
        heading.setStyle(
            "-fx-text-fill: #D4A0A0;" +
            "-fx-font-size: 10px;" +
            "-fx-font-weight: bold;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;" +
            "-fx-padding: 0 0 0 16;"
        );

        offeringsTable = new TableView<>();
        offeringsTable.setStyle(
            "-fx-background-color: #2D1515;" +
            "-fx-border-color: #4A2020;" +
            "-fx-table-cell-border-color: #3D1A1A;"
        );
        offeringsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(offeringsTable, Priority.ALWAYS);
        VBox.setMargin(offeringsTable, new Insets(0, 12, 0, 12));

        TableColumn<Offering, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setPrefWidth(140);

        TableColumn<Offering, String> locationCol = new TableColumn<>("Location");
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        locationCol.setPrefWidth(90);

        TableColumn<Offering, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(70);

        TableColumn<Offering, String> stipendCol = new TableColumn<>("Salary");
        stipendCol.setPrefWidth(120);
        stipendCol.setCellFactory(col -> new javafx.scene.control.TableCell<Offering, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                } else {
                    Offering o = getTableRow().getItem();
                    if (o.getSalaryText() != null && !o.getSalaryText().isEmpty()) {
                        setText(o.getSalaryText());
                    } else if (o.getStipend() > 0) {
                        setText("PKR " + (int) o.getStipend());
                    } else {
                        setText("--");
                    }
                }
            }
        });

        offeringsTable.getColumns().addAll(titleCol, locationCol, statusCol, stipendCol);

        offeringsTable.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> {
            if (nv != null) {
                loadApplicants();
            }
        });

        HBox buttonRow = new HBox(10);
        buttonRow.setPadding(new Insets(0, 12, 0, 12));

        Button viewCandidatesBtn = new Button("View Candidates");
        viewCandidatesBtn.setPrefHeight(36);
        HBox.setHgrow(viewCandidatesBtn, Priority.ALWAYS);
        viewCandidatesBtn.setMaxWidth(Double.MAX_VALUE);
        applySecondaryButtonStyle(viewCandidatesBtn, false);
        viewCandidatesBtn.setOnMouseEntered(e -> applySecondaryButtonStyle(viewCandidatesBtn, true));
        viewCandidatesBtn.setOnMouseExited(e -> applySecondaryButtonStyle(viewCandidatesBtn, false));
        viewCandidatesBtn.setOnAction(e -> handleViewCandidates());

        Button closeOfferingBtn = new Button("Close Offering");
        closeOfferingBtn.setPrefHeight(36);
        HBox.setHgrow(closeOfferingBtn, Priority.ALWAYS);
        closeOfferingBtn.setMaxWidth(Double.MAX_VALUE);
        closeOfferingBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #8A6060;" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: #4A2020;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 8;" +
            "-fx-cursor: hand;"
        );
        closeOfferingBtn.setOnMouseEntered(e -> closeOfferingBtn.setStyle(
            "-fx-background-color: #3D1A1A;" +
            "-fx-text-fill: #D4A0A0;" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: #4A2020;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 8;" +
            "-fx-cursor: hand;"
        ));
        closeOfferingBtn.setOnMouseExited(e -> closeOfferingBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #8A6060;" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: #4A2020;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 8;" +
            "-fx-cursor: hand;"
        ));
        closeOfferingBtn.setOnAction(e -> {
            Offering selected = offeringsTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showDialog("Please select an offering to close.");
                return;
            }
            companyController.closeOffering(selected.getOfferingID());
            refreshView();
        });

        buttonRow.getChildren().addAll(viewCandidatesBtn, closeOfferingBtn);

        leftPanel.getChildren().addAll(heading, offeringsTable, buttonRow);
        return leftPanel;
    }

    @SuppressWarnings("unchecked")
    private VBox buildCenterPanel() {
        VBox centerPanel = new VBox(0);
        centerPanel.setStyle("-fx-background-color: #1A0E0E;");

        Label heading = new Label("Applicants");
        heading.setStyle(
            "-fx-text-fill: #ffffff;" +
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );
        heading.setPadding(new Insets(20, 24, 12, 24));

        applicantsTable = new TableView<>();
        applicantsTable.setStyle(
            "-fx-background-color: #1A0E0E;" +
            "-fx-border-color: #4A2020;" +
            "-fx-table-cell-border-color: #3D1A1A;"
        );
        applicantsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        applicantsTable.setPlaceholder(new Label("Select an offering to view applicants"));
        VBox.setVgrow(applicantsTable, Priority.ALWAYS);
        VBox.setMargin(applicantsTable, new Insets(0, 16, 16, 16));

        TableColumn<domain.Application, Integer> idCol = new TableColumn<>("App ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("applicationID"));
        idCol.setPrefWidth(60);

        TableColumn<domain.Application, Integer> seekerCol = new TableColumn<>("Seeker ID");
        seekerCol.setCellValueFactory(new PropertyValueFactory<>("seekerID"));
        seekerCol.setPrefWidth(80);

        TableColumn<domain.Application, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(80);

        TableColumn<domain.Application, Double> rankCol = new TableColumn<>("Rank Score");
        rankCol.setCellValueFactory(new PropertyValueFactory<>("weightedRank"));
        rankCol.setPrefWidth(90);

        TableColumn<domain.Application, String> resumeCol = new TableColumn<>("Resume");
        resumeCol.setCellValueFactory(new PropertyValueFactory<>("resumeFile"));
        resumeCol.setPrefWidth(120);

        applicantsTable.getColumns().addAll(idCol, seekerCol, statusCol, rankCol, resumeCol);

        HBox actionRow = new HBox(10);
        actionRow.setPadding(new Insets(0, 16, 16, 16));

        Button rankBtn = new Button("Rank Applicants");
        rankBtn.setPrefHeight(38);
        HBox.setHgrow(rankBtn, Priority.ALWAYS);
        rankBtn.setMaxWidth(Double.MAX_VALUE);
        applyPrimaryButtonStyle(rankBtn, false);
        rankBtn.setOnMouseEntered(e -> applyPrimaryButtonStyle(rankBtn, true));
        rankBtn.setOnMouseExited(e -> applyPrimaryButtonStyle(rankBtn, false));
        rankBtn.setOnAction(e -> displayRankedCandidates());

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setPrefHeight(38);
        HBox.setHgrow(refreshBtn, Priority.ALWAYS);
        refreshBtn.setMaxWidth(Double.MAX_VALUE);
        applySecondaryButtonStyle(refreshBtn, false);
        refreshBtn.setOnMouseEntered(e -> applySecondaryButtonStyle(refreshBtn, true));
        refreshBtn.setOnMouseExited(e -> applySecondaryButtonStyle(refreshBtn, false));
        refreshBtn.setOnAction(e -> refreshView());

        actionRow.getChildren().addAll(rankBtn, refreshBtn);

        centerPanel.getChildren().addAll(heading, applicantsTable, actionRow);
        return centerPanel;
    }

    private ScrollPane buildRightPanel() {
        analyticsPanel = new VBox(20);
        analyticsPanel.setPrefWidth(300);
        analyticsPanel.setPadding(new Insets(20));
        analyticsPanel.setStyle(
            "-fx-background-color: #2D1515;" +
            "-fx-border-color: #4A2020;" +
            "-fx-border-width: 0 0 0 1;"
        );

        Label heading = new Label("ANALYTICS");
        heading.setStyle(
            "-fx-text-fill: #D4A0A0;" +
            "-fx-font-size: 10px;" +
            "-fx-font-weight: bold;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );

        VBox statsCard = new VBox(12);
        statsCard.setPadding(new Insets(16));
        statsCard.setStyle(
            "-fx-background-color: #3D1A1A;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #4A2020;" +
            "-fx-border-radius: 12;" +
            "-fx-border-width: 1;"
        );

        avgCGPALabel = new Label("Avg CGPA: --");
        avgCGPALabel.setStyle(
            "-fx-text-fill: #ffffff;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );

        totalApplicantsLabel = new Label("Total Applicants: --");
        totalApplicantsLabel.setStyle(
            "-fx-text-fill: #D4A0A0;" +
            "-fx-font-size: 13px;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );

        statsCard.getChildren().addAll(avgCGPALabel, totalApplicantsLabel);

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Match Range");
        xAxis.setTickLabelFill(Color.web("#D4A0A0"));

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Count");
        yAxis.setTickLabelFill(Color.web("#D4A0A0"));

        matchDistributionChart = new BarChart<>(xAxis, yAxis);
        matchDistributionChart.setTitle("Match Score Distribution");
        matchDistributionChart.setLegendVisible(false);
        matchDistributionChart.setPrefHeight(250);
        matchDistributionChart.setStyle(
            "-fx-background-color: transparent;"
        );
        matchDistributionChart.lookup(".chart-title").setStyle("-fx-text-fill: #D4A0A0; -fx-font-size: 11px;");

        VBox topSkillsCard = new VBox(8);
        topSkillsCard.setPadding(new Insets(16));
        topSkillsCard.setStyle(
            "-fx-background-color: #3D1A1A;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #4A2020;" +
            "-fx-border-radius: 12;" +
            "-fx-border-width: 1;"
        );

        Label skillsHeading = new Label("TOP APPLICANT SKILLS");
        skillsHeading.setStyle(
            "-fx-text-fill: #D4A0A0;" +
            "-fx-font-size: 10px;" +
            "-fx-font-weight: bold;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );
        topSkillsCard.getChildren().add(skillsHeading);

        analyticsPanel.getChildren().addAll(heading, statsCard, matchDistributionChart, topSkillsCard);

        ScrollPane scrollPane = new ScrollPane(analyticsPanel);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefWidth(320);
        scrollPane.setStyle("-fx-background: #2D1515; -fx-background-color: #2D1515;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        return scrollPane;
    }

    public void loadOfferings() {
        offeringsTable.getItems().clear();
        if (resolvedCompanyID <= 0) return;

        List<Offering> offerings = companyController.getPostedOfferings(resolvedCompanyID);
        offeringsTable.getItems().addAll(offerings);
    }

    public void loadApplicants() {
        applicantsTable.getItems().clear();
        Offering selected = offeringsTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        List<Application> applicants = companyController.getApplicants(selected.getOfferingID());
        applicantsTable.getItems().addAll(applicants);
    }

    public void handlePostOffering() {
        new PostOfferingScreen().start(primaryStage);
    }

    public void handleViewCandidates() {
        Offering selected = offeringsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showDialog("Please select an offering first.");
            return;
        }
        ViewCandidatesScreen screen = new ViewCandidatesScreen(selected.getOfferingID());
        screen.start(primaryStage);
    }

    public void loadAnalytics() {
        if (resolvedCompanyID <= 0) return;

        try {
            AnalyticsData analytics = companyController.getAnalytics(resolvedCompanyID);
            if (analytics == null) return;

            avgCGPALabel.setText("Avg CGPA: " + analytics.getAvgCGPA());
            totalApplicantsLabel.setText("Total Applicants: " + analytics.getTotalApplications());

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            Map<String, Integer> dist = analytics.getMatchDistribution();
            String[] ranges = {"0-20", "21-40", "41-60", "61-80", "81-100"};
            for (String range : ranges) {
                series.getData().add(new XYChart.Data<>(range, dist.getOrDefault(range, 0)));
            }
            matchDistributionChart.getData().clear();
            matchDistributionChart.getData().add(series);

            VBox topSkillsCard = (VBox) analyticsPanel.getChildren().get(3);
            while (topSkillsCard.getChildren().size() > 1) {
                topSkillsCard.getChildren().remove(topSkillsCard.getChildren().size() - 1);
            }
            List<String> topSkills = analytics.getTopSkills();
            if (topSkills.isEmpty()) {
                Label noSkills = new Label("No applicant data yet");
                noSkills.setStyle("-fx-text-fill: #8A6060; -fx-font-size: 11px;");
                topSkillsCard.getChildren().add(noSkills);
            } else {
                for (String skill : topSkills) {
                    HBox row = new HBox(8);
                    row.setAlignment(Pos.CENTER_LEFT);
                    Label bullet = new Label("\u2022");
                    bullet.setStyle("-fx-text-fill: #E53935; -fx-font-size: 14px;");
                    Label skillLabel = new Label(skill);
                    skillLabel.setStyle(
                        "-fx-text-fill: #D4A0A0;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
                    );
                    row.getChildren().addAll(bullet, skillLabel);
                    topSkillsCard.getChildren().add(row);
                }
            }
        } catch (Exception e) {
            avgCGPALabel.setText("Avg CGPA: --");
            totalApplicantsLabel.setText("Total Applicants: 0");
        }
    }

    public void displayRankedCandidates() {
        Offering selected = offeringsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showDialog("Please select an offering to rank applicants.");
            return;
        }

        applicantsTable.getItems().clear();
        List<Application> ranked = companyController.getRankedApplicants(selected.getOfferingID());
        applicantsTable.getItems().addAll(ranked);
    }

    private void showDialog(String message) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(primaryStage);
        dialog.setTitle("CareerBridge");
        dialog.setResizable(false);

        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(32, 36, 28, 36));
        content.setStyle("-fx-background-color: #2D1515;");

        Label msgLabel = new Label(message);
        msgLabel.setStyle(
            "-fx-text-fill: #D4A0A0;" +
            "-fx-font-size: 13px;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );
        msgLabel.setWrapText(true);
        msgLabel.setTextAlignment(TextAlignment.CENTER);

        Button okButton = new Button("OK");
        okButton.setPrefWidth(100);
        okButton.setPrefHeight(38);
        applyPrimaryButtonStyle(okButton, false);
        okButton.setOnMouseEntered(e -> applyPrimaryButtonStyle(okButton, true));
        okButton.setOnMouseExited(e -> applyPrimaryButtonStyle(okButton, false));
        okButton.setOnAction(e -> dialog.close());

        content.getChildren().addAll(msgLabel, okButton);

        Scene dialogScene = new Scene(content, 340, 160);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    private void applyPrimaryButtonStyle(Button btn, boolean hovered) {
        if (hovered) {
            btn.setStyle(
                "-fx-background-color: linear-gradient(to right, #C62828, #B71C1C);" +
                "-fx-text-fill: #ffffff;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 9;" +
                "-fx-cursor: hand;"
            );
            DropShadow glow = new DropShadow();
            glow.setColor(Color.web("#E53935", 0.45));
            glow.setRadius(16);
            btn.setEffect(glow);
        } else {
            btn.setStyle(
                "-fx-background-color: linear-gradient(to right, #E53935, #C62828);" +
                "-fx-text-fill: #0a0a0a;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 9;" +
                "-fx-cursor: hand;"
            );
            btn.setEffect(null);
        }
    }

    private void applySecondaryButtonStyle(Button btn, boolean hovered) {
        if (hovered) {
            btn.setStyle(
                "-fx-background-color: #3D1A1A;" +
                "-fx-text-fill: #E53935;" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 9;" +
                "-fx-border-color: #E53935;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 9;" +
                "-fx-cursor: hand;"
            );
        } else {
            btn.setStyle(
                "-fx-background-color: #2D1515;" +
                "-fx-text-fill: #E53935;" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 9;" +
                "-fx-border-color: #E53935;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 9;" +
                "-fx-cursor: hand;"
            );
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}