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
import controller.JobSeekerController;
import domain.JobSeeker;
import domain.Profile;
import domain.User;
import service.AIMatchingService;
import utility.SessionManager;

public class ViewProfileScreen extends Application {

    private int seekerID;
    private Runnable onBack;
    private Stage primaryStage;
    private JobSeekerController seekerController;

    private boolean isCompanyViewer;
    private int offeringID;

    public ViewProfileScreen() {
        this.seekerID = -1;
    }

    public ViewProfileScreen(int seekerID) {
        this.seekerID = seekerID;
    }

    public ViewProfileScreen(int seekerID, int offeringID) {
        this.seekerID = seekerID;
        this.offeringID = offeringID;
        this.isCompanyViewer = true;
    }

    public void setOnBack(Runnable onBack) {
        this.onBack = onBack;
    }

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        this.seekerController = new JobSeekerController(new AIMatchingService());

        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null && !(currentUser instanceof domain.JobSeeker)) {
            isCompanyViewer = true;
        }

        initialize();
    }

    public void initialize() {
        primaryStage.setTitle("CareerBridge - View Profile");
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
        if (isCompanyViewer) {
            root.setStyle("-fx-background-color: #1A0E0E;");
        } else {
            root.setStyle("-fx-background-color: #0A1929;");
        }
        root.setTop(buildTopBar());

        if (seekerID > 0) {
            root.setCenter(buildProfileView());
        } else {
            root.setCenter(buildSearchView());
        }
        return root;
    }

    private HBox buildTopBar() {
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(14, 28, 14, 28));
        topBar.setSpacing(14);

        if (isCompanyViewer) {
            topBar.setStyle(
                "-fx-background-color: #2D1515;" +
                "-fx-border-color: #4A2020;" +
                "-fx-border-width: 0 0 1 0;"
            );
        } else {
            topBar.setStyle(
                "-fx-background-color: #0F2639;" +
                "-fx-border-color: #1E3A5F;" +
                "-fx-border-width: 0 0 1 0;"
            );
        }

        Label rocketIcon = new Label("🚀");
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

        String subtitleColor = isCompanyViewer ? "#D4A0A0" : "#B0C4DE";
        Label titleLabel = new Label("View Profile");
        titleLabel.setStyle(
            "-fx-text-fill: " + subtitleColor + ";" +
            "-fx-font-size: 14px;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );

        Button backButton = new Button("Back");
        backButton.setPrefHeight(36);
        applyBackButtonStyle(backButton, false);
        backButton.setOnMouseEntered(e -> applyBackButtonStyle(backButton, true));
        backButton.setOnMouseExited(e -> applyBackButtonStyle(backButton, false));
        backButton.setOnAction(e -> handleBack());

        topBar.getChildren().addAll(rocketIcon, brand, spacer, titleLabel, backButton);
        return topBar;
    }

    private ScrollPane buildSearchView() {
        VBox outerContainer = new VBox(0);
        outerContainer.setAlignment(Pos.TOP_CENTER);
        outerContainer.setPadding(new Insets(60, 0, 40, 0));

        VBox card = new VBox(24);
        card.setMaxWidth(520);
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

        Text heading = new Text("Search Profile");
        heading.setStyle(
            "-fx-font-family: 'Segoe UI', 'Helvetica', Arial, sans-serif;" +
            "-fx-font-size: 26px;" +
            "-fx-font-weight: bold;" +
            "-fx-fill: #ffffff;"
        );

        Text sub = new Text("Enter the email address of a Job Seeker to view their profile");
        sub.setStyle(
            "-fx-font-family: 'Segoe UI', 'Helvetica', Arial, sans-serif;" +
            "-fx-font-size: 13px;" +
            "-fx-fill: #5A7A9A;"
        );

        Region gap = new Region();
        gap.setPrefHeight(4);

        VBox emailBox = new VBox(7);
        Label emailLabel = new Label("Email Address");
        emailLabel.setStyle(
            "-fx-text-fill: #8EAFC4;" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );

        String baseInputStyle =
            "-fx-background-color: #0A1929;" +
            "-fx-text-fill: #e0e0e0;" +
            "-fx-prompt-text-fill: #3A5068;" +
            "-fx-border-color: #1E3A5F;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 9;" +
            "-fx-background-radius: 9;" +
            "-fx-padding: 11 14 11 14;" +
            "-fx-font-size: 13px;";

        String focusedInputStyle =
            "-fx-background-color: #0D1F33;" +
            "-fx-text-fill: #e0e0e0;" +
            "-fx-prompt-text-fill: #3A5068;" +
            "-fx-border-color: #1E90FF;" +
            "-fx-border-width: 1.5;" +
            "-fx-border-radius: 9;" +
            "-fx-background-radius: 9;" +
            "-fx-padding: 11 14 11 14;" +
            "-fx-font-size: 13px;";

        TextField emailField = new TextField();
        emailField.setPromptText("e.g. johndoe@gmail.com");
        emailField.setStyle(baseInputStyle);
        emailField.focusedProperty().addListener((obs, ov, nv) -> emailField.setStyle(nv ? focusedInputStyle : baseInputStyle));
        emailBox.getChildren().addAll(emailLabel, emailField);

        Label errorLabel = new Label();
        errorLabel.setStyle(
            "-fx-text-fill: #FF6B6B;" +
            "-fx-font-size: 12px;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );
        errorLabel.setWrapText(true);
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        Region buttonGap = new Region();
        buttonGap.setPrefHeight(8);

        Button searchButton = new Button("View Profile  →");
        searchButton.setMaxWidth(Double.MAX_VALUE);
        searchButton.setPrefHeight(48);
        applySeekerPrimaryStyle(searchButton, false);
        searchButton.setOnMouseEntered(e -> applySeekerPrimaryStyle(searchButton, true));
        searchButton.setOnMouseExited(e -> applySeekerPrimaryStyle(searchButton, false));
        searchButton.setOnAction(e -> {
            String email = emailField.getText().trim();
            if (email.isEmpty()) {
                errorLabel.setText("Please enter an email address.");
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
                return;
            }

            JobSeeker seeker = seekerController.findSeekerByEmail(email);
            if (seeker == null) {
                errorLabel.setText("No Job Seeker found with email: " + email);
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
                return;
            }

            this.seekerID = seeker.getSeekerID();
            BorderPane root = (BorderPane) primaryStage.getScene().getRoot();
            root.setCenter(buildProfileView());
        });

        card.getChildren().addAll(heading, sub, gap, emailBox, errorLabel, buttonGap, searchButton);
        outerContainer.getChildren().add(card);

        ScrollPane scrollPane = new ScrollPane(outerContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #0A1929; -fx-background-color: #0A1929;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        return scrollPane;
    }

    private ScrollPane buildProfileView() {
        JobSeeker seeker = seekerController.findSeekerBySeekerID(seekerID);
        Profile profile = seekerController.getProfile(seekerID);

        String bgColor = isCompanyViewer ? "#1A0E0E" : "#0A1929";
        String cardBg = isCompanyViewer ? "#2D1515" : "#0F2639";
        String cardBorder = isCompanyViewer ? "#4A2020" : "#1E3A5F";
        String labelColor = isCompanyViewer ? "#D4A0A0" : "#8EAFC4";
        String valueColor = "#e0e0e0";
        String mutedColor = isCompanyViewer ? "#8A6060" : "#5A7A9A";
        String accentColor = isCompanyViewer ? "#E53935" : "#1E90FF";
        String fieldBg = isCompanyViewer ? "#1A0E0E" : "#0A1929";
        String fieldBorder = isCompanyViewer ? "#4A2020" : "#1E3A5F";

        VBox outerContainer = new VBox(0);
        outerContainer.setAlignment(Pos.TOP_CENTER);
        outerContainer.setPadding(new Insets(40, 0, 40, 0));

        VBox card = new VBox(24);
        card.setMaxWidth(620);
        card.setPadding(new Insets(40, 44, 40, 44));
        card.setStyle(
            "-fx-background-color: " + cardBg + ";" +
            "-fx-background-radius: 16;" +
            "-fx-border-color: " + cardBorder + ";" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 16;"
        );
        DropShadow cardShadow = new DropShadow();
        cardShadow.setColor(Color.web("#000000", 0.5));
        cardShadow.setRadius(40);
        cardShadow.setSpread(0.03);
        card.setEffect(cardShadow);

        String seekerName = (seeker != null && seeker.getName() != null) ? seeker.getName() : "Job Seeker #" + seekerID;

        Text heading = new Text(seekerName);
        heading.setStyle(
            "-fx-font-family: 'Segoe UI', 'Helvetica', Arial, sans-serif;" +
            "-fx-font-size: 26px;" +
            "-fx-font-weight: bold;" +
            "-fx-fill: #ffffff;"
        );

        String seekerEmail = (seeker != null && seeker.getEmail() != null) ? seeker.getEmail() : "--";
        Text emailText = new Text(seekerEmail);
        emailText.setStyle(
            "-fx-font-family: 'Segoe UI', 'Helvetica', Arial, sans-serif;" +
            "-fx-font-size: 14px;" +
            "-fx-fill: " + accentColor + ";"
        );

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: " + cardBorder + ";");

        card.getChildren().addAll(heading, emailText, sep);

        if (profile == null) {
            Label noProfile = new Label("This user has not set up their profile yet.");
            noProfile.setStyle(
                "-fx-text-fill: " + mutedColor + ";" +
                "-fx-font-size: 14px;" +
                "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
            );
            noProfile.setWrapText(true);
            card.getChildren().add(noProfile);
        } else {
            card.getChildren().add(buildReadOnlyField("Education",
                profile.getEducation() != null ? profile.getEducation() : "--",
                labelColor, valueColor, fieldBg, fieldBorder));

            card.getChildren().add(buildReadOnlyField("CGPA",
                profile.getCGPA() > 0 ? String.format("%.2f", profile.getCGPA()) : "--",
                labelColor, valueColor, fieldBg, fieldBorder));

            card.getChildren().add(buildReadOnlyField("Location",
                profile.getLocation() != null && !profile.getLocation().isEmpty() ? profile.getLocation() : "--",
                labelColor, valueColor, fieldBg, fieldBorder));

            card.getChildren().add(buildReadOnlyArea("Skills",
                profile.getSkills() != null ? profile.getSkills() : "--",
                labelColor, valueColor, fieldBg, fieldBorder));

            card.getChildren().add(buildReadOnlyArea("Experience",
                profile.getExperience() != null && !profile.getExperience().isEmpty() ? profile.getExperience() : "--",
                labelColor, valueColor, fieldBg, fieldBorder));

            String cvDisplay = "--";
            if (profile.getCVFile() != null && !profile.getCVFile().isEmpty()) {
                java.io.File f = new java.io.File(profile.getCVFile());
                cvDisplay = f.getName();
            }
            card.getChildren().add(buildReadOnlyField("CV / Resume",
                cvDisplay, labelColor, valueColor, fieldBg, fieldBorder));
        }

        Region buttonGap = new Region();
        buttonGap.setPrefHeight(8);

        Button backBtn = new Button("Back");
        backBtn.setMaxWidth(Double.MAX_VALUE);
        backBtn.setPrefHeight(48);
        if (isCompanyViewer) {
            applyCompanyPrimaryStyle(backBtn, false);
            backBtn.setOnMouseEntered(e -> applyCompanyPrimaryStyle(backBtn, true));
            backBtn.setOnMouseExited(e -> applyCompanyPrimaryStyle(backBtn, false));
        } else {
            applySeekerPrimaryStyle(backBtn, false);
            backBtn.setOnMouseEntered(e -> applySeekerPrimaryStyle(backBtn, true));
            backBtn.setOnMouseExited(e -> applySeekerPrimaryStyle(backBtn, false));
        }
        backBtn.setOnAction(e -> handleBack());

        card.getChildren().addAll(buttonGap, backBtn);

        outerContainer.getChildren().add(card);

        ScrollPane scrollPane = new ScrollPane(outerContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: " + bgColor + "; -fx-background-color: " + bgColor + ";");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        return scrollPane;
    }

    private VBox buildReadOnlyField(String label, String value,
                                     String labelColor, String valueColor,
                                     String fieldBg, String fieldBorder) {
        VBox box = new VBox(7);

        Label lbl = new Label(label);
        lbl.setStyle(
            "-fx-text-fill: " + labelColor + ";" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );

        TextField tf = new TextField(value);
        tf.setEditable(false);
        tf.setStyle(
            "-fx-background-color: " + fieldBg + ";" +
            "-fx-text-fill: " + valueColor + ";" +
            "-fx-border-color: " + fieldBorder + ";" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 9;" +
            "-fx-background-radius: 9;" +
            "-fx-padding: 11 14 11 14;" +
            "-fx-font-size: 13px;" +
            "-fx-opacity: 1.0;"
        );

        box.getChildren().addAll(lbl, tf);
        return box;
    }

    private VBox buildReadOnlyArea(String label, String value,
                                    String labelColor, String valueColor,
                                    String fieldBg, String fieldBorder) {
        VBox box = new VBox(7);

        Label lbl = new Label(label);
        lbl.setStyle(
            "-fx-text-fill: " + labelColor + ";" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );

        TextArea ta = new TextArea(value);
        ta.setEditable(false);
        ta.setWrapText(true);
        ta.setPrefRowCount(3);
        ta.setStyle(
            "-fx-control-inner-background: " + fieldBg + ";" +
            "-fx-text-fill: " + valueColor + ";" +
            "-fx-border-color: " + fieldBorder + ";" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 9;" +
            "-fx-background-radius: 9;" +
            "-fx-padding: 8 10 8 10;" +
            "-fx-font-size: 13px;" +
            "-fx-opacity: 1.0;"
        );

        box.getChildren().addAll(lbl, ta);
        return box;
    }

    private void handleBack() {
        if (onBack != null) {
            onBack.run();
            return;
        }
        if (isCompanyViewer && offeringID > 0) {
            ViewCandidatesScreen screen = new ViewCandidatesScreen(offeringID);
            screen.start(primaryStage);
        } else if (isCompanyViewer) {
            new CompanyDashboard().start(primaryStage);
        } else {
            new JobSeekerDashboard().start(primaryStage);
        }
    }

    private void applyBackButtonStyle(Button btn, boolean hovered) {
        if (isCompanyViewer) {
            if (hovered) {
                btn.setStyle(
                    "-fx-background-color: #3D1A1A;" +
                    "-fx-text-fill: #D4A0A0;" +
                    "-fx-font-size: 12px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-background-radius: 8;" +
                    "-fx-border-color: #4A2020;" +
                    "-fx-border-width: 1;" +
                    "-fx-border-radius: 8;" +
                    "-fx-cursor: hand;"
                );
            } else {
                btn.setStyle(
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
            }
        } else {
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
    }

    private void applySeekerPrimaryStyle(Button btn, boolean hovered) {
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

    private void applyCompanyPrimaryStyle(Button btn, boolean hovered) {
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

    public static void main(String[] args) {
        launch(args);
    }
}
