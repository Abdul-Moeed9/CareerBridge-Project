package ui;


import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.stage.*;
import controller.ApplicationController;
import controller.CompanyController;
import domain.Application;
import domain.Profile;
import domain.Offering;
import domain.ApplicantRanker;
import dao.ProfileDAO;
import service.AIMatchingService;
import java.util.List;

public class ViewCandidatesScreen extends javafx.application.Application {

    private TableView<Application> applicantsTable;
    private TableColumn<Application, Integer> rankColumn;
    private TableColumn<Application, String> nameColumn;
    private TableColumn<Application, Double> cgpaColumn;
    private TableColumn<Application, Double> matchScoreColumn;
    private ComboBox<String> sortBySelector;

    private int offeringID;
    private Stage primaryStage;
    private ApplicationController applicationController;
    private CompanyController companyController;
    private ProfileDAO profileDAO;

    public ViewCandidatesScreen() {
        this.offeringID = -1;
    }

    public ViewCandidatesScreen(int offeringID) {
        this.offeringID = offeringID;
    }

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        AIMatchingService aiService = new AIMatchingService();
        this.applicationController = new ApplicationController(new ApplicantRanker(), aiService);
        this.companyController = new CompanyController(aiService);
        this.profileDAO = new ProfileDAO();
        initialize();
    }

    public void initialize() {
        primaryStage.setTitle("CareerBridge - View Candidates");
        primaryStage.setResizable(true);
        Scene scene = new Scene(buildRoot(), 1280, 800);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint("");
        primaryStage.setFullScreenExitKeyCombination(javafx.scene.input.KeyCombination.NO_MATCH);
        primaryStage.show();
        loadApplicants(offeringID);
    }

    private Parent buildRoot() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1A0E0E;");
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

        Label separator = new Label("|");
        separator.setStyle("-fx-text-fill: #4A2020; -fx-font-size: 22px;");

        Label screenTitle = new Label("Candidates for Offering #" + offeringID);
        screenTitle.setStyle(
            "-fx-text-fill: #D4A0A0;" +
            "-fx-font-size: 14px;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button backButton = new Button("Back to Dashboard");
        backButton.setPrefHeight(36);
        backButton.setStyle(
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
        backButton.setOnMouseEntered(e -> backButton.setStyle(
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
        backButton.setOnMouseExited(e -> backButton.setStyle(
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
        backButton.setOnAction(e -> {
            CompanyDashboard dashboard = new CompanyDashboard();
            dashboard.start(primaryStage);
        });

        topBar.getChildren().addAll(rocketIcon, brand, separator, screenTitle, spacer, backButton);
        return topBar;
    }

    @SuppressWarnings("unchecked")
    private VBox buildCenterPanel() {
        VBox centerPanel = new VBox(0);
        centerPanel.setStyle("-fx-background-color: #1A0E0E;");

        HBox headerRow = new HBox(16);
        headerRow.setAlignment(Pos.CENTER_LEFT);
        headerRow.setPadding(new Insets(20, 24, 12, 24));

        Label heading = new Label("Applicants");
        heading.setStyle(
            "-fx-text-fill: #ffffff;" +
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        Label sortLabel = new Label("Sort by:");
        sortLabel.setStyle(
            "-fx-text-fill: #D4A0A0;" +
            "-fx-font-size: 12px;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );

        sortBySelector = new ComboBox<>();
        sortBySelector.getItems().addAll("Rank Score", "CGPA", "Application Date", "Status");
        sortBySelector.setValue("Rank Score");
        sortBySelector.setStyle(
            "-fx-background-color: #2D1515;" +
            "-fx-border-color: #4A2020;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 6;" +
            "-fx-background-radius: 6;"
        );
        sortBySelector.setOnAction(e -> handleSort());

        headerRow.getChildren().addAll(heading, headerSpacer, sortLabel, sortBySelector);

        applicantsTable = new TableView<>();
        applicantsTable.setStyle(
            "-fx-background-color: #1A0E0E;" +
            "-fx-border-color: #4A2020;" +
            "-fx-table-cell-border-color: #3D1A1A;"
        );
        applicantsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        applicantsTable.setPlaceholder(new Label("No applicants found for this offering"));
        VBox.setVgrow(applicantsTable, Priority.ALWAYS);
        VBox.setMargin(applicantsTable, new Insets(0, 16, 16, 16));

        rankColumn = new TableColumn<>("#");
        rankColumn.setPrefWidth(40);
        rankColumn.setCellFactory(col -> new TableCell<Application, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.valueOf(getIndex() + 1));
                }
            }
        });

        TableColumn<Application, Integer> appIdCol = new TableColumn<>("App ID");
        appIdCol.setCellValueFactory(new PropertyValueFactory<>("applicationID"));
        appIdCol.setPrefWidth(70);

        TableColumn<Application, Integer> seekerIdCol = new TableColumn<>("Seeker ID");
        seekerIdCol.setCellValueFactory(new PropertyValueFactory<>("seekerID"));
        seekerIdCol.setPrefWidth(80);

        nameColumn = new TableColumn<>("Name");
        nameColumn.setPrefWidth(140);
        nameColumn.setCellFactory(col -> new TableCell<Application, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                } else {
                    Application app = getTableRow().getItem();
                    Profile profile = app.getProfile();
                    if (profile != null && profile.getEducation() != null) {
                        setText(profile.getEducation());
                    } else {
                        setText("Seeker #" + app.getSeekerID());
                    }
                }
            }
        });

        cgpaColumn = new TableColumn<>("CGPA");
        cgpaColumn.setPrefWidth(70);
        cgpaColumn.setCellFactory(col -> new TableCell<Application, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                } else {
                    Application app = getTableRow().getItem();
                    Profile profile = app.getProfile();
                    if (profile != null && profile.getCGPA() > 0) {
                        setText(String.format("%.2f", profile.getCGPA()));
                    } else {
                        setText("--");
                    }
                }
            }
        });

        TableColumn<Application, String> skillsCol = new TableColumn<>("Skills");
        skillsCol.setPrefWidth(180);
        skillsCol.setCellFactory(col -> new TableCell<Application, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                } else {
                    Application app = getTableRow().getItem();
                    Profile profile = app.getProfile();
                    if (profile != null && profile.getSkills() != null) {
                        setText(profile.getSkills());
                    } else {
                        setText("--");
                    }
                }
            }
        });

        matchScoreColumn = new TableColumn<>("Rank Score");
        matchScoreColumn.setCellValueFactory(new PropertyValueFactory<>("weightedRank"));
        matchScoreColumn.setPrefWidth(90);
        matchScoreColumn.setCellFactory(col -> new TableCell<Application, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    double score = item * 100;
                    setText(String.format("%.1f%%", score));
                    if (score >= 75) {
                        setStyle("-fx-text-fill: #00ff88; -fx-font-weight: bold;");
                    } else if (score >= 50) {
                        setStyle("-fx-text-fill: #FFD700; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #D4A0A0;");
                    }
                }
            }
        });

        TableColumn<Application, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(80);
        statusCol.setCellFactory(col -> new TableCell<Application, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.substring(0, 1).toUpperCase() + item.substring(1));
                    if ("accepted".equalsIgnoreCase(item)) {
                        setStyle("-fx-text-fill: #00ff88; -fx-font-weight: bold;");
                    } else if ("rejected".equalsIgnoreCase(item)) {
                        setStyle("-fx-text-fill: #ff4757;");
                    } else {
                        setStyle("-fx-text-fill: #FFD700;");
                    }
                }
            }
        });

        applicantsTable.getColumns().addAll(
            rankColumn, appIdCol, seekerIdCol, nameColumn, cgpaColumn, skillsCol, matchScoreColumn, statusCol
        );

        HBox actionRow = new HBox(10);
        actionRow.setPadding(new Insets(0, 16, 16, 16));

        Button acceptBtn = new Button("Accept Selected");
        acceptBtn.setPrefHeight(38);
        HBox.setHgrow(acceptBtn, Priority.ALWAYS);
        acceptBtn.setMaxWidth(Double.MAX_VALUE);
        applySecondaryButtonStyle(acceptBtn, false);
        acceptBtn.setOnMouseEntered(e -> applySecondaryButtonStyle(acceptBtn, true));
        acceptBtn.setOnMouseExited(e -> applySecondaryButtonStyle(acceptBtn, false));
        acceptBtn.setOnAction(e -> {
            Application selected = applicantsTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showDialog("Please select an applicant first.");
                return;
            }
            boolean success = applicationController.updateApplicationStatus(selected.getApplicationID(), "accepted");
            if (success) {
                showDialog("Applicant accepted successfully.");
                loadApplicants(offeringID);
            } else {
                showDialog("Failed to update status.");
            }
        });

        Button rejectBtn = new Button("Reject Selected");
        rejectBtn.setPrefHeight(38);
        HBox.setHgrow(rejectBtn, Priority.ALWAYS);
        rejectBtn.setMaxWidth(Double.MAX_VALUE);
        applyDangerButtonStyle(rejectBtn, false);
        rejectBtn.setOnMouseEntered(e -> applyDangerButtonStyle(rejectBtn, true));
        rejectBtn.setOnMouseExited(e -> applyDangerButtonStyle(rejectBtn, false));
        rejectBtn.setOnAction(e -> {
            Application selected = applicantsTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showDialog("Please select an applicant first.");
                return;
            }
            boolean success = applicationController.updateApplicationStatus(selected.getApplicationID(), "rejected");
            if (success) {
                showDialog("Applicant rejected.");
                loadApplicants(offeringID);
            } else {
                showDialog("Failed to update status.");
            }
        });

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setPrefHeight(38);
        HBox.setHgrow(refreshBtn, Priority.ALWAYS);
        refreshBtn.setMaxWidth(Double.MAX_VALUE);
        refreshBtn.setStyle(
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
        refreshBtn.setOnMouseEntered(e -> refreshBtn.setStyle(
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
        refreshBtn.setOnMouseExited(e -> refreshBtn.setStyle(
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
        refreshBtn.setOnAction(e -> loadApplicants(offeringID));

        actionRow.getChildren().addAll(acceptBtn, rejectBtn, refreshBtn);

        centerPanel.getChildren().addAll(headerRow, applicantsTable, actionRow);
        return centerPanel;
    }

    private VBox buildRightPanel() {
        VBox rightPanel = new VBox(16);
        rightPanel.setPrefWidth(280);
        rightPanel.setPadding(new Insets(20));
        rightPanel.setStyle(
            "-fx-background-color: #2D1515;" +
            "-fx-border-color: #4A2020;" +
            "-fx-border-width: 0 0 0 1;"
        );

        Label heading = new Label("CANDIDATE DETAILS");
        heading.setStyle(
            "-fx-text-fill: #D4A0A0;" +
            "-fx-font-size: 10px;" +
            "-fx-font-weight: bold;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );

        VBox detailCard = new VBox(12);
        detailCard.setPadding(new Insets(16));
        detailCard.setStyle(
            "-fx-background-color: #3D1A1A;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #4A2020;" +
            "-fx-border-radius: 12;" +
            "-fx-border-width: 1;"
        );

        Label placeholderLabel = new Label("Select an applicant to view details");
        placeholderLabel.setStyle(
            "-fx-text-fill: #8A6060;" +
            "-fx-font-size: 12px;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );
        placeholderLabel.setWrapText(true);
        detailCard.getChildren().add(placeholderLabel);

        applicantsTable.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> {
            detailCard.getChildren().clear();
            if (nv == null) {
                detailCard.getChildren().add(placeholderLabel);
                return;
            }
            Profile profile = nv.getProfile();

            Label idLabel = createDetailLabel("Application ID", String.valueOf(nv.getApplicationID()));
            Label seekerLabel = createDetailLabel("Seeker ID", String.valueOf(nv.getSeekerID()));
            Label statusLabel = createDetailLabel("Status", nv.getStatus() != null ? nv.getStatus() : "--");
            Label rankLabel = createDetailLabel("Rank Score", String.format("%.1f%%", nv.getWeightedRank() * 100));

            detailCard.getChildren().addAll(idLabel, seekerLabel, statusLabel, rankLabel);

            if (profile != null) {
                Separator sep = new Separator();
                sep.setStyle("-fx-background-color: #4A2020;");

                Label eduLabel = createDetailLabel("Education", profile.getEducation() != null ? profile.getEducation() : "--");
                Label cgpaLabel = createDetailLabel("CGPA", profile.getCGPA() > 0 ? String.format("%.2f", profile.getCGPA()) : "--");
                Label skillsLabel = createDetailLabel("Skills", profile.getSkills() != null ? profile.getSkills() : "--");
                skillsLabel.setWrapText(true);
                Label expLabel = createDetailLabel("Experience", profile.getExperience() != null ? profile.getExperience() : "--");
                expLabel.setWrapText(true);
                Label cvLabel = createDetailLabel("CV File", profile.getCVFile() != null && !profile.getCVFile().isEmpty() ? profile.getCVFile() : "Not uploaded");

                detailCard.getChildren().addAll(sep, eduLabel, cgpaLabel, skillsLabel, expLabel, cvLabel);
            }
        });

        Button viewProfileBtn = new Button("View Full Profile");
        viewProfileBtn.setMaxWidth(Double.MAX_VALUE);
        viewProfileBtn.setPrefHeight(38);
        applySecondaryButtonStyle(viewProfileBtn, false);
        viewProfileBtn.setOnMouseEntered(e -> applySecondaryButtonStyle(viewProfileBtn, true));
        viewProfileBtn.setOnMouseExited(e -> applySecondaryButtonStyle(viewProfileBtn, false));
        viewProfileBtn.setOnAction(e -> handleViewProfile());

        rightPanel.getChildren().addAll(heading, detailCard, viewProfileBtn);
        return rightPanel;
    }

    public void loadApplicants(int offeringID) {
        applicantsTable.getItems().clear();
        if (offeringID <= 0) return;
        try {
            List<Application> applicants = applicationController.getApplicationsByOffering(offeringID);
            for (Application app : applicants) {
                if (app.getProfile() == null) {
                    app.setProfile(profileDAO.findBySeekerID(app.getSeekerID()));
                }
            }
            applicantsTable.getItems().addAll(applicants);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleSort() {
        String sortBy = sortBySelector.getValue();
        if (sortBy == null || applicantsTable.getItems().isEmpty()) return;

        List<Application> items = new java.util.ArrayList<>(applicantsTable.getItems());

        switch (sortBy) {
            case "Rank Score":
                items.sort((a, b) -> Double.compare(b.getWeightedRank(), a.getWeightedRank()));
                break;
            case "CGPA":
                items.sort((a, b) -> {
                    double cgpaA = a.getProfile() != null ? a.getProfile().getCGPA() : 0;
                    double cgpaB = b.getProfile() != null ? b.getProfile().getCGPA() : 0;
                    return Double.compare(cgpaB, cgpaA);
                });
                break;
            case "Application Date":
                items.sort((a, b) -> {
                    if (a.getApplicationDate() == null || b.getApplicationDate() == null) return 0;
                    return b.getApplicationDate().compareTo(a.getApplicationDate());
                });
                break;
            case "Status":
                items.sort((a, b) -> {
                    String sa = a.getStatus() != null ? a.getStatus() : "";
                    String sb = b.getStatus() != null ? b.getStatus() : "";
                    return sa.compareTo(sb);
                });
                break;
        }

        applicantsTable.getItems().clear();
        applicantsTable.getItems().addAll(items);
    }

    public void handleViewProfile() {
        Application selected = applicantsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showDialog("Please select an applicant to view profile.");
            return;
        }
        ViewProfileScreen profileScreen = new ViewProfileScreen(selected.getSeekerID(), offeringID);
        profileScreen.start(primaryStage);
    }

    private Label createDetailLabel(String title, String value) {
        Label label = new Label(title + ": " + value);
        label.setStyle(
            "-fx-text-fill: #D4A0A0;" +
            "-fx-font-size: 12px;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );
        return label;
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

        Scene dialogScene = new Scene(content, 380, 180);
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

    private void applyDangerButtonStyle(Button btn, boolean hovered) {
        if (hovered) {
            btn.setStyle(
                "-fx-background-color: #4A1010;" +
                "-fx-text-fill: #ff4757;" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 9;" +
                "-fx-border-color: #ff4757;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 9;" +
                "-fx-cursor: hand;"
            );
        } else {
            btn.setStyle(
                "-fx-background-color: #3D1515;" +
                "-fx-text-fill: #ff4757;" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 9;" +
                "-fx-border-color: #ff4757;" +
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