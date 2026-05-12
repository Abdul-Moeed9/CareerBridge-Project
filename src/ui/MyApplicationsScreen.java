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

import java.text.SimpleDateFormat;
import java.util.List;

import controller.JobSeekerController;
import controller.OfferingController;
import domain.JobSeeker;
import domain.Offering;
import domain.User;
import service.AIMatchingService;
import service.FilterService;
import domain.OfferingRanker;
import utility.SessionManager;

public class MyApplicationsScreen extends Application {

    private ListView<domain.Application> applicationsListView;
    private Label totalLabel;

    private Stage primaryStage;
    private JobSeekerController jobSeekerController;
    private OfferingController offeringController;
    private int currentSeekerID = -1;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        AIMatchingService aiService = new AIMatchingService();
        this.jobSeekerController = new JobSeekerController(aiService);
        this.offeringController = new OfferingController(new FilterService(), new OfferingRanker(), aiService);

        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser instanceof JobSeeker) {
            this.currentSeekerID = ((JobSeeker) currentUser).getSeekerID();
        }

        initialize();
    }

    public void initialize() {
        primaryStage.setTitle("CareerBridge - My Applications");
        primaryStage.setResizable(true);
        Scene scene = new Scene(buildRoot(), 1280, 800);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint("");
        primaryStage.setFullScreenExitKeyCombination(javafx.scene.input.KeyCombination.NO_MATCH);
        primaryStage.show();
        loadApplications();
    }

    private Parent buildRoot() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0A1929;");
        root.setTop(buildTopBar());
        root.setCenter(buildCenterPanel());
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

        Label titleLabel = new Label("My Applications");
        titleLabel.setStyle(
            "-fx-text-fill: #B0C4DE;" +
            "-fx-font-size: 14px;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );

        Button backButton = new Button("Back to Dashboard");
        backButton.setPrefHeight(36);
        backButton.setPadding(new Insets(0, 18, 0, 18));
        applyTopBarButtonStyle(backButton, false);
        backButton.setOnMouseEntered(e -> applyTopBarButtonStyle(backButton, true));
        backButton.setOnMouseExited(e -> applyTopBarButtonStyle(backButton, false));
        backButton.setOnAction(e -> new JobSeekerDashboard().start(primaryStage));

        topBar.getChildren().addAll(rocketIcon, brand, spacer, titleLabel, backButton);
        return topBar;
    }

    private VBox buildCenterPanel() {
        VBox outerContainer = new VBox(0);
        outerContainer.setAlignment(Pos.TOP_CENTER);
        outerContainer.setPadding(new Insets(40, 0, 40, 0));

        VBox card = new VBox(20);
        card.setMaxWidth(820);
        card.setMinWidth(820);
        card.setPadding(new Insets(36, 40, 36, 40));
        card.setStyle(
            "-fx-background-color: #0F2639;" +
            "-fx-background-radius: 16;" +
            "-fx-border-color: #1E3A5F;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 16;"
        );
        DropShadow cardShadow = new DropShadow();
        cardShadow.setColor(Color.web("#000000", 0.5));
        cardShadow.setRadius(40);
        cardShadow.setSpread(0.03);
        card.setEffect(cardShadow);

        Text heading = new Text("Application History");
        heading.setStyle(
            "-fx-font-family: 'Segoe UI', 'Helvetica', Arial, sans-serif;" +
            "-fx-font-size: 24px;" +
            "-fx-font-weight: bold;" +
            "-fx-fill: #ffffff;"
        );

        Label subtitle = new Label("Track the status of all your job and internship applications");
        subtitle.setStyle(
            "-fx-text-fill: #5A7A9A;" +
            "-fx-font-size: 12px;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );

        HBox statsBar = new HBox(20);
        statsBar.setAlignment(Pos.CENTER_LEFT);
        statsBar.setPadding(new Insets(12, 16, 12, 16));
        statsBar.setStyle(
            "-fx-background-color: #0A1929;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: #1E3A5F;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 10;"
        );

        totalLabel = new Label("Total: 0");
        totalLabel.setStyle(
            "-fx-text-fill: #B0C4DE;" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );

        Region statsSpacer = new Region();
        HBox.setHgrow(statsSpacer, Priority.ALWAYS);

        Button refreshButton = new Button("Refresh");
        refreshButton.setPrefHeight(32);
        refreshButton.setPadding(new Insets(0, 16, 0, 16));
        refreshButton.setStyle(
            "-fx-background-color: #132F4C;" +
            "-fx-text-fill: #B0C4DE;" +
            "-fx-font-size: 11px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: #1E3A5F;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 8;" +
            "-fx-cursor: hand;"
        );
        refreshButton.setOnMouseEntered(e -> refreshButton.setStyle(
            "-fx-background-color: #1A3A5C;" +
            "-fx-text-fill: #FFFFFF;" +
            "-fx-font-size: 11px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: #1E90FF;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 8;" +
            "-fx-cursor: hand;"
        ));
        refreshButton.setOnMouseExited(e -> refreshButton.setStyle(
            "-fx-background-color: #132F4C;" +
            "-fx-text-fill: #B0C4DE;" +
            "-fx-font-size: 11px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: #1E3A5F;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 8;" +
            "-fx-cursor: hand;"
        ));
        refreshButton.setOnAction(e -> loadApplications());

        statsBar.getChildren().addAll(totalLabel, statsSpacer, refreshButton);

        applicationsListView = new ListView<>();
        applicationsListView.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-border-color: transparent;"
        );
        applicationsListView.setPrefHeight(500);
        VBox.setVgrow(applicationsListView, Priority.ALWAYS);

        applicationsListView.setCellFactory(lv -> new ListCell<domain.Application>() {
            @Override
            protected void updateItem(domain.Application app, boolean empty) {
                super.updateItem(app, empty);
                if (empty || app == null) {
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent;");
                    return;
                }

                Offering offering = offeringController.getOfferingByID(app.getOfferingID());

                HBox cardBox = new HBox(0);
                cardBox.setPadding(new Insets(16, 20, 16, 20));
                cardBox.setAlignment(Pos.CENTER_LEFT);
                cardBox.setStyle(
                    "-fx-background-color: #132F4C;" +
                    "-fx-background-radius: 12;" +
                    "-fx-border-color: #1E3A5F;" +
                    "-fx-border-width: 1;" +
                    "-fx-border-radius: 12;"
                );

                VBox infoSection = new VBox(6);
                HBox.setHgrow(infoSection, Priority.ALWAYS);

                String jobTitle = "Unknown Position";
                String jobLocation = "";
                String jobType = "";
                double jobStipend = 0;
                String jobSalaryText = null;

                if (offering != null) {
                    jobTitle = offering.getTitle() != null ? offering.getTitle() : "Untitled";
                    jobLocation = offering.getLocation() != null ? offering.getLocation() : "";
                    jobType = offering.getOfferingType() != null ? offering.getOfferingType().toUpperCase() : "";
                    jobStipend = offering.getStipend();
                    jobSalaryText = offering.getSalaryText();
                }

                Label titleLabel = new Label(jobTitle);
                titleLabel.setStyle(
                    "-fx-text-fill: #ffffff;" +
                    "-fx-font-size: 14px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
                );

                HBox metaRow = new HBox(14);
                metaRow.setAlignment(Pos.CENTER_LEFT);

                if (!jobLocation.isEmpty()) {
                    Label locLabel = new Label("\uD83D\uDCCD " + jobLocation);
                    locLabel.setStyle("-fx-text-fill: #B0C4DE; -fx-font-size: 11px;");
                    metaRow.getChildren().add(locLabel);
                }

                if (!jobType.isEmpty()) {
                    Label typeLabel = new Label(jobType);
                    typeLabel.setStyle(
                        "-fx-text-fill: #5A7A9A;" +
                        "-fx-font-size: 10px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-color: #0A1929;" +
                        "-fx-background-radius: 4;" +
                        "-fx-padding: 2 8 2 8;"
                    );
                    metaRow.getChildren().add(typeLabel);
                }

                if (jobSalaryText != null && !jobSalaryText.isEmpty()) {
                    Label stipendLabel = new Label(jobSalaryText);
                    stipendLabel.setStyle("-fx-text-fill: #00E676; -fx-font-size: 11px; -fx-font-weight: bold;");
                    metaRow.getChildren().add(stipendLabel);
                } else if (jobStipend > 0) {
                    Label stipendLabel = new Label("PKR " + (int) jobStipend + "/mo");
                    stipendLabel.setStyle("-fx-text-fill: #00E676; -fx-font-size: 11px; -fx-font-weight: bold;");
                    metaRow.getChildren().add(stipendLabel);
                }

                String dateStr = "";
                if (app.getApplicationDate() != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
                    dateStr = "Applied on " + sdf.format(app.getApplicationDate());
                }
                Label dateLabel = new Label(dateStr);
                dateLabel.setStyle(
                    "-fx-text-fill: #5A7A9A;" +
                    "-fx-font-size: 10px;" +
                    "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
                );

                infoSection.getChildren().addAll(titleLabel, metaRow, dateLabel);

                VBox statusSection = new VBox(4);
                statusSection.setAlignment(Pos.CENTER_RIGHT);
                statusSection.setMinWidth(120);

                String status = app.getStatus() != null ? app.getStatus().toLowerCase() : "pending";
                Label statusBadge = new Label(status.substring(0, 1).toUpperCase() + status.substring(1));

                String badgeBg;
                String badgeText;
                String badgeBorder;
                switch (status) {
                    case "approved":
                        badgeBg = "#0D3B2E";
                        badgeText = "#00E676";
                        badgeBorder = "#1B5E20";
                        break;
                    case "rejected":
                        badgeBg = "#3B0D0D";
                        badgeText = "#EF5350";
                        badgeBorder = "#B71C1C";
                        break;
                    default:
                        badgeBg = "#1A2F4A";
                        badgeText = "#FFB74D";
                        badgeBorder = "#E65100";
                        break;
                }

                statusBadge.setStyle(
                    "-fx-background-color: " + badgeBg + ";" +
                    "-fx-text-fill: " + badgeText + ";" +
                    "-fx-font-size: 11px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-background-radius: 20;" +
                    "-fx-border-color: " + badgeBorder + ";" +
                    "-fx-border-width: 1;" +
                    "-fx-border-radius: 20;" +
                    "-fx-padding: 4 14 4 14;" +
                    "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
                );

                Button withdrawBtn = new Button("Withdraw");
                withdrawBtn.setPrefHeight(28);
                withdrawBtn.setPadding(new Insets(0, 14, 0, 14));
                withdrawBtn.setStyle(
                    "-fx-background-color: #3B0D0D;" +
                    "-fx-text-fill: #EF5350;" +
                    "-fx-font-size: 10px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-background-radius: 8;" +
                    "-fx-border-color: #B71C1C;" +
                    "-fx-border-width: 1;" +
                    "-fx-border-radius: 8;" +
                    "-fx-cursor: hand;"
                );
                withdrawBtn.setOnMouseEntered(ev -> withdrawBtn.setStyle(
                    "-fx-background-color: #5B1A1A;" +
                    "-fx-text-fill: #FFFFFF;" +
                    "-fx-font-size: 10px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-background-radius: 8;" +
                    "-fx-border-color: #EF5350;" +
                    "-fx-border-width: 1;" +
                    "-fx-border-radius: 8;" +
                    "-fx-cursor: hand;"
                ));
                withdrawBtn.setOnMouseExited(ev -> withdrawBtn.setStyle(
                    "-fx-background-color: #3B0D0D;" +
                    "-fx-text-fill: #EF5350;" +
                    "-fx-font-size: 10px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-background-radius: 8;" +
                    "-fx-border-color: #B71C1C;" +
                    "-fx-border-width: 1;" +
                    "-fx-border-radius: 8;" +
                    "-fx-cursor: hand;"
                ));

                if ("accepted".equalsIgnoreCase(status) || "rejected".equalsIgnoreCase(status)) {
                    withdrawBtn.setDisable(true);
                    withdrawBtn.setOpacity(0.4);
                }

                withdrawBtn.setOnAction(ev -> {
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                        "Are you sure you want to withdraw this application?",
                        ButtonType.YES, ButtonType.NO);
                    confirm.setTitle("Withdraw Application");
                    confirm.setHeaderText(null);
                    confirm.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.YES) {
                            boolean success = jobSeekerController.withdrawApplication(
                                app.getApplicationID(), currentSeekerID);
                            if (success) {
                                loadApplications();
                            } else {
                                Alert error = new Alert(Alert.AlertType.ERROR,
                                    "Failed to withdraw application.");
                                error.setHeaderText(null);
                                error.showAndWait();
                            }
                        }
                    });
                });

                statusSection.getChildren().addAll(statusBadge, withdrawBtn);

                cardBox.getChildren().addAll(infoSection, statusSection);

                cardBox.setOnMouseEntered(ev -> cardBox.setStyle(
                    "-fx-background-color: #1A3A5C;" +
                    "-fx-background-radius: 12;" +
                    "-fx-border-color: #1E90FF;" +
                    "-fx-border-width: 1;" +
                    "-fx-border-radius: 12;"
                ));
                cardBox.setOnMouseExited(ev -> cardBox.setStyle(
                    "-fx-background-color: #132F4C;" +
                    "-fx-background-radius: 12;" +
                    "-fx-border-color: #1E3A5F;" +
                    "-fx-border-width: 1;" +
                    "-fx-border-radius: 12;"
                ));

                VBox wrapper = new VBox(cardBox);
                wrapper.setPadding(new Insets(4, 0, 4, 0));
                wrapper.setStyle("-fx-background-color: transparent;");

                setGraphic(wrapper);
                setStyle("-fx-background-color: transparent; -fx-padding: 0;");
            }
        });

        Label emptyHint = new Label("");
        emptyHint.setStyle(
            "-fx-text-fill: #5A7A9A;" +
            "-fx-font-size: 12px;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );
        emptyHint.managedProperty().bind(emptyHint.visibleProperty());

        card.getChildren().addAll(heading, subtitle, statsBar, applicationsListView, emptyHint);

        outerContainer.getChildren().add(card);

        ScrollPane scrollPane = new ScrollPane(outerContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #0A1929; -fx-background-color: #0A1929;");

        VBox wrapper = new VBox(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        wrapper.setStyle("-fx-background-color: #0A1929;");
        return wrapper;
    }

    private void loadApplications() {
        applicationsListView.getItems().clear();
        if (currentSeekerID <= 0) return;
        try {
            List<domain.Application> applications = jobSeekerController.getApplicationHistory(currentSeekerID);
            applicationsListView.getItems().addAll(applications);
            totalLabel.setText("Total: " + applications.size());
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

    public static void main(String[] args) {
        launch(args);
    }
}