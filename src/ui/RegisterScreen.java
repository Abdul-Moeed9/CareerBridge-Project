package ui;

import controller.AuthController;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
import javafx.stage.Stage;
import javafx.util.Duration;

public class RegisterScreen extends Application {

    private TextField nameField;
    private TextField emailField;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;
    private ComboBox<String> roleSelector;
    private TextField companyNameField;
    private Button registerButton;
    private Label errorLabel;
    private Stage primaryStage;
    private VBox companyNameBox;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        initialize();
    }

    public void initialize() {
        primaryStage.setTitle("CareerBridge - Register");
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

        VBox mainContainer = new VBox(25);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(30));

        VBox topSection = new VBox(8);
        topSection.setAlignment(Pos.CENTER);

        Label rocketLogo = new Label("\uD83D\uDE80");
        rocketLogo.setStyle("-fx-font-size: 42px; -fx-text-fill: #FFD700;");

        Label brandName = new Label("CareerBridge");
        brandName.setFont(Font.font("System", FontWeight.BOLD, 26));
        brandName.setStyle("-fx-text-fill: #00d4ff;");

        topSection.getChildren().addAll(rocketLogo, brandName);

        VBox card = new VBox(15);
        card.setMaxWidth(420);
        card.setPadding(new Insets(30, 38, 30, 38));
        card.setStyle(
            "-fx-background-color: #1e1e1e;" +
            "-fx-background-radius: 14;" +
            "-fx-border-color: #2a2a2a;" +
            "-fx-border-radius: 14;" +
            "-fx-border-width: 1;"
        );

        Label heading = new Label("Create Account");
        heading.setFont(Font.font("System", FontWeight.BOLD, 22));
        heading.setStyle("-fx-text-fill: #e0e0e0;");

        Label sub = new Label("Join CareerBridge today");
        sub.setStyle("-fx-text-fill: #555555; -fx-font-size: 13px;");

        VBox gap = new VBox();
        gap.setMinHeight(3);

        VBox nameBox = buildInputField("Full Name", "Enter your full name", false);
        nameField = (TextField) nameBox.getChildren().get(1);

        VBox emailBox = buildInputField("Email Address", "Enter your email", false);
        emailField = (TextField) emailBox.getChildren().get(1);

        VBox passBox = buildInputField("Password", "Create a password", true);
        passwordField = (PasswordField) passBox.getChildren().get(1);

        VBox confirmPassBox = buildInputField("Confirm Password", "Confirm your password", true);
        confirmPasswordField = (PasswordField) confirmPassBox.getChildren().get(1);

        VBox roleBox = new VBox(7);
        Label roleLbl = new Label("I am a...");
        roleLbl.setStyle("-fx-text-fill: #707070; -fx-font-size: 12px; -fx-font-weight: bold;");
        roleSelector = new ComboBox<>();
        roleSelector.getItems().addAll("Job Seeker", "Company");
        roleSelector.setPromptText("Select your role");
        roleSelector.setMaxWidth(Double.MAX_VALUE);
        roleSelector.setPrefHeight(42);
        applyComboStyle(roleSelector);
        roleSelector.setOnAction(e -> toggleCompanyFields());
        roleBox.getChildren().addAll(roleLbl, roleSelector);

        companyNameBox = buildInputField("Company Name", "Enter your company name", false);
        companyNameField = (TextField) companyNameBox.getChildren().get(1);
        companyNameBox.setVisible(false);
        companyNameBox.setManaged(false);

        errorLabel = new Label();
        errorLabel.setMaxWidth(Double.MAX_VALUE);
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        registerButton = new Button("Create Account  \u2192");
        registerButton.setMaxWidth(Double.MAX_VALUE);
        registerButton.setPrefHeight(47);
        applyButtonStyle(registerButton, false);
        registerButton.setOnMouseEntered(e -> applyButtonStyle(registerButton, true));
        registerButton.setOnMouseExited(e -> applyButtonStyle(registerButton, false));
        registerButton.setOnAction(e -> handleRegister());

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #252525;");

        HBox loginRow = new HBox(7);
        loginRow.setAlignment(Pos.CENTER);

        Label hasAcc = new Label("Already have an account?");
        hasAcc.setStyle("-fx-text-fill: #444444; -fx-font-size: 12px;");

        Hyperlink loginLink = new Hyperlink("Sign in");
        loginLink.setStyle(
            "-fx-text-fill: #00d4ff;" +
            "-fx-font-size: 12px;" +
            "-fx-border-color: transparent;" +
            "-fx-padding: 0;"
        );
        loginLink.setOnMouseEntered(e -> loginLink.setStyle(
            "-fx-text-fill: #66e5ff; -fx-font-size: 12px;" +
            "-fx-border-color: transparent; -fx-padding: 0;"
        ));
        loginLink.setOnMouseExited(e -> loginLink.setStyle(
            "-fx-text-fill: #00d4ff; -fx-font-size: 12px;" +
            "-fx-border-color: transparent; -fx-padding: 0;"
        ));
        loginLink.setOnAction(e -> navigateToLogin());
        loginRow.getChildren().addAll(hasAcc, loginLink);

        card.getChildren().addAll(
            heading, sub, gap,
            nameBox, emailBox, passBox, confirmPassBox,
            roleBox, companyNameBox,
            errorLabel, registerButton,
            sep, loginRow
        );

        mainContainer.getChildren().addAll(topSection, card);
        root.getChildren().add(mainContainer);
        return root;
    }

    public void handleRegister() {
        if (!validateInputs()) return;

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String selectedRole = roleSelector.getValue();

        String role = selectedRole.equals("Job Seeker") ? "job_seeker" : "company";

        if (role.equals("company")) {
            String companyName = companyNameField.getText().trim();
            if (!companyName.isEmpty()) {
                name = companyName;
            }
        }

        boolean success = AuthController.getInstance().register(name, email, password, role);

        if (success) {
            showSuccess("Account created successfully! Redirecting to login...");
            PauseTransition pause = new PauseTransition(Duration.seconds(1.4));
            pause.setOnFinished(ev -> navigateToLogin());
            pause.play();
        } else {
            showError("Registration failed. Email may already be in use.");
        }
    }

    private void toggleCompanyFields() {
        boolean isCompany = roleSelector.getValue() != null && roleSelector.getValue().equals("Company");
        companyNameBox.setVisible(isCompany);
        companyNameBox.setManaged(isCompany);
    }

    private boolean validateInputs() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPass = confirmPasswordField.getText().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPass.isEmpty()) {
            showError("Please fill in all fields.");
            return false;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            showError("Please enter a valid email address.");
            return false;
        }

        if (password.length() < 6) {
            showError("Password must be at least 6 characters.");
            return false;
        }

        if (!password.equals(confirmPass)) {
            showError("Passwords do not match.");
            return false;
        }

        if (roleSelector.getValue() == null) {
            showError("Please select a role.");
            return false;
        }

        if (roleSelector.getValue().equals("Company")) {
            String companyName = companyNameField.getText().trim();
            if (companyName.isEmpty()) {
                showError("Please enter your company name.");
                return false;
            }
        }

        return true;
    }

    public void navigateToLogin() {
        new LoginScreen().start(primaryStage);
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

    private void applyComboStyle(ComboBox<String> combo) {
        combo.setStyle(
            "-fx-background-color: #151515;" +
            "-fx-border-color: #2e2e2e;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 9;" +
            "-fx-background-radius: 9;" +
            "-fx-font-size: 13px;" +
            "-fx-text-fill: #e0e0e0;" +
            "-fx-prompt-text-fill: #383838;"
        );
    }

    public static void main(String[] args) {
        launch(args);
    }
}