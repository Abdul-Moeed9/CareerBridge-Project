package ui;

import javafx.application.Application;
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
import controller.AdminController;
import domain.Company;
import domain.TrendingSkill;
import domain.User;
import utility.SessionManager;
import java.util.List;

public class AdminDashboard extends Application implements IDashboard {

    private TableView<Company> pendingCompaniesTable;
    private Button approveButton;
    private Button rejectButton;
    private TableView<User> allUsersTable;
    private Button deactivateUserButton;
    private VBox trendingSkillsPanel;

    private Stage primaryStage;
    private AdminController adminController;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        this.adminController = new AdminController();
        initialize();
    }

    @Override
    public void initialize() {
        primaryStage.setTitle("CareerBridge - Admin Dashboard");
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
        loadPendingCompanies();
        loadAllUsers();
        loadTrendingSkills();
    }

    @Override
    public void refreshView() {
        pendingCompaniesTable.getItems().clear();
        allUsersTable.getItems().clear();
        loadData();
    }

    private Parent buildRoot() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #111111;");
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

        Label roleTag = new Label("ADMIN");
        roleTag.setStyle(
            "-fx-background-color: #002b3d;" +
            "-fx-text-fill: #00d4ff;" +
            "-fx-font-size: 9px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 3 8 3 8;" +
            "-fx-background-radius: 4;" +
            "-fx-border-color: #00d4ff;" +
            "-fx-border-radius: 4;" +
            "-fx-border-width: 1;"
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        User currentUser = SessionManager.getInstance().getCurrentUser();
        String userName = currentUser != null ? currentUser.getName() : "Admin";

        Label welcomeLabel = new Label("Welcome, " + userName);
        welcomeLabel.setStyle(
            "-fx-text-fill: #888888;" +
            "-fx-font-size: 13px;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );

        Button refreshButton = new Button("Refresh All");
        refreshButton.setPrefHeight(36);
        applyPrimaryButtonStyle(refreshButton, false);
        refreshButton.setOnMouseEntered(e -> applyPrimaryButtonStyle(refreshButton, true));
        refreshButton.setOnMouseExited(e -> applyPrimaryButtonStyle(refreshButton, false));
        refreshButton.setOnAction(e -> refreshView());

        Button logoutButton = new Button("Logout");
        logoutButton.setPrefHeight(36);
        logoutButton.setStyle(
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
        logoutButton.setOnMouseEntered(e -> logoutButton.setStyle(
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
        logoutButton.setOnMouseExited(e -> logoutButton.setStyle(
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
        logoutButton.setOnAction(e -> {
            SessionManager.getInstance().clearSession();
            new LoginScreen().start(primaryStage);
        });

        topBar.getChildren().addAll(rocketIcon, brand, roleTag, spacer, welcomeLabel, refreshButton, logoutButton);
        return topBar;
    }

    @SuppressWarnings("unchecked")
    private VBox buildLeftPanel() {
        VBox leftPanel = new VBox(16);
        leftPanel.setPadding(new Insets(20, 0, 20, 0));
        leftPanel.setPrefWidth(440);
        leftPanel.setStyle(
            "-fx-background-color: #1e1e1e;" +
            "-fx-border-color: #2e2e2e;" +
            "-fx-border-width: 0 1 0 0;"
        );

        Label heading = new Label("PENDING COMPANIES");
        heading.setStyle(
            "-fx-text-fill: #888888;" +
            "-fx-font-size: 10px;" +
            "-fx-font-weight: bold;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;" +
            "-fx-padding: 0 0 0 16;"
        );

        pendingCompaniesTable = new TableView<>();
        pendingCompaniesTable.setStyle(
            "-fx-background-color: #1e1e1e;" +
            "-fx-border-color: #2e2e2e;" +
            "-fx-table-cell-border-color: #2a2a2a;"
        );
        pendingCompaniesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        pendingCompaniesTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        pendingCompaniesTable.setPlaceholder(new Label("No pending companies"));
        VBox.setVgrow(pendingCompaniesTable, Priority.ALWAYS);
        VBox.setMargin(pendingCompaniesTable, new Insets(0, 12, 0, 12));

        TableColumn<Company, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("companyID"));
        idCol.setPrefWidth(50);

        TableColumn<Company, String> nameCol = new TableColumn<>("Company Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("companyName"));
        nameCol.setPrefWidth(140);

        TableColumn<Company, String> industryCol = new TableColumn<>("Industry");
        industryCol.setCellValueFactory(new PropertyValueFactory<>("industry"));
        industryCol.setPrefWidth(100);

        TableColumn<Company, String> emailCol = new TableColumn<>("Contact");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("contactEmail"));
        emailCol.setPrefWidth(130);

        pendingCompaniesTable.getColumns().addAll(idCol, nameCol, industryCol, emailCol);

        HBox buttonRow = new HBox(10);
        buttonRow.setPadding(new Insets(0, 12, 0, 12));

        approveButton = new Button("Approve");
        approveButton.setPrefHeight(36);
        HBox.setHgrow(approveButton, Priority.ALWAYS);
        approveButton.setMaxWidth(Double.MAX_VALUE);
        applyPrimaryButtonStyle(approveButton, false);
        approveButton.setOnMouseEntered(e -> applyPrimaryButtonStyle(approveButton, true));
        approveButton.setOnMouseExited(e -> applyPrimaryButtonStyle(approveButton, false));
        approveButton.setOnAction(e -> handleApproveCompany());

        rejectButton = new Button("Reject");
        rejectButton.setPrefHeight(36);
        HBox.setHgrow(rejectButton, Priority.ALWAYS);
        rejectButton.setMaxWidth(Double.MAX_VALUE);
        applyDangerButtonStyle(rejectButton, false);
        rejectButton.setOnMouseEntered(e -> applyDangerButtonStyle(rejectButton, true));
        rejectButton.setOnMouseExited(e -> applyDangerButtonStyle(rejectButton, false));
        rejectButton.setOnAction(e -> handleRejectCompany());

        buttonRow.getChildren().addAll(approveButton, rejectButton);

        leftPanel.getChildren().addAll(heading, pendingCompaniesTable, buttonRow);
        return leftPanel;
    }

    @SuppressWarnings("unchecked")
    private VBox buildCenterPanel() {
        VBox centerPanel = new VBox(0);
        centerPanel.setStyle("-fx-background-color: #111111;");

        Label heading = new Label("All Users");
        heading.setStyle(
            "-fx-text-fill: #ffffff;" +
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );
        heading.setPadding(new Insets(20, 24, 12, 24));

        allUsersTable = new TableView<>();
        allUsersTable.setStyle(
            "-fx-background-color: #111111;" +
            "-fx-border-color: #2e2e2e;" +
            "-fx-table-cell-border-color: #2a2a2a;"
        );
        allUsersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        allUsersTable.setPlaceholder(new Label("No users found"));
        VBox.setVgrow(allUsersTable, Priority.ALWAYS);
        VBox.setMargin(allUsersTable, new Insets(0, 16, 16, 16));

        TableColumn<User, Integer> userIdCol = new TableColumn<>("User ID");
        userIdCol.setCellValueFactory(new PropertyValueFactory<>("userID"));
        userIdCol.setPrefWidth(70);

        TableColumn<User, String> userNameCol = new TableColumn<>("Name");
        userNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        userNameCol.setPrefWidth(140);

        TableColumn<User, String> userEmailCol = new TableColumn<>("Email");
        userEmailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        userEmailCol.setPrefWidth(180);

        TableColumn<User, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        roleCol.setPrefWidth(90);

        TableColumn<User, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("accountStatus"));
        statusCol.setPrefWidth(90);

        allUsersTable.getColumns().addAll(userIdCol, userNameCol, userEmailCol, roleCol, statusCol);

        HBox actionRow = new HBox(10);
        actionRow.setPadding(new Insets(0, 16, 16, 16));

        deactivateUserButton = new Button("Deactivate Selected User");
        deactivateUserButton.setPrefHeight(38);
        HBox.setHgrow(deactivateUserButton, Priority.ALWAYS);
        deactivateUserButton.setMaxWidth(Double.MAX_VALUE);
        applyDangerButtonStyle(deactivateUserButton, false);
        deactivateUserButton.setOnMouseEntered(e -> applyDangerButtonStyle(deactivateUserButton, true));
        deactivateUserButton.setOnMouseExited(e -> applyDangerButtonStyle(deactivateUserButton, false));
        deactivateUserButton.setOnAction(e -> handleDeactivateUser());

        Button refreshUsersBtn = new Button("Refresh Users");
        refreshUsersBtn.setPrefHeight(38);
        HBox.setHgrow(refreshUsersBtn, Priority.ALWAYS);
        refreshUsersBtn.setMaxWidth(Double.MAX_VALUE);
        applySecondaryButtonStyle(refreshUsersBtn, false);
        refreshUsersBtn.setOnMouseEntered(e -> applySecondaryButtonStyle(refreshUsersBtn, true));
        refreshUsersBtn.setOnMouseExited(e -> applySecondaryButtonStyle(refreshUsersBtn, false));
        refreshUsersBtn.setOnAction(e -> {
            allUsersTable.getItems().clear();
            loadAllUsers();
        });

        actionRow.getChildren().addAll(deactivateUserButton, refreshUsersBtn);

        centerPanel.getChildren().addAll(heading, allUsersTable, actionRow);
        return centerPanel;
    }

    private ScrollPane buildRightPanel() {
        trendingSkillsPanel = new VBox(20);
        trendingSkillsPanel.setPrefWidth(300);
        trendingSkillsPanel.setPadding(new Insets(20));
        trendingSkillsPanel.setStyle(
            "-fx-background-color: #1e1e1e;" +
            "-fx-border-color: #2e2e2e;" +
            "-fx-border-width: 0 0 0 1;"
        );

        Label heading = new Label("TRENDING SKILLS");
        heading.setStyle(
            "-fx-text-fill: #888888;" +
            "-fx-font-size: 10px;" +
            "-fx-font-weight: bold;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );

        VBox statsCard = new VBox(12);
        statsCard.setPadding(new Insets(16));
        statsCard.setStyle(
            "-fx-background-color: #252525;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #2e2e2e;" +
            "-fx-border-radius: 12;" +
            "-fx-border-width: 1;"
        );

        Label statsHeading = new Label("Platform Overview");
        statsHeading.setStyle(
            "-fx-text-fill: #ffffff;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );

        Label pendingCountLabel = new Label("Pending: " + pendingCompaniesTable.getItems().size());
        pendingCountLabel.setStyle(
            "-fx-text-fill: #00d4ff;" +
            "-fx-font-size: 13px;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );

        statsCard.getChildren().addAll(statsHeading, pendingCountLabel);

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setTickLabelFill(Color.web("#888888"));
        xAxis.setTickLabelRotation(45);

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Frequency");
        yAxis.setTickLabelFill(Color.web("#888888"));

        BarChart<String, Number> skillsBarChart = new BarChart<>(xAxis, yAxis);
        skillsBarChart.setTitle("Skill Demand");
        skillsBarChart.setLegendVisible(false);
        skillsBarChart.setPrefHeight(280);
        skillsBarChart.setStyle("-fx-background-color: transparent;");

        VBox skillsListCard = new VBox(8);
        skillsListCard.setPadding(new Insets(16));
        skillsListCard.setStyle(
            "-fx-background-color: #252525;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #2e2e2e;" +
            "-fx-border-radius: 12;" +
            "-fx-border-width: 1;"
        );

        Label skillsListHeading = new Label("TOP SKILLS");
        skillsListHeading.setStyle(
            "-fx-text-fill: #888888;" +
            "-fx-font-size: 10px;" +
            "-fx-font-weight: bold;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );
        skillsListCard.getChildren().add(skillsListHeading);

        Button refreshSkillsBtn = new Button("Refresh Skills");
        refreshSkillsBtn.setMaxWidth(Double.MAX_VALUE);
        refreshSkillsBtn.setPrefHeight(36);
        applySecondaryButtonStyle(refreshSkillsBtn, false);
        refreshSkillsBtn.setOnMouseEntered(e -> applySecondaryButtonStyle(refreshSkillsBtn, true));
        refreshSkillsBtn.setOnMouseExited(e -> applySecondaryButtonStyle(refreshSkillsBtn, false));
        refreshSkillsBtn.setOnAction(e -> {
            adminController.refreshTrendingSkills();
            loadTrendingSkills();
        });

        trendingSkillsPanel.getChildren().addAll(heading, statsCard, skillsBarChart, skillsListCard, refreshSkillsBtn);

        ScrollPane scrollPane = new ScrollPane(trendingSkillsPanel);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefWidth(320);
        scrollPane.setStyle("-fx-background: #1e1e1e; -fx-background-color: #1e1e1e;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        return scrollPane;
    }

    public void loadPendingCompanies() {
        pendingCompaniesTable.getItems().clear();
        try {
            List<Company> pending = adminController.getPendingCompanies();
            pendingCompaniesTable.getItems().addAll(pending);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleApproveCompany() {
        Company selected = pendingCompaniesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showDialog("Please select a company to approve.");
            return;
        }
        boolean success = adminController.approveCompany(selected.getCompanyID());
        if (success) {
            showDialog("Company '" + selected.getCompanyName() + "' approved successfully.");
            refreshView();
        } else {
            showDialog("Failed to approve company. Please try again.");
        }
    }

    public void handleRejectCompany() {
        Company selected = pendingCompaniesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showDialog("Please select a company to reject.");
            return;
        }
        boolean success = adminController.rejectCompany(selected.getCompanyID());
        if (success) {
            showDialog("Company '" + selected.getCompanyName() + "' rejected.");
            refreshView();
        } else {
            showDialog("Failed to reject company. Please try again.");
        }
    }

    public void loadAllUsers() {
        allUsersTable.getItems().clear();
        try {
            List<User> users = adminController.getAllUsers();
            allUsersTable.getItems().addAll(users);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleDeactivateUser() {
        User selected = allUsersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showDialog("Please select a user to deactivate.");
            return;
        }
        if ("admin".equals(selected.getRole())) {
            showDialog("Cannot deactivate admin accounts.");
            return;
        }
        boolean success = adminController.deactivateUser(selected.getUserID());
        if (success) {
            showDialog("User '" + selected.getName() + "' has been deactivated.");
            allUsersTable.getItems().clear();
            loadAllUsers();
        } else {
            showDialog("Failed to deactivate user. Please try again.");
        }
    }

    public void loadTrendingSkills() {
        try {
            List<TrendingSkill> skills = adminController.getTrendingSkills();

            if (trendingSkillsPanel.getChildren().size() >= 3) {
                Node chartNode = trendingSkillsPanel.getChildren().get(2);
                if (chartNode instanceof BarChart) {
                    BarChart<String, Number> chart = (BarChart<String, Number>) chartNode;
                    chart.getData().clear();
                    XYChart.Series<String, Number> series = new XYChart.Series<>();
                    int count = 0;
                    for (TrendingSkill skill : skills) {
                        if (count >= 10) break;
                        series.getData().add(new XYChart.Data<>(skill.getSkillName(), skill.getFrequency()));
                        count++;
                    }
                    chart.getData().add(series);
                }
            }

            if (trendingSkillsPanel.getChildren().size() >= 4) {
                Node listNode = trendingSkillsPanel.getChildren().get(3);
                if (listNode instanceof VBox) {
                    VBox skillsListCard = (VBox) listNode;
                    while (skillsListCard.getChildren().size() > 1) {
                        skillsListCard.getChildren().remove(skillsListCard.getChildren().size() - 1);
                    }
                    if (skills.isEmpty()) {
                        Label noData = new Label("No trending skills data");
                        noData.setStyle("-fx-text-fill: #666666; -fx-font-size: 11px;");
                        skillsListCard.getChildren().add(noData);
                    } else {
                        int rank = 1;
                        for (TrendingSkill skill : skills) {
                            if (rank > 15) break;
                            HBox row = new HBox(10);
                            row.setAlignment(Pos.CENTER_LEFT);
                            row.setPadding(new Insets(4, 0, 4, 0));

                            Label rankLabel = new Label("#" + rank);
                            rankLabel.setMinWidth(28);
                            rankLabel.setStyle(
                                "-fx-text-fill: #00d4ff;" +
                                "-fx-font-size: 11px;" +
                                "-fx-font-weight: bold;" +
                                "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
                            );

                            Label skillLabel = new Label(skill.getSkillName());
                            skillLabel.setStyle(
                                "-fx-text-fill: #cccccc;" +
                                "-fx-font-size: 12px;" +
                                "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
                            );

                            Region filler = new Region();
                            HBox.setHgrow(filler, Priority.ALWAYS);

                            Label freqLabel = new Label(String.valueOf(skill.getFrequency()));
                            freqLabel.setStyle(
                                "-fx-text-fill: #888888;" +
                                "-fx-font-size: 11px;" +
                                "-fx-font-family: 'Segoe UI', Arial, sans-serif;" +
                                "-fx-padding: 0 14 0 0;"
                            );

                            row.getChildren().addAll(rankLabel, skillLabel, filler, freqLabel);
                            skillsListCard.getChildren().add(row);
                            rank++;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        content.setStyle("-fx-background-color: #1e1e1e;");

        Label msgLabel = new Label(message);
        msgLabel.setStyle(
            "-fx-text-fill: #cccccc;" +
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

        Scene dialogScene = new Scene(content, 380, 170);
        dialog.setScene(dialogScene);
        dialog.show();
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

    private void applySecondaryButtonStyle(Button btn, boolean hovered) {
        if (hovered) {
            btn.setStyle(
                "-fx-background-color: #252525;" +
                "-fx-text-fill: #00d4ff;" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 9;" +
                "-fx-border-color: #00d4ff;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 9;" +
                "-fx-cursor: hand;"
            );
        } else {
            btn.setStyle(
                "-fx-background-color: #1e1e1e;" +
                "-fx-text-fill: #00d4ff;" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 9;" +
                "-fx-border-color: #00d4ff;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 9;" +
                "-fx-cursor: hand;"
            );
        }
    }

    private void applyDangerButtonStyle(Button btn, boolean hovered) {
        if (hovered) {
            btn.setStyle(
                "-fx-background-color: #3d1111;" +
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
                "-fx-background-color: #2a0e0e;" +
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