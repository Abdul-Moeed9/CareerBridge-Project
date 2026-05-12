package ui;

import controller.AuthController;
import domain.User;
import service.AIMatchingService;
import utility.SessionManager;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LoginScreen extends Application {

    private TextField emailField;
    private PasswordField passwordField;
    private Button loginButton;
    private Hyperlink registerLink;
    private Label errorLabel;
    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        initialize();
    }

    public void initialize() {
        primaryStage.setTitle("CareerBridge");
        primaryStage.setResizable(true);
        Scene scene = new Scene(buildRoot(), 980, 640);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint("");
        primaryStage.setFullScreenExitKeyCombination(javafx.scene.input.KeyCombination.NO_MATCH);
        primaryStage.show();
    }

    private Parent buildRoot() {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #111111;");

        VBox mainContainer = new VBox(30);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(40));

        VBox topSection = new VBox(10);
        topSection.setAlignment(Pos.CENTER);

        Label rocketLogo = new Label("\uD83D\uDE80");
        rocketLogo.setStyle("-fx-font-size: 48px; -fx-text-fill: #FFD700;");

        Label brandName = new Label("CareerBridge");
        brandName.setFont(Font.font("System", FontWeight.BOLD, 28));
        brandName.setStyle("-fx-text-fill: #00d4ff;");

        Label tagline = new Label("Connect. Apply. Grow.");
        tagline.setStyle("-fx-text-fill: #555555; -fx-font-size: 13px;");

        topSection.getChildren().addAll(rocketLogo, brandName, tagline);

        VBox card = new VBox(18);
        card.setMaxWidth(400);
        card.setPadding(new Insets(35, 40, 35, 40));
        card.setStyle(
            "-fx-background-color: #1e1e1e;" +
            "-fx-background-radius: 14;" +
            "-fx-border-color: #2a2a2a;" +
            "-fx-border-radius: 14;" +
            "-fx-border-width: 1;"
        );

        Label heading = new Label("Welcome Back");
        heading.setFont(Font.font("System", FontWeight.BOLD, 22));
        heading.setStyle("-fx-text-fill: #e0e0e0;");

        Label sub = new Label("Sign in to your account");
        sub.setStyle("-fx-text-fill: #555555; -fx-font-size: 13px;");

        VBox gap = new VBox();
        gap.setMinHeight(5);

        VBox emailBox = buildInputField("Email Address", "Enter your email", false);
        emailField = (TextField) emailBox.getChildren().get(1);

        VBox passBox = buildInputField("Password", "Enter your password", true);
        passwordField = (PasswordField) passBox.getChildren().get(1);

        errorLabel = new Label();
        errorLabel.setMaxWidth(Double.MAX_VALUE);
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        loginButton = new Button("Sign In  \u2192");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setPrefHeight(47);
        applyButtonStyle(loginButton, false);
        loginButton.setOnMouseEntered(e -> applyButtonStyle(loginButton, true));
        loginButton.setOnMouseExited(e -> applyButtonStyle(loginButton, false));
        loginButton.setOnAction(e -> handleLogin());

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #252525;");

        HBox registerRow = new HBox(7);
        registerRow.setAlignment(Pos.CENTER);

        Label noAcc = new Label("Don't have an account?");
        noAcc.setStyle("-fx-text-fill: #444444; -fx-font-size: 12px;");

        registerLink = new Hyperlink("Create one");
        registerLink.setStyle(
            "-fx-text-fill: #00d4ff;" +
            "-fx-font-size: 12px;" +
            "-fx-border-color: transparent;" +
            "-fx-padding: 0;"
        );
        registerLink.setOnMouseEntered(e -> registerLink.setStyle(
            "-fx-text-fill: #66e5ff; -fx-font-size: 12px;" +
            "-fx-border-color: transparent; -fx-padding: 0;"
        ));
        registerLink.setOnMouseExited(e -> registerLink.setStyle(
            "-fx-text-fill: #00d4ff; -fx-font-size: 12px;" +
            "-fx-border-color: transparent; -fx-padding: 0;"
        ));
        registerLink.setOnAction(e -> navigateToRegister());
        registerRow.getChildren().addAll(noAcc, registerLink);

        card.getChildren().addAll(
            heading, sub, gap,
            emailBox, passBox,
            errorLabel, loginButton,
            sep, registerRow
        );

        mainContainer.getChildren().addAll(topSection, card);
        root.getChildren().add(mainContainer);
        return root;
    }

    public void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showError("Please fill in all fields.");
            return;
        }

        User user = AuthController.getInstance().login(email, password);

        if (user == null) {
            showError("Invalid email or password, or account deactivated.");
            return;
        }

        if (!AIMatchingService.isModelTrained()) {
            showSuccess("Welcome, " + user.getName() + "! Preparing AI model...");
            Thread modelThread = new Thread(() -> {
                try {
                    AIMatchingService tempService = new AIMatchingService();
                    tempService.trainFromDatabase();
                } catch (Exception e) {
                    System.err.println("Background model training failed: " + e.getMessage());
                }
                javafx.application.Platform.runLater(() -> {
                    showSuccess("AI model ready! Redirecting...");
                    PauseTransition p = new PauseTransition(Duration.seconds(1.0));
                    p.setOnFinished(ev -> navigateByRole(user.getRole()));
                    p.play();
                });
            });
            modelThread.setDaemon(true);
            modelThread.start();
        } else {
            showSuccess("Welcome, " + user.getName() + "! Redirecting...");
            PauseTransition pause = new PauseTransition(Duration.seconds(1.2));
            pause.setOnFinished(ev -> navigateByRole(user.getRole()));
            pause.play();
        }
    }

    private void navigateByRole(String role) {
        switch (role) {
            case "job_seeker" -> {
                User currentUser = SessionManager.getInstance().getCurrentUser();
                if (currentUser instanceof domain.JobSeeker) {
                    int seekerID = ((domain.JobSeeker) currentUser).getSeekerID();
                    dao.ProfileDAO profileDAO = new dao.ProfileDAO();
                    domain.Profile profile = profileDAO.findBySeekerID(seekerID);
                    if (profile == null || !isProfileComplete(profile)) {
                        new ProfileSetupScreen().start(primaryStage);
                    } else {
                        new JobSeekerDashboard().start(primaryStage);
                    }
                } else {
                    new ProfileSetupScreen().start(primaryStage);
                }
            }
            case "company" -> new CompanyDashboard().start(primaryStage);
            case "admin" -> new AdminDashboard().start(primaryStage);
        }
    }

    private boolean isProfileComplete(domain.Profile profile) {
        return profile.getEducation() != null && !profile.getEducation().isEmpty()
            && profile.getSkills() != null && !profile.getSkills().isEmpty()
            && profile.getLocation() != null && !profile.getLocation().isEmpty()
            && profile.getCGPA() > 0;
    }

    public void navigateToRegister() {
        new RegisterScreen().start(primaryStage);
    }

    private void showError(String message) {
        errorLabel.setStyle(
            "-fx-text-fill: #ff4757;" +
            "-fx-font-size: 12px;" +
            "-fx-background-color: #1a0408;" +
            "-fx-background-radius: 7;" +
            "-fx-padding: 9 13 9 13;" +
            "-fx-border-color: #4a0010;" +
            "-fx-border-radius: 7;" +
            "-fx-border-width: 1;"
        );
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void showSuccess(String message) {
        errorLabel.setStyle(
            "-fx-text-fill: #00ff88;" +
            "-fx-font-size: 12px;" +
            "-fx-background-color: #001a0d;" +
            "-fx-background-radius: 7;" +
            "-fx-padding: 9 13 9 13;" +
            "-fx-border-color: #004422;" +
            "-fx-border-radius: 7;" +
            "-fx-border-width: 1;"
        );
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private VBox buildInputField(String labelText, String prompt, boolean isPassword) {
        VBox box = new VBox(7);

        Label lbl = new Label(labelText);
        lbl.setStyle("-fx-text-fill: #707070; -fx-font-size: 12px; -fx-font-weight: bold;");

        String baseStyle =
            "-fx-background-color: #151515;" +
            "-fx-text-fill: #e0e0e0;" +
            "-fx-prompt-text-fill: #383838;" +
            "-fx-border-color: #2e2e2e;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 9;" +
            "-fx-background-radius: 9;" +
            "-fx-padding: 11 14 11 14;" +
            "-fx-font-size: 13px;";

        String focusedStyle =
            "-fx-background-color: #181818;" +
            "-fx-text-fill: #e0e0e0;" +
            "-fx-prompt-text-fill: #383838;" +
            "-fx-border-color: #00d4ff;" +
            "-fx-border-width: 1.5;" +
            "-fx-border-radius: 9;" +
            "-fx-background-radius: 9;" +
            "-fx-padding: 11 14 11 14;" +
            "-fx-font-size: 13px;";

        if (isPassword) {
            PasswordField pf = new PasswordField();
            pf.setPromptText(prompt);
            pf.setStyle(baseStyle);
            pf.focusedProperty().addListener((obs, ov, nv) -> pf.setStyle(nv ? focusedStyle : baseStyle));
            pf.setPrefHeight(42);
            box.getChildren().addAll(lbl, pf);
        } else {
            TextField tf = new TextField();
            tf.setPromptText(prompt);
            tf.setStyle(baseStyle);
            tf.focusedProperty().addListener((obs, ov, nv) -> tf.setStyle(nv ? focusedStyle : baseStyle));
            tf.setPrefHeight(42);
            box.getChildren().addAll(lbl, tf);
        }

        return box;
    }

    private void applyButtonStyle(Button btn, boolean hovered) {
        if (hovered) {
            btn.setStyle(
                "-fx-background-color: #00b8d9;" +
                "-fx-text-fill: #ffffff;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 9;" +
                "-fx-cursor: hand;"
            );
        } else {
            btn.setStyle(
                "-fx-background-color: #00d4ff;" +
                "-fx-text-fill: #111111;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 9;" +
                "-fx-cursor: hand;"
            );
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}