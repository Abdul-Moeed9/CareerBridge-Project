package ui;

import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.stage.*;

import java.util.List;

import controller.JobSeekerController;
import controller.OfferingController;
import controller.AdminController;
import domain.JobSeeker;
import domain.FilterCandidate;
import domain.Offering;
import domain.TrendingSkill;
import domain.User;
import interfaces.IDashboard;
import service.AIMatchingService;
import service.FilterService;
import domain.ApplicantRanker;
import domain.OfferingRanker;
import utility.SessionManager;
import utility.DatasetController;

public class JobSeekerDashboard extends Application implements IDashboard {

    private Label welcomeLabel;
    private ListView<Offering> jobListView;
    private FilterPanel filterPanel;
    private ProgressBar matchScoreBar;
    private ListView<TrendingSkill> trendingSkillsList;
    private Button applyButton;
    private Button viewProfileButton;
    private Button refreshFeedButton;

    private Stage primaryStage;
    private OfferingController offeringController;
    private JobSeekerController jobSeekerController;
    private AdminController adminController;
    private int currentSeekerID = -1;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        AIMatchingService aiService = new AIMatchingService();
        this.offeringController = new OfferingController(new FilterService(), new OfferingRanker(), aiService);
        this.jobSeekerController = new JobSeekerController(aiService);
        this.adminController = new AdminController();

        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser instanceof JobSeeker) {
            this.currentSeekerID = ((JobSeeker) currentUser).getSeekerID();
        }

        initialize();
    }

    public void initialize() {
        primaryStage.setTitle("CareerBridge - Job Seeker Dashboard");
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

    public void loadData() {
        loadJobListings();
        loadTrendingSkills();
        displayMatchScore(0.0);
    }

    public void refreshView() {
        jobListView.getItems().clear();
        trendingSkillsList.getItems().clear();
        loadData();
    }

    private Parent buildRoot() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0A1929;");
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
            "-fx-background-color: #0F2639;" +
            "-fx-border-color: #1E3A5F;" +
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
        String userName = currentUser != null ? currentUser.getName() : "Job Seeker";

        welcomeLabel = new Label("Welcome, " + userName + "!");
        welcomeLabel.setStyle(
            "-fx-text-fill: #B0C4DE;" +
            "-fx-font-size: 13px;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );

        Button myApplicationsButton = new Button("My Applications");
        myApplicationsButton.setPrefHeight(36);
        myApplicationsButton.setPadding(new Insets(0, 18, 0, 18));
        applyTopBarButtonStyle(myApplicationsButton, false);
        myApplicationsButton.setOnMouseEntered(e -> applyTopBarButtonStyle(myApplicationsButton, true));
        myApplicationsButton.setOnMouseExited(e -> applyTopBarButtonStyle(myApplicationsButton, false));
        myApplicationsButton.setOnAction(e -> navigateToMyApplications());

        viewProfileButton = new Button("My Profile");
        viewProfileButton.setPrefHeight(36);
        viewProfileButton.setPadding(new Insets(0, 18, 0, 18));
        applyTopBarButtonStyle(viewProfileButton, false);
        viewProfileButton.setOnMouseEntered(e -> applyTopBarButtonStyle(viewProfileButton, true));
        viewProfileButton.setOnMouseExited(e -> applyTopBarButtonStyle(viewProfileButton, false));
        viewProfileButton.setOnAction(e -> navigateToProfile());

        Button searchProfileButton = new Button("Search Profile");
        searchProfileButton.setPrefHeight(36);
        searchProfileButton.setPadding(new Insets(0, 18, 0, 18));
        applyTopBarButtonStyle(searchProfileButton, false);
        searchProfileButton.setOnMouseEntered(e -> applyTopBarButtonStyle(searchProfileButton, true));
        searchProfileButton.setOnMouseExited(e -> applyTopBarButtonStyle(searchProfileButton, false));
        searchProfileButton.setOnAction(e -> navigateToSearchProfile());

        Button logoutButton = new Button("Sign Out");
        logoutButton.setPrefHeight(36);
        logoutButton.setPadding(new Insets(0, 18, 0, 18));
        logoutButton.setStyle(
            "-fx-background-color: #132F4C;" +
            "-fx-text-fill: #B0C4DE;" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: #1E3A5F;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 8;" +
            "-fx-cursor: hand;"
        );
        logoutButton.setOnMouseEntered(e -> logoutButton.setStyle(
            "-fx-background-color: #1A3A5C;" +
            "-fx-text-fill: #FFFFFF;" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: #1E90FF;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 8;" +
            "-fx-cursor: hand;"
        ));
        logoutButton.setOnMouseExited(e -> logoutButton.setStyle(
            "-fx-background-color: #132F4C;" +
            "-fx-text-fill: #B0C4DE;" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: #1E3A5F;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 8;" +
            "-fx-cursor: hand;"
        ));
        logoutButton.setOnAction(e -> {
            SessionManager.getInstance().clearSession();
            new LoginScreen().start(primaryStage);
        });

        topBar.getChildren().addAll(rocketIcon, brand, spacer, welcomeLabel, myApplicationsButton, viewProfileButton, searchProfileButton, logoutButton);
        return topBar;
    }

    private VBox buildLeftPanel() {
        VBox leftPanel = new VBox(16);
        leftPanel.setPadding(new Insets(20, 0, 20, 0));
        leftPanel.setPrefWidth(240);
        leftPanel.setStyle(
            "-fx-background-color: #0F2639;" +
            "-fx-border-color: #1E3A5F;" +
            "-fx-border-width: 0 1 0 0;"
        );

        Label filterHeading = new Label("FILTERS");
        filterHeading.setStyle(
            "-fx-text-fill: #B0C4DE;" +
            "-fx-font-size: 10px;" +
            "-fx-font-weight: bold;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;" +
            "-fx-padding: 0 0 0 16;"
        );

        filterPanel = new FilterPanel();
        filterPanel.setOnApplyCallback(() -> handleFilter());
        filterPanel.setOnResetCallback(() -> handleRefreshFeed());
        VBox.setVgrow(filterPanel, Priority.ALWAYS);

        Button applyFilterButton = new Button("Apply Filters");
        applyFilterButton.setMaxWidth(Double.MAX_VALUE);
        applyFilterButton.setPrefHeight(40);
        VBox.setMargin(applyFilterButton, new Insets(0, 16, 0, 16));
        applyFilterButton.setStyle(
            "-fx-background-color: #132F4C;" +
            "-fx-text-fill: #1E90FF;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 9;" +
            "-fx-border-color: #1E90FF;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 9;" +
            "-fx-cursor: hand;"
        );
        applyFilterButton.setOnMouseEntered(e -> applyFilterButton.setStyle(
            "-fx-background-color: #0D3B66;" +
            "-fx-text-fill: #1E90FF;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 9;" +
            "-fx-border-color: #1E90FF;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 9;" +
            "-fx-cursor: hand;"
        ));
        applyFilterButton.setOnMouseExited(e -> applyFilterButton.setStyle(
            "-fx-background-color: #132F4C;" +
            "-fx-text-fill: #1E90FF;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 9;" +
            "-fx-border-color: #1E90FF;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 9;" +
            "-fx-cursor: hand;"
        ));
        applyFilterButton.setOnAction(e -> handleFilter());

        leftPanel.getChildren().addAll(filterHeading, filterPanel, applyFilterButton);
        return leftPanel;
    }

    private VBox buildCenterPanel() {
        VBox centerPanel = new VBox(0);
        centerPanel.setStyle("-fx-background-color: #0A1929;");

        HBox centerTopBar = new HBox(12);
        centerTopBar.setAlignment(Pos.CENTER_LEFT);
        centerTopBar.setPadding(new Insets(20, 24, 16, 24));

        Label listingsHeading = new Label("Job Listings");
        listingsHeading.setStyle(
            "-fx-text-fill: #ffffff;" +
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );

        Region centerSpacer = new Region();
        HBox.setHgrow(centerSpacer, Priority.ALWAYS);

        refreshFeedButton = new Button("Refresh Feed");
        refreshFeedButton.setPrefHeight(36);
        refreshFeedButton.setPadding(new Insets(0, 16, 0, 16));
        refreshFeedButton.setStyle(
            "-fx-background-color: #132F4C;" +
            "-fx-text-fill: #B0C4DE;" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: #1E3A5F;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 8;" +
            "-fx-cursor: hand;"
        );
        refreshFeedButton.setOnMouseEntered(e -> refreshFeedButton.setStyle(
            "-fx-background-color: #1A3A5C;" +
            "-fx-text-fill: #FFFFFF;" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: #1E90FF;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 8;" +
            "-fx-cursor: hand;"
        ));
        refreshFeedButton.setOnMouseExited(e -> refreshFeedButton.setStyle(
            "-fx-background-color: #132F4C;" +
            "-fx-text-fill: #B0C4DE;" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: #1E3A5F;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 8;" +
            "-fx-cursor: hand;"
        ));
        refreshFeedButton.setOnAction(e -> handleRefreshFeed());

        centerTopBar.getChildren().addAll(listingsHeading, centerSpacer, refreshFeedButton);

        jobListView = new ListView<>();
        jobListView.setStyle(
            "-fx-background-color: #0A1929;" +
            "-fx-border-color: transparent;" +
            "-fx-padding: 0 24 0 24;"
        );
        VBox.setVgrow(jobListView, Priority.ALWAYS);

        jobListView.setCellFactory(lv -> new ListCell<Offering>() {
            @Override
            protected void updateItem(Offering item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent;");
                    return;
                }

                VBox card = new VBox(8);
                card.setPadding(new Insets(16, 20, 16, 20));
                card.setStyle(
                    "-fx-background-color: #132F4C;" +
                    "-fx-background-radius: 12;" +
                    "-fx-border-color: #1E3A5F;" +
                    "-fx-border-width: 1;" +
                    "-fx-border-radius: 12;"
                );

                HBox titleRow = new HBox(10);
                titleRow.setAlignment(Pos.CENTER_LEFT);

                Label titleLabel = new Label(item.getTitle() != null ? item.getTitle() : "Untitled");
                titleLabel.setStyle(
                    "-fx-text-fill: #ffffff;" +
                    "-fx-font-size: 14px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
                );

                Region titleSpacer = new Region();
                HBox.setHgrow(titleSpacer, Priority.ALWAYS);

                double score = item.getMatchScore();
                Label matchBadge = new Label(String.format("%.0f%% Match", score * 100));
                matchBadge.setStyle(
                    "-fx-background-color: #0D3B66;" +
                    "-fx-text-fill: #1E90FF;" +
                    "-fx-font-size: 11px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-background-radius: 20;" +
                    "-fx-padding: 3 10 3 10;"
                );

                titleRow.getChildren().addAll(titleLabel, titleSpacer, matchBadge);

                Label typeLabel = new Label(item.getOfferingType() != null ? item.getOfferingType().toUpperCase() : "JOB");
                typeLabel.setStyle(
                    "-fx-text-fill: #B0C4DE;" +
                    "-fx-font-size: 12px;" +
                    "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
                );

                HBox metaRow = new HBox(18);
                metaRow.setAlignment(Pos.CENTER_LEFT);

                Label locationLabel = new Label(item.getLocation() != null ? "\uD83D\uDCCD " + item.getLocation() : "Remote");
                locationLabel.setStyle("-fx-text-fill: #B0C4DE; -fx-font-size: 11px;");

                String salaryDisplay = "Negotiable";
                if (item.getSalaryText() != null && !item.getSalaryText().isEmpty()) {
                    salaryDisplay = item.getSalaryText();
                } else if (item.getStipend() > 0) {
                    salaryDisplay = "PKR " + (int) item.getStipend() + "/mo";
                }
                Label stipendLabel = new Label(salaryDisplay);
                stipendLabel.setStyle("-fx-text-fill: #00E676; -fx-font-size: 11px; -fx-font-weight: bold;");

                Label cgpaLabel = new Label("CGPA: " + (item.getRequiredCGPA() != null ? item.getRequiredCGPA() : "None"));
                cgpaLabel.setStyle("-fx-text-fill: #B0C4DE; -fx-font-size: 11px;");

                metaRow.getChildren().addAll(locationLabel, stipendLabel, cgpaLabel);
                Label sourceBadge = new Label("scraped".equals(item.getSource()) ? "EXTERNAL" : "PLATFORM");
sourceBadge.setStyle(
    "-fx-background-color: " + ("scraped".equals(item.getSource()) ? "#3A2000" : "#002A1A") + ";" +
    "-fx-text-fill: " + ("scraped".equals(item.getSource()) ? "#FFA726" : "#00E676") + ";" +
    "-fx-font-size: 10px;" +
    "-fx-font-weight: bold;" +
    "-fx-background-radius: 20;" +
    "-fx-padding: 2 8 2 8;"
);

                card.getChildren().addAll(titleRow, typeLabel, metaRow, sourceBadge);

                card.setOnMouseEntered(ev -> card.setStyle(
                    "-fx-background-color: #1A3A5C;" +
                    "-fx-background-radius: 12;" +
                    "-fx-border-color: #1E90FF;" +
                    "-fx-border-width: 1;" +
                    "-fx-border-radius: 12;"
                ));
                card.setOnMouseExited(ev -> card.setStyle(
                    "-fx-background-color: #132F4C;" +
                    "-fx-background-radius: 12;" +
                    "-fx-border-color: #1E3A5F;" +
                    "-fx-border-width: 1;" +
                    "-fx-border-radius: 12;"
                ));

                VBox wrapper = new VBox(card);
                wrapper.setPadding(new Insets(4, 0, 4, 0));
                wrapper.setStyle("-fx-background-color: transparent;");

                setGraphic(wrapper);
                setStyle("-fx-background-color: transparent; -fx-padding: 0;");
            }
        });

        jobListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
    if (newVal != null) {
        if ("scraped".equals(newVal.getSource())) {
            applyButton.setText("Visit Link");
        } else {
            applyButton.setText("Apply Now");
        }
        if (currentSeekerID > 0) {
            double score = jobSeekerController.getMatchScore(currentSeekerID, newVal.getOfferingID());
            displayMatchScore(score);
        }
    }
});

        HBox bottomBar = new HBox(12);
        bottomBar.setAlignment(Pos.CENTER_RIGHT);
        bottomBar.setPadding(new Insets(16, 24, 20, 24));
        bottomBar.setStyle(
            "-fx-background-color: #0A1929;" +
            "-fx-border-color: #1E3A5F;" +
            "-fx-border-width: 1 0 0 0;"
        );

        applyButton = new Button("Apply Now");
        applyButton.setPrefHeight(42);
        applyButton.setPadding(new Insets(0, 28, 0, 28));
        applyPrimaryButtonStyle(applyButton, false);
        applyButton.setOnMouseEntered(e -> applyPrimaryButtonStyle(applyButton, true));
        applyButton.setOnMouseExited(e -> applyPrimaryButtonStyle(applyButton, false));
        applyButton.setOnAction(e -> handleApply());

        bottomBar.getChildren().add(applyButton);

        centerPanel.getChildren().addAll(centerTopBar, jobListView, bottomBar);
        return centerPanel;
    }

    private VBox buildRightPanel() {
        VBox rightPanel = new VBox(0);
        rightPanel.setPrefWidth(260);
        rightPanel.setStyle(
            "-fx-background-color: #0F2639;" +
            "-fx-border-color: #1E3A5F;" +
            "-fx-border-width: 0 0 0 1;"
        );

        VBox matchSection = new VBox(12);
        matchSection.setPadding(new Insets(20, 20, 20, 20));
        matchSection.setStyle(
            "-fx-border-color: #1E3A5F;" +
            "-fx-border-width: 0 0 1 0;"
        );

        Label matchHeading = new Label("MATCH SCORE");
        matchHeading.setStyle(
            "-fx-text-fill: #B0C4DE;" +
            "-fx-font-size: 10px;" +
            "-fx-font-weight: bold;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );

        Label selectedJobLabel = new Label("Select a job to see your match");
        selectedJobLabel.setStyle(
            "-fx-text-fill: #B0C4DE;" +
            "-fx-font-size: 11px;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );
        selectedJobLabel.setWrapText(true);

        matchScoreBar = new ProgressBar(0);
        matchScoreBar.setMaxWidth(Double.MAX_VALUE);
        matchScoreBar.setPrefHeight(10);
        matchScoreBar.setStyle(
            "-fx-accent: #1E90FF;" +
            "-fx-background-color: #1E3A5F;" +
            "-fx-background-radius: 10;" +
            "-fx-border-radius: 10;"
        );

        HBox scoreRow = new HBox();
        scoreRow.setAlignment(Pos.CENTER_LEFT);

        Label scoreLabel = new Label("0%");
        scoreLabel.setStyle(
            "-fx-text-fill: #1E90FF;" +
            "-fx-font-size: 28px;" +
            "-fx-font-weight: bold;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );

        Region scoreSpacer = new Region();
        HBox.setHgrow(scoreSpacer, Priority.ALWAYS);

        Label scoreCaption = new Label("match");
        scoreCaption.setStyle("-fx-text-fill: #B0C4DE; -fx-font-size: 12px;");

        scoreRow.getChildren().addAll(scoreLabel, scoreSpacer, scoreCaption);

        jobListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedJobLabel.setText(newVal.getTitle() != null ? newVal.getTitle() : "Selected Job");
                if (currentSeekerID > 0) {
                    double score = jobSeekerController.getMatchScore(currentSeekerID, newVal.getOfferingID());
                    displayMatchScore(score);
                    scoreLabel.setText(String.format("%.0f%%", score * 100));
                }
            }
        });

        matchSection.getChildren().addAll(matchHeading, selectedJobLabel, scoreRow, matchScoreBar);

        VBox skillsSection = new VBox(12);
        skillsSection.setPadding(new Insets(20, 20, 20, 20));
        VBox.setVgrow(skillsSection, Priority.ALWAYS);

        Label skillsHeading = new Label("TRENDING SKILLS");
        skillsHeading.setStyle(
            "-fx-text-fill: #B0C4DE;" +
            "-fx-font-size: 10px;" +
            "-fx-font-weight: bold;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );

        trendingSkillsList = new ListView<>();
        trendingSkillsList.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-border-color: transparent;"
        );
        VBox.setVgrow(trendingSkillsList, Priority.ALWAYS);

        trendingSkillsList.setCellFactory(lv -> new ListCell<TrendingSkill>() {
            @Override
            protected void updateItem(TrendingSkill item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent;");
                    return;
                }

                HBox row = new HBox(8);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPadding(new Insets(6, 0, 6, 0));

                Label bullet = new Label("\u2022");
                bullet.setStyle("-fx-text-fill: #1E90FF; -fx-font-size: 14px;");

                Label skillLabel = new Label(item.getSkillName());
                skillLabel.setStyle(
                    "-fx-text-fill: #B0C4DE;" +
                    "-fx-font-size: 12px;" +
                    "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
                );

                Region skillSpacer = new Region();
                HBox.setHgrow(skillSpacer, Priority.ALWAYS);

                Label freqLabel = new Label(String.valueOf(item.getFrequency()));
                freqLabel.setStyle(
                    "-fx-text-fill: #B0C4DE;" +
                    "-fx-font-size: 10px;" +
                    "-fx-padding: 0 14 0 0;"
                );

                row.getChildren().addAll(bullet, skillLabel, skillSpacer, freqLabel);

                setGraphic(row);
                setStyle("-fx-background-color: transparent; -fx-padding: 0;");
            }
        });

        skillsSection.getChildren().addAll(skillsHeading, trendingSkillsList);

        rightPanel.getChildren().addAll(matchSection, skillsSection);
        return rightPanel;
    }

    public void loadJobListings() {
        jobListView.getItems().clear();
        try {
            List<Offering> offerings;
            if (currentSeekerID > 0) {
                offerings = offeringController.getRankedOfferings(currentSeekerID);
            } else {
                offerings = offeringController.getAllOfferings();
            }
            jobListView.getItems().addAll(offerings);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleApply() {
    Offering selected = jobListView.getSelectionModel().getSelectedItem();
    if (selected == null) {
        showInfoDialog("Please select a job listing to apply.");
        return;
    }
    if (currentSeekerID <= 0) {
        showInfoDialog("Session error. Please log in again.");
        return;
    }
    if ("scraped".equals(selected.getSource())) {
        if (selected.getUrl() != null && !selected.getUrl().isEmpty()) {
            try {
                java.awt.Desktop.getDesktop().browse(new java.net.URI(selected.getUrl()));
            } catch (Exception ex) {
                showInfoDialog("Could not open link:\n" + selected.getUrl());
            }
        } else {
            showInfoDialog("This is an external listing with no available link.");
        }
        return;
    }
    boolean success = jobSeekerController.applyForOffering(currentSeekerID, selected.getOfferingID());
    if (success) {
        showInfoDialog("Application submitted for:\n" + selected.getTitle());
    } else {
        showInfoDialog("You have already applied for this offering, or an error occurred.");
    }
}

    public void handleFilter() {
        FilterCandidate filter = filterPanel.buildFilterObject();
        jobListView.getItems().clear();
        try {
            List<Offering> filtered = offeringController.getFilteredOfferings(filter, currentSeekerID);
            jobListView.getItems().addAll(filtered);
            if (filtered.isEmpty()) {
                showInfoDialog("No offerings match your filter criteria.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleRefreshFeed() {
        refreshFeedButton.setDisable(true);
        refreshFeedButton.setText("Loading...");
        new Thread(() -> {
            String processed = DatasetController.refreshOneDataset();
            if (processed != null) {
                adminController.refreshTrendingSkills();
            }
            javafx.application.Platform.runLater(() -> {
                refreshFeedButton.setDisable(false);
                refreshFeedButton.setText("Refresh Feed");
                jobListView.getItems().clear();
                loadJobListings();
                trendingSkillsList.getItems().clear();
                loadTrendingSkills();
                if (processed != null) {
                    showInfoDialog("Loaded dataset: " + processed);
                } else {
                    showInfoDialog("No new datasets available to load.");
                }
            });
        }).start();
    }

    public void navigateToProfile() {
        new ProfileSetupScreen().start(primaryStage);
    }

    public void navigateToSearchProfile() {
        ViewProfileScreen screen = new ViewProfileScreen();
        screen.start(primaryStage);
    }

    public void navigateToMyApplications() {
        new MyApplicationsScreen().start(primaryStage);
    }

    public void displayMatchScore(double score) {
        matchScoreBar.setProgress(score);
    }

    public void loadTrendingSkills() {
        trendingSkillsList.getItems().clear();
        try {
            List<TrendingSkill> skills = adminController.getTrendingSkills();
            trendingSkillsList.getItems().addAll(skills);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void applyTopBarButtonStyle(Button btn, boolean hovered) {
        if (hovered) {
            btn.setStyle(
                "-fx-background-color: linear-gradient(to right, #1872CC, #145A9E);" +
                "-fx-text-fill: #ffffff;" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"
            );
            DropShadow glow = new DropShadow();
            glow.setColor(Color.web("#1E90FF", 0.35));
            glow.setRadius(12);
            btn.setEffect(glow);
        } else {
            btn.setStyle(
                "-fx-background-color: linear-gradient(to right, #1E90FF, #1872CC);" +
                "-fx-text-fill: #0a0a0a;" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"
            );
            btn.setEffect(null);
        }
    }

    private void applyPrimaryButtonStyle(Button btn, boolean hovered) {
        if (hovered) {
            btn.setStyle(
                "-fx-background-color: linear-gradient(to right, #1872CC, #145A9E);" +
                "-fx-text-fill: #ffffff;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;"
            );
            DropShadow glow = new DropShadow();
            glow.setColor(Color.web("#1E90FF", 0.45));
            glow.setRadius(18);
            btn.setEffect(glow);
        } else {
            btn.setStyle(
                "-fx-background-color: linear-gradient(to right, #1E90FF, #1872CC);" +
                "-fx-text-fill: #0a0a0a;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;"
            );
            btn.setEffect(null);
        }
    }

    private void showInfoDialog(String message) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(primaryStage);
        dialog.setTitle("CareerBridge");
        dialog.setResizable(false);

        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(32, 36, 28, 36));
        content.setStyle("-fx-background-color: #132F4C;");

        Label msgLabel = new Label(message);
        msgLabel.setStyle(
            "-fx-text-fill: #B0C4DE;" +
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

    public static void main(String[] args) {
        launch(args);
    }
}