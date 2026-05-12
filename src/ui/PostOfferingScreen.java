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
import controller.CompanyController;
import dao.CompanyDAO;
import domain.Company;
import domain.Offering;
import domain.User;
import service.AIMatchingService;
import utility.SessionManager;
import java.util.Date;

public class PostOfferingScreen extends Application {

    private TextField titleField;
    private TextField roleField;
    private TextField locationField;
    private TextArea descriptionArea;
    private TextField requiredCGPAField;
    private TextField requiredExperienceField;
    private TextField stipendField;
    private ComboBox<String> offeringTypeSelector;
    private Button submitButton;

    private Stage primaryStage;
    private CompanyController companyController;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        this.companyController = new CompanyController(new AIMatchingService());
        initialize();
    }

    public void initialize() {
        primaryStage.setTitle("CareerBridge - Post Offering");
        primaryStage.setResizable(true);
        Scene scene = new Scene(buildRoot(), 1280, 800);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint("");
        primaryStage.setFullScreenExitKeyCombination(javafx.scene.input.KeyCombination.NO_MATCH);
        primaryStage.show();
    }

    private Parent buildRoot() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1A0E0E;");
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

        Label titleLabel = new Label("Post New Offering");
        titleLabel.setStyle(
            "-fx-text-fill: #D4A0A0;" +
            "-fx-font-size: 14px;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );

        Button backButton = new Button("Back to Dashboard");
        backButton.setPrefHeight(36);
        applyTopBarButtonStyle(backButton, false);
        backButton.setOnMouseEntered(e -> applyTopBarButtonStyle(backButton, true));
        backButton.setOnMouseExited(e -> applyTopBarButtonStyle(backButton, false));
        backButton.setOnAction(e -> new CompanyDashboard().start(primaryStage));

        topBar.getChildren().addAll(rocketIcon, brand, spacer, titleLabel, backButton);
        return topBar;
    }

    private ScrollPane buildCenterPanel() {
        VBox outerContainer = new VBox(0);
        outerContainer.setAlignment(Pos.TOP_CENTER);
        outerContainer.setPadding(new Insets(40, 0, 40, 0));

        VBox card = new VBox(22);
        card.setMaxWidth(640);
        card.setPadding(new Insets(40, 44, 40, 44));
        card.setStyle(
            "-fx-background-color: #2D1515;" +
            "-fx-background-radius: 16;" +
            "-fx-border-color: #4A2020;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 16;"
        );
        DropShadow cardShadow = new DropShadow();
        cardShadow.setColor(Color.web("#000000", 0.5));
        cardShadow.setRadius(40);
        cardShadow.setSpread(0.03);
        card.setEffect(cardShadow);

        Text heading = new Text("Post a Job or Internship");
        heading.setStyle(
            "-fx-font-family: 'Segoe UI', 'Helvetica', Arial, sans-serif;" +
            "-fx-font-size: 26px;" +
            "-fx-font-weight: bold;" +
            "-fx-fill: #ffffff;"
        );

        Text sub = new Text("Fill in the details below to publish your offering");
        sub.setStyle(
            "-fx-font-family: 'Segoe UI', 'Helvetica', Arial, sans-serif;" +
            "-fx-font-size: 13px;" +
            "-fx-fill: #8A6060;"
        );

        Region gap = new Region();
        gap.setPrefHeight(4);

        VBox typeBox = new VBox(7);
        Label typeLbl = new Label("Offering Type");
        typeLbl.setStyle(
            "-fx-text-fill: #D4A0A0;" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );
        offeringTypeSelector = new ComboBox<>();
        offeringTypeSelector.getItems().addAll("Job", "Internship");
        offeringTypeSelector.setPromptText("Select type");
        offeringTypeSelector.setMaxWidth(Double.MAX_VALUE);
        offeringTypeSelector.setPrefHeight(42);
        applyComboBaseStyle();
        offeringTypeSelector.focusedProperty().addListener((obs, ov, nv) -> {
            if (nv) {
                offeringTypeSelector.setStyle(
                    "-fx-background-color: #1F0D0D;" +
                    "-fx-border-color: #E53935;" +
                    "-fx-border-width: 1.5;" +
                    "-fx-border-radius: 9;" +
                    "-fx-background-radius: 9;" +
                    "-fx-font-size: 13px;"
                );
            } else {
                applyComboBaseStyle();
            }
        });
        typeBox.getChildren().addAll(typeLbl, offeringTypeSelector);

        VBox titleBox = buildInputField("Title", "e.g. Software Engineer Intern");
        titleField = (TextField) titleBox.getChildren().get(1);

        VBox roleBox = buildInputField("Role", "e.g. Backend Developer");
        roleField = (TextField) roleBox.getChildren().get(1);

        VBox locationBox = buildInputField("Location", "e.g. Lahore, Karachi, Remote");
        locationField = (TextField) locationBox.getChildren().get(1);

        VBox descBox = buildTextAreaField("Job Description", "Describe the role, responsibilities, and required skills");
        descriptionArea = (TextArea) descBox.getChildren().get(1);

        HBox twoColRow1 = new HBox(16);
        twoColRow1.setAlignment(Pos.CENTER_LEFT);

        VBox cgpaBox = buildInputField("Required CGPA", "e.g. 3.0");
        requiredCGPAField = (TextField) cgpaBox.getChildren().get(1);
        HBox.setHgrow(cgpaBox, Priority.ALWAYS);

        VBox expBox = buildInputField("Required Experience", "e.g. Fresh, 1-2 years");
        requiredExperienceField = (TextField) expBox.getChildren().get(1);
        HBox.setHgrow(expBox, Priority.ALWAYS);

        twoColRow1.getChildren().addAll(cgpaBox, expBox);

        VBox stipendBox = buildInputField("Salary", "e.g. $45,000 - $121,000 a year");
        stipendField = (TextField) stipendBox.getChildren().get(1);

        Region buttonGap = new Region();
        buttonGap.setPrefHeight(8);

        submitButton = new Button("Publish Offering  \u2192");
        submitButton.setMaxWidth(Double.MAX_VALUE);
        submitButton.setPrefHeight(48);
        applyPrimaryButtonStyle(submitButton, false);
        submitButton.setOnMouseEntered(e -> applyPrimaryButtonStyle(submitButton, true));
        submitButton.setOnMouseExited(e -> applyPrimaryButtonStyle(submitButton, false));
        submitButton.setOnAction(e -> handleSubmit());

        card.getChildren().addAll(
            heading, sub, gap,
            typeBox, titleBox, roleBox, locationBox,
            descBox, twoColRow1, stipendBox,
            buttonGap, submitButton
        );

        outerContainer.getChildren().add(card);

        ScrollPane scrollPane = new ScrollPane(outerContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #1A0E0E; -fx-background-color: #1A0E0E;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        return scrollPane;
    }

    private VBox buildInputField(String label, String prompt) {
        VBox box = new VBox(7);

        Label lbl = new Label(label);
        lbl.setStyle(
            "-fx-text-fill: #D4A0A0;" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );

        String baseStyle =
            "-fx-background-color: #1A0E0E;" +
            "-fx-text-fill: #e0e0e0;" +
            "-fx-prompt-text-fill: #5A3535;" +
            "-fx-border-color: #4A2020;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 9;" +
            "-fx-background-radius: 9;" +
            "-fx-padding: 11 14 11 14;" +
            "-fx-font-size: 13px;";

        String focusedStyle =
            "-fx-background-color: #1F0D0D;" +
            "-fx-text-fill: #e0e0e0;" +
            "-fx-prompt-text-fill: #5A3535;" +
            "-fx-border-color: #E53935;" +
            "-fx-border-width: 1.5;" +
            "-fx-border-radius: 9;" +
            "-fx-background-radius: 9;" +
            "-fx-padding: 11 14 11 14;" +
            "-fx-font-size: 13px;";

        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle(baseStyle);
        tf.focusedProperty().addListener((obs, ov, nv) -> tf.setStyle(nv ? focusedStyle : baseStyle));

        box.getChildren().addAll(lbl, tf);
        return box;
    }

    private VBox buildTextAreaField(String label, String prompt) {
        VBox box = new VBox(7);

        Label lbl = new Label(label);
        lbl.setStyle(
            "-fx-text-fill: #D4A0A0;" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );

        String baseStyle =
            "-fx-control-inner-background: #1A0E0E;" +
            "-fx-text-fill: #e0e0e0;" +
            "-fx-prompt-text-fill: #5A3535;" +
            "-fx-border-color: #4A2020;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 9;" +
            "-fx-background-radius: 9;" +
            "-fx-padding: 8 10 8 10;" +
            "-fx-font-size: 13px;";

        String focusedStyle =
            "-fx-control-inner-background: #1F0D0D;" +
            "-fx-text-fill: #e0e0e0;" +
            "-fx-prompt-text-fill: #5A3535;" +
            "-fx-border-color: #E53935;" +
            "-fx-border-width: 1.5;" +
            "-fx-border-radius: 9;" +
            "-fx-background-radius: 9;" +
            "-fx-padding: 8 10 8 10;" +
            "-fx-font-size: 13px;";

        TextArea ta = new TextArea();
        ta.setPromptText(prompt);
        ta.setPrefRowCount(4);
        ta.setWrapText(true);
        ta.setStyle(baseStyle);
        ta.focusedProperty().addListener((obs, ov, nv) -> ta.setStyle(nv ? focusedStyle : baseStyle));

        box.getChildren().addAll(lbl, ta);
        return box;
    }

    public void handleSubmit() {
        if (!validateInputs()) return;

        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            showDialog("Session expired. Please log in again.", false);
            return;
        }

        CompanyDAO companyDAO = new CompanyDAO();
        Company company = (Company) companyDAO.findByID(currentUser.getUserID());
        if (company == null) {
            showDialog("Company profile not found. Please contact admin.", false);
            return;
        }

        Offering offering = new Offering();
        offering.setOfferingType(offeringTypeSelector.getValue().toLowerCase());
        offering.setTitle(titleField.getText().trim());
        offering.setRole(roleField.getText().trim());
        offering.setLocation(locationField.getText().trim());
        offering.setJobDescription(descriptionArea.getText().trim());
        offering.setStatus("open");
        offering.setCompanyID(company.getCompanyID());
        offering.setSource("platform");
        offering.setPostedDate(new Date());

        String cgpaText = requiredCGPAField.getText().trim();
        if (!cgpaText.isEmpty()) {
            try { offering.setRequiredCGPA(Double.parseDouble(cgpaText)); }
            catch (NumberFormatException ignored) {}
        }

        offering.setRequiredExperience(requiredExperienceField.getText().trim());

        String stipendText = stipendField.getText().trim();
        if (!stipendText.isEmpty()) {
            offering.setSalaryText(stipendText);
            offering.setStipend(parseSalaryNumber(stipendText));
        }

        boolean posted = companyController.postOffering(offering);

        if (posted) {
            showConfirmation();
        } else {
            showDialog("Failed to post offering. Please try again.", false);
        }
    }

    private boolean validateInputs() {
        if (offeringTypeSelector.getValue() == null) {
            showDialog("Please select an offering type.", false);
            return false;
        }
        if (titleField.getText().trim().isEmpty()) {
            showDialog("Title is required.", false);
            return false;
        }
        if (roleField.getText().trim().isEmpty()) {
            showDialog("Role is required.", false);
            return false;
        }
        if (locationField.getText().trim().isEmpty()) {
            showDialog("Location is required.", false);
            return false;
        }
        if (descriptionArea.getText().trim().isEmpty()) {
            showDialog("Job description is required.", false);
            return false;
        }
        String cgpaText = requiredCGPAField.getText().trim();
        if (!cgpaText.isEmpty()) {
            try {
                double cgpa = Double.parseDouble(cgpaText);
                if (cgpa < 0.0 || cgpa > 4.0) {
                    showDialog("CGPA must be between 0.0 and 4.0.", false);
                    return false;
                }
            } catch (NumberFormatException e) {
                showDialog("Please enter a valid numeric CGPA.", false);
                return false;
            }
        }
        return true;
    }

    private void showConfirmation() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(primaryStage);
        dialog.setTitle("CareerBridge");
        dialog.setResizable(false);

        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(32, 36, 28, 36));
        content.setStyle("-fx-background-color: #2D1515;");

        Label checkIcon = new Label("\u2713");
        checkIcon.setStyle(
            "-fx-text-fill: #00ff88;" +
            "-fx-font-size: 36px;" +
            "-fx-font-weight: bold;"
        );

        Label msgLabel = new Label("Offering published successfully!");
        msgLabel.setStyle(
            "-fx-text-fill: #D4A0A0;" +
            "-fx-font-size: 14px;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );
        msgLabel.setWrapText(true);
        msgLabel.setTextAlignment(TextAlignment.CENTER);

        Button okButton = new Button("Back to Dashboard");
        okButton.setPrefWidth(180);
        okButton.setPrefHeight(40);
        applyPrimaryButtonStyle(okButton, false);
        okButton.setOnMouseEntered(e -> applyPrimaryButtonStyle(okButton, true));
        okButton.setOnMouseExited(e -> applyPrimaryButtonStyle(okButton, false));
        okButton.setOnAction(e -> {
            dialog.close();
            new CompanyDashboard().start(primaryStage);
        });

        content.getChildren().addAll(checkIcon, msgLabel, okButton);

        Scene dialogScene = new Scene(content, 360, 200);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    private void showDialog(String message, boolean success) {
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
            "-fx-text-fill: " + (success ? "#00ff88" : "#FF6B6B") + ";" +
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

    private void applyTopBarButtonStyle(Button btn, boolean hovered) {
        if (hovered) {
            btn.setStyle(
                "-fx-background-color: linear-gradient(to right, #C62828, #B71C1C);" +
                "-fx-text-fill: #ffffff;" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"
            );
            DropShadow glow = new DropShadow();
            glow.setColor(Color.web("#E53935", 0.35));
            glow.setRadius(12);
            btn.setEffect(glow);
        } else {
            btn.setStyle(
                "-fx-background-color: linear-gradient(to right, #E53935, #C62828);" +
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
                "-fx-background-color: linear-gradient(to right, #C62828, #B71C1C);" +
                "-fx-text-fill: #ffffff;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;"
            );
            DropShadow glow = new DropShadow();
            glow.setColor(Color.web("#E53935", 0.45));
            glow.setRadius(18);
            btn.setEffect(glow);
        } else {
            btn.setStyle(
                "-fx-background-color: linear-gradient(to right, #E53935, #C62828);" +
                "-fx-text-fill: #0a0a0a;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;"
            );
            btn.setEffect(null);
        }
    }

    private void applyComboBaseStyle() {
        offeringTypeSelector.setStyle(
            "-fx-background-color: #1A0E0E;" +
            "-fx-border-color: #4A2020;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 9;" +
            "-fx-background-radius: 9;" +
            "-fx-font-size: 13px;"
        );
    }

    private double parseSalaryNumber(String salary) {
        if (salary == null || salary.isEmpty()) return 0.0;
        try {
            String cleaned = salary.replaceAll("[^0-9,\\-]", "").trim();
            if (cleaned.contains("-")) {
                String[] parts = cleaned.split("-");
                double low = Double.parseDouble(parts[0].replace(",", "").trim());
                double high = Double.parseDouble(parts[1].replace(",", "").trim());
                return (low + high) / 2.0;
            }
            return Double.parseDouble(cleaned.replace(",", "").trim());
        } catch (Exception e) {
            return 0.0;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}