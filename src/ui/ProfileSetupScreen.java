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
import javafx.util.Duration;
import javafx.animation.PauseTransition;
import controller.JobSeekerController;
import domain.JobSeeker;
import domain.Profile;
import domain.User;
import service.AIMatchingService;
import utility.CVTextExtractor;
import utility.SessionManager;
import java.io.File;
import java.util.Date;

public class ProfileSetupScreen extends Application {

    private TextField educationField;
    private TextField cgpaField;
    private TextField locationField;
    private TextArea skillsField;
    private TextArea experienceField;
    private Button cvUploadButton;
    private Button saveButton;
    private Label cvFileLabel;
    private String selectedCVPath;
    private String extractedCVText;

    private Stage primaryStage;
    private JobSeekerController seekerController;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        this.seekerController = new JobSeekerController(new AIMatchingService());
        initialize();
    }

    public void initialize() {
        primaryStage.setTitle("CareerBridge - Profile Setup");
        primaryStage.setResizable(true);
        Scene scene = new Scene(buildRoot(), 1280, 800);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint("");
        primaryStage.setFullScreenExitKeyCombination(javafx.scene.input.KeyCombination.NO_MATCH);
        primaryStage.show();
        loadExistingProfile();
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

        Label titleLabel = new Label("Profile Setup");
        titleLabel.setStyle(
            "-fx-text-fill: #B0C4DE;" +
            "-fx-font-size: 14px;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );

        Button backButton = new Button("Back to Dashboard");
        backButton.setPrefHeight(36);
        applyTopBarButtonStyle(backButton, false);
        backButton.setOnMouseEntered(e -> applyTopBarButtonStyle(backButton, true));
        backButton.setOnMouseExited(e -> applyTopBarButtonStyle(backButton, false));
        backButton.setOnAction(e -> new JobSeekerDashboard().start(primaryStage));

        topBar.getChildren().addAll(rocketIcon, brand, spacer, titleLabel, backButton);
        return topBar;
    }

    private ScrollPane buildCenterPanel() {
        VBox outerContainer = new VBox(0);
        outerContainer.setAlignment(Pos.TOP_CENTER);
        outerContainer.setPadding(new Insets(40, 0, 40, 0));

        VBox card = new VBox(24);
        card.setMaxWidth(620);
        card.setPadding(new Insets(40, 44, 40, 44));
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

        Text heading = new Text("Setup Your Profile");
        heading.setStyle(
            "-fx-font-family: 'Segoe UI', 'Helvetica', Arial, sans-serif;" +
            "-fx-font-size: 26px;" +
            "-fx-font-weight: bold;" +
            "-fx-fill: #ffffff;"
        );

        Text sub = new Text("Complete your profile to get matched with the best opportunities");
        sub.setStyle(
            "-fx-font-family: 'Segoe UI', 'Helvetica', Arial, sans-serif;" +
            "-fx-font-size: 13px;" +
            "-fx-fill: #5A7A9A;"
        );

        Region gap = new Region();
        gap.setPrefHeight(4);

        VBox educationBox = buildInputField("Education", "e.g. BS Computer Science - FAST NUCES");
        educationField = (TextField) educationBox.getChildren().get(1);

        VBox cgpaBox = buildInputField("CGPA", "e.g. 3.45");
        cgpaField = (TextField) cgpaBox.getChildren().get(1);

        VBox locationBox = buildInputField("Location", "e.g. Lahore, Karachi, Islamabad");
        locationField = (TextField) locationBox.getChildren().get(1);

        VBox skillsBox = buildTextAreaField("Skills", "e.g. Java, Python, Spring Boot, React, SQL");
        skillsField = (TextArea) skillsBox.getChildren().get(1);

        VBox experienceBox = buildTextAreaField("Experience", "e.g. 6-month internship at Systems Ltd as Backend Developer");
        experienceField = (TextArea) experienceBox.getChildren().get(1);

        VBox cvSection = new VBox(10);
        Label cvLabel = new Label("CV / Resume");
        cvLabel.setStyle(
            "-fx-text-fill: #8EAFC4;" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );

        HBox cvRow = new HBox(12);
        cvRow.setAlignment(Pos.CENTER_LEFT);

        cvUploadButton = new Button("Choose File");
        cvUploadButton.setPrefHeight(40);
        cvUploadButton.setPrefWidth(140);
        applyCvButtonStyle(cvUploadButton, false);
        cvUploadButton.setOnMouseEntered(e -> applyCvButtonStyle(cvUploadButton, true));
        cvUploadButton.setOnMouseExited(e -> applyCvButtonStyle(cvUploadButton, false));
        cvUploadButton.setOnAction(e -> handleCVUpload());

        cvFileLabel = new Label("No file selected");
        cvFileLabel.setStyle(
            "-fx-text-fill: #5A7A9A;" +
            "-fx-font-size: 12px;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );

        cvRow.getChildren().addAll(cvUploadButton, cvFileLabel);
        cvSection.getChildren().addAll(cvLabel, cvRow);

        Region buttonGap = new Region();
        buttonGap.setPrefHeight(8);

        saveButton = new Button("Save Profile  \u2192");
        saveButton.setMaxWidth(Double.MAX_VALUE);
        saveButton.setPrefHeight(48);
        applyPrimaryButtonStyle(saveButton, false);
        saveButton.setOnMouseEntered(e -> applyPrimaryButtonStyle(saveButton, true));
        saveButton.setOnMouseExited(e -> applyPrimaryButtonStyle(saveButton, false));
        saveButton.setOnAction(e -> handleSaveProfile());

        card.getChildren().addAll(
            heading, sub, gap,
            educationBox, cgpaBox, locationBox, skillsBox, experienceBox,
            cvSection, buttonGap, saveButton
        );

        outerContainer.getChildren().add(card);

        ScrollPane scrollPane = new ScrollPane(outerContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #0A1929; -fx-background-color: #0A1929;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        return scrollPane;
    }

    private VBox buildInputField(String label, String prompt) {
        VBox box = new VBox(7);

        Label lbl = new Label(label);
        lbl.setStyle(
            "-fx-text-fill: #8EAFC4;" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );

        String baseStyle =
            "-fx-background-color: #0A1929;" +
            "-fx-text-fill: #e0e0e0;" +
            "-fx-prompt-text-fill: #3A5068;" +
            "-fx-border-color: #1E3A5F;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 9;" +
            "-fx-background-radius: 9;" +
            "-fx-padding: 11 14 11 14;" +
            "-fx-font-size: 13px;";

        String focusedStyle =
            "-fx-background-color: #0D1F33;" +
            "-fx-text-fill: #e0e0e0;" +
            "-fx-prompt-text-fill: #3A5068;" +
            "-fx-border-color: #1E90FF;" +
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
            "-fx-text-fill: #8EAFC4;" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );

        String baseStyle =
            "-fx-control-inner-background: #0A1929;" +
            "-fx-text-fill: #e0e0e0;" +
            "-fx-prompt-text-fill: #3A5068;" +
            "-fx-border-color: #1E3A5F;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 9;" +
            "-fx-background-radius: 9;" +
            "-fx-padding: 8 10 8 10;" +
            "-fx-font-size: 13px;";

        String focusedStyle =
            "-fx-control-inner-background: #0D1F33;" +
            "-fx-text-fill: #e0e0e0;" +
            "-fx-prompt-text-fill: #3A5068;" +
            "-fx-border-color: #1E90FF;" +
            "-fx-border-width: 1.5;" +
            "-fx-border-radius: 9;" +
            "-fx-background-radius: 9;" +
            "-fx-padding: 8 10 8 10;" +
            "-fx-font-size: 13px;";

        TextArea ta = new TextArea();
        ta.setPromptText(prompt);
        ta.setPrefRowCount(3);
        ta.setWrapText(true);
        ta.setStyle(baseStyle);
        ta.focusedProperty().addListener((obs, ov, nv) -> ta.setStyle(nv ? focusedStyle : baseStyle));

        box.getChildren().addAll(lbl, ta);
        return box;
    }

    public void handleCVUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select CV / Resume");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
            new FileChooser.ExtensionFilter("Word Documents", "*.doc", "*.docx"),
            new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            selectedCVPath = file.getAbsolutePath();
            cvFileLabel.setText(file.getName());
            cvFileLabel.setStyle(
                "-fx-text-fill: #1E90FF;" +
                "-fx-font-size: 12px;" +
                "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
            );
            extractedCVText = CVTextExtractor.extract(selectedCVPath);
            if (extractedCVText != null && !extractedCVText.isEmpty()) {
                cvFileLabel.setText(file.getName() + "  (parsed)");
            }
        }
    }

    public void handleSaveProfile() {
        if (!validateInputs()) return;

        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            showDialog("Session expired. Please log in again.", false);
            return;
        }

        int seekerID;
        if (currentUser instanceof domain.JobSeeker) {
            seekerID = ((domain.JobSeeker) currentUser).getSeekerID();
        } else {
            showDialog("Invalid user type.", false);
            return;
        }

        Profile profile = new Profile();
        profile.setSeekerID(seekerID);
        profile.setEducation(educationField.getText().trim());
        profile.setCGPA(Double.parseDouble(cgpaField.getText().trim()));
        profile.setSkills(skillsField.getText().trim());
        profile.setLocation(locationField.getText().trim());
        profile.setExperience(experienceField.getText().trim());
        profile.setCVFile(selectedCVPath != null ? selectedCVPath : "");
        profile.setProfileCreatedDate(new Date());

        if (extractedCVText != null && !extractedCVText.isEmpty()) {
            profile.setCvText(extractedCVText);
        } else if (selectedCVPath != null && !selectedCVPath.isEmpty()) {
            String text = CVTextExtractor.extract(selectedCVPath);
            profile.setCvText(text);
        }

        boolean saved = seekerController.saveProfile(profile);

        if (saved) {
            showSuccessMessage();
        } else {
            showDialog("Failed to save profile. Please try again.", false);
        }
    }

    private boolean validateInputs() {
        if (educationField.getText().trim().isEmpty()) {
            showDialog("Education field is required.", false);
            return false;
        }
        String cgpaText = cgpaField.getText().trim();
        if (cgpaText.isEmpty()) {
            showDialog("CGPA is required.", false);
            return false;
        }
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
        if (skillsField.getText().trim().isEmpty()) {
            showDialog("Skills field is required.", false);
            return false;
        }
        return true;
    }

    public void loadExistingProfile() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) return;

        int seekerID = (currentUser instanceof domain.JobSeeker)
            ? ((domain.JobSeeker) currentUser).getSeekerID()
            : currentUser.getUserID();
        Profile profile = seekerController.getProfile(seekerID);
        if (profile != null) {
            educationField.setText(profile.getEducation() != null ? profile.getEducation() : "");
            cgpaField.setText(profile.getCGPA() > 0 ? String.valueOf(profile.getCGPA()) : "");
            locationField.setText(profile.getLocation() != null ? profile.getLocation() : "");
            skillsField.setText(profile.getSkills() != null ? profile.getSkills() : "");
            experienceField.setText(profile.getExperience() != null ? profile.getExperience() : "");
            if (profile.getCVFile() != null && !profile.getCVFile().isEmpty()) {
                selectedCVPath = profile.getCVFile();
                File f = new File(selectedCVPath);
                cvFileLabel.setText(f.getName());
                cvFileLabel.setStyle(
                    "-fx-text-fill: #1E90FF;" +
                    "-fx-font-size: 12px;" +
                    "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
                );
            }
            if (profile.getCvText() != null && !profile.getCvText().isEmpty()) {
                extractedCVText = profile.getCvText();
            }
        }
    }

    private void showSuccessMessage() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(primaryStage);
        dialog.setTitle("CareerBridge");
        dialog.setResizable(false);

        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(32, 36, 28, 36));
        content.setStyle("-fx-background-color: #0F2639;");

        Label checkIcon = new Label("\u2713");
        checkIcon.setStyle(
            "-fx-text-fill: #00ff88;" +
            "-fx-font-size: 36px;" +
            "-fx-font-weight: bold;"
        );

        Label msgLabel = new Label("Profile saved successfully!");
        msgLabel.setStyle(
            "-fx-text-fill: #B0C4DE;" +
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
            new JobSeekerDashboard().start(primaryStage);
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
        content.setStyle("-fx-background-color: #0F2639;");

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

    private void applyCvButtonStyle(Button btn, boolean hovered) {
        if (hovered) {
            btn.setStyle(
                "-fx-background-color: #0D3B66;" +
                "-fx-text-fill: #1E90FF;" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 9;" +
                "-fx-border-color: #1E90FF;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 9;" +
                "-fx-cursor: hand;"
            );
        } else {
            btn.setStyle(
                "-fx-background-color: #132F4C;" +
                "-fx-text-fill: #1E90FF;" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 9;" +
                "-fx-border-color: #1E90FF;" +
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