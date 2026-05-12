package ui;

import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import domain.FilterCandidate;

public class FilterPanel extends VBox {

    private TextField locationField;
    private TextField roleField;
    private TextField minCGPAField;
    private TextField minStipendField;
    private ComboBox<String> experienceSelector;
    private Button applyFilterButton;
    private Button resetButton;

    private Runnable onResetCallback;
    private Runnable onApplyCallback;

    public FilterPanel() {
        initialize();
    }

    public void initialize() {
        setSpacing(14);
        setPadding(new Insets(12, 16, 12, 16));
        setStyle("-fx-background-color: #0F2639;");

        VBox locationBox = buildFilterField("Location", "e.g. Lahore, Karachi");
        locationField = (TextField) locationBox.getChildren().get(1);

        VBox roleBox = buildFilterField("Role", "e.g. Backend Developer");
        roleField = (TextField) roleBox.getChildren().get(1);

        VBox cgpaBox = buildFilterField("Min CGPA", "e.g. 3.0");
        minCGPAField = (TextField) cgpaBox.getChildren().get(1);

        VBox stipendBox = buildFilterField("Min Stipend", "e.g. 20000");
        minStipendField = (TextField) stipendBox.getChildren().get(1);

        VBox expBox = new VBox(6);
        Label expLabel = new Label("Experience");
        expLabel.setStyle(
            "-fx-text-fill: #8EAFC4;" +
            "-fx-font-size: 10px;" +
            "-fx-font-weight: bold;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );

        experienceSelector = new ComboBox<>();
        experienceSelector.getItems().addAll("Any", "Fresh / 0 years", "1-2 years", "3-5 years", "5+ years");
        experienceSelector.setPromptText("Select experience");
        experienceSelector.setMaxWidth(Double.MAX_VALUE);
        experienceSelector.setPrefHeight(36);
        applyComboBaseStyle();
        experienceSelector.focusedProperty().addListener((obs, ov, nv) -> {
            if (nv) {
                experienceSelector.setStyle(
                    "-fx-background-color: #0D1F33;" +
                    "-fx-border-color: #1E90FF;" +
                    "-fx-border-width: 1.5;" +
                    "-fx-border-radius: 7;" +
                    "-fx-background-radius: 7;" +
                    "-fx-font-size: 11px;"
                );
            } else {
                applyComboBaseStyle();
            }
        });
        expBox.getChildren().addAll(expLabel, experienceSelector);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        resetButton = new Button("Reset Filters");
        resetButton.setMaxWidth(Double.MAX_VALUE);
        resetButton.setPrefHeight(36);
        applyResetBaseStyle();
        resetButton.setOnMouseEntered(e -> resetButton.setStyle(
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
        resetButton.setOnMouseExited(e -> applyResetBaseStyle());
        resetButton.setOnAction(e -> handleReset());

        applyFilterButton = new Button("Apply Filters");
        applyFilterButton.setMaxWidth(Double.MAX_VALUE);
        applyFilterButton.setPrefHeight(36);
        applyFilterButton.setStyle(
            "-fx-background-color: #1E90FF;" +
            "-fx-text-fill: #FFFFFF;" +
            "-fx-font-size: 11px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        applyFilterButton.setOnAction(e -> handleApplyFilter());

        getChildren().addAll(locationBox, roleBox, cgpaBox, stipendBox, expBox, spacer, applyFilterButton, resetButton);
    }

    private VBox buildFilterField(String label, String prompt) {
        VBox box = new VBox(6);

        Label lbl = new Label(label);
        lbl.setStyle(
            "-fx-text-fill: #8EAFC4;" +
            "-fx-font-size: 10px;" +
            "-fx-font-weight: bold;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );

        String baseStyle =
            "-fx-background-color: #0A1929;" +
            "-fx-text-fill: #e0e0e0;" +
            "-fx-prompt-text-fill: #3A5068;" +
            "-fx-border-color: #1E3A5F;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 7;" +
            "-fx-background-radius: 7;" +
            "-fx-padding: 8 10 8 10;" +
            "-fx-font-size: 11px;";

        String focusedStyle =
            "-fx-background-color: #0D1F33;" +
            "-fx-text-fill: #e0e0e0;" +
            "-fx-prompt-text-fill: #3A5068;" +
            "-fx-border-color: #1E90FF;" +
            "-fx-border-width: 1.5;" +
            "-fx-border-radius: 7;" +
            "-fx-background-radius: 7;" +
            "-fx-padding: 8 10 8 10;" +
            "-fx-font-size: 11px;";

        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle(baseStyle);
        tf.focusedProperty().addListener((obs, ov, nv) -> tf.setStyle(nv ? focusedStyle : baseStyle));

        box.getChildren().addAll(lbl, tf);
        return box;
    }

    private void applyComboBaseStyle() {
        experienceSelector.setStyle(
            "-fx-background-color: #0A1929;" +
            "-fx-border-color: #1E3A5F;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 7;" +
            "-fx-background-radius: 7;" +
            "-fx-font-size: 11px;"
        );
    }

    private void applyResetBaseStyle() {
        resetButton.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #5A7A9A;" +
            "-fx-font-size: 11px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: #1E3A5F;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 8;" +
            "-fx-cursor: hand;"
        );
    }

    public void handleApplyFilter() {
        if (onApplyCallback != null) {
            onApplyCallback.run();
        }
    }

    public void setOnApplyCallback(Runnable callback) {
        this.onApplyCallback = callback;
    }

    public void handleReset() {
        locationField.clear();
        roleField.clear();
        minCGPAField.clear();
        minStipendField.clear();
        experienceSelector.getSelectionModel().clearSelection();
        experienceSelector.setPromptText("Select experience");
        experienceSelector.setValue(null);
        if (onResetCallback != null) {
            onResetCallback.run();
        }
    }

    public void setOnResetCallback(Runnable callback) {
        this.onResetCallback = callback;
    }

    public FilterCandidate buildFilterObject() {
        FilterCandidate filter = new FilterCandidate();

        String loc = locationField.getText().trim();
        if (!loc.isEmpty()) filter.setLocation(loc);

        String role = roleField.getText().trim();
        if (!role.isEmpty()) filter.setRole(role);

        String cgpaText = minCGPAField.getText().trim();
        if (!cgpaText.isEmpty()) {
            try {
                filter.setCGPA(Double.parseDouble(cgpaText));
            } catch (NumberFormatException ignored) {}
        }

        String stipendText = minStipendField.getText().trim();
        if (!stipendText.isEmpty()) {
            try {
                filter.setStipend(Double.parseDouble(stipendText));
            } catch (NumberFormatException ignored) {}
        }

        String exp = experienceSelector.getValue();
        if (exp != null && !"Any".equals(exp)) {
            filter.setExperience(exp);
        }

        return filter;
    }

    public TextField getLocationField() { return locationField; }
    public TextField getRoleField() { return roleField; }
    public TextField getMinCGPAField() { return minCGPAField; }
    public TextField getMinStipendField() { return minStipendField; }
    public ComboBox<String> getExperienceSelector() { return experienceSelector; }
    public Button getApplyFilterButton() { return applyFilterButton; }
    public Button getResetButton() { return resetButton; }
}