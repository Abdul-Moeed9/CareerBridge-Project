import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.*;

public class AKDLoginScreen extends Application {

    private TextField UserIdField;
    private TextField[] PasswordBoxes = new TextField[10];
    private Label ChallengeLabel;
    private Label ErrorLabel;
    private List<Integer> RequiredPositions = new ArrayList<>();

    @Override
    public void start(Stage PrimaryStage) {
        PrimaryStage.setTitle("Trade Cast v1.4.0");
        PrimaryStage.setResizable(false);

        BorderPane Root = new BorderPane();
        Root.setStyle("-fx-background-color: #FFFFFF;");
        Root.setTop(BuildTitleBar());
        Root.setCenter(BuildCenterPane(PrimaryStage));
        Root.setBottom(BuildFooter());

        Scene MainScene = new Scene(Root, 580, 450);
        PrimaryStage.setScene(MainScene);
        PrimaryStage.show();
    }

    private VBox BuildTitleBar() {
        VBox TopBar = new VBox();
        TopBar.setStyle("-fx-background-color: #3366CC; -fx-padding: 6;");
        Label TitleLabel = new Label("  Trade Cast v1.4.0");
        TitleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13; -fx-font-weight: bold;");
        TopBar.getChildren().add(TitleLabel);
        return TopBar;
    }

    private GridPane BuildCenterPane(Stage PrimaryStage) {
        GridPane CenterPane = new GridPane();
        CenterPane.setHgap(15);
        CenterPane.setVgap(12);
        CenterPane.setPadding(new Insets(25, 30, 10, 30));

        CenterPane.add(BuildBrandLabel(), 0, 0, 2, 1);
        CenterPane.add(BuildAkdBrand(), 2, 0, 2, 2);
        AttachUserIdRow(CenterPane, PrimaryStage);
        AttachPasswordRow(CenterPane);

        GenerateChallenge();

        ChallengeLabel = new Label(BuildChallengeText());
        ChallengeLabel.setFont(Font.font("Arial", 11));
        ChallengeLabel.setTextFill(Color.web("#333333"));
        CenterPane.add(ChallengeLabel, 1, 4, 3, 1);

        ErrorLabel = new Label("Invalid login credentials!");
        ErrorLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        ErrorLabel.setTextFill(Color.RED);
        ErrorLabel.setVisible(false);
        CenterPane.add(ErrorLabel, 1, 5, 2, 1);

        CenterPane.add(BuildLoginButtons(PrimaryStage), 0, 6, 4, 1);
        CenterPane.add(BuildExampleSection(), 0, 7, 4, 1);

        return CenterPane;
    }

    private Text BuildBrandLabel() {
        Text BrandName = new Text("TRADE CAST");
        BrandName.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        BrandName.setFill(Color.web("#3366CC"));
        return BrandName;
    }

    private VBox BuildAkdBrand() {
        VBox AkdBrand = new VBox(2);
        AkdBrand.setAlignment(Pos.CENTER_RIGHT);


        return AkdBrand;
    }

    private void AttachUserIdRow(GridPane CenterPane, Stage PrimaryStage) {
        Label UserIdLabel = new Label("User ID");
        UserIdLabel.setFont(Font.font("Arial", 13));
        CenterPane.add(UserIdLabel, 0, 2);

        UserIdField = new TextField();
        UserIdField.setPrefWidth(160);
        CenterPane.add(UserIdField, 1, 2);

        Button ForgotPasswordBtn = new Button("Forgot Password?");
        ForgotPasswordBtn.setStyle("-fx-font-size: 11;");
        ForgotPasswordBtn.setOnAction(E -> ShowForgotPasswordDialog(PrimaryStage));
        CenterPane.add(ForgotPasswordBtn, 2, 2);
    }

    private void AttachPasswordRow(GridPane CenterPane) {
        Label PasswordLabel = new Label("Password");
        PasswordLabel.setFont(Font.font("Arial", 13));
        CenterPane.add(PasswordLabel, 0, 3);

        HBox PasswordRow = new HBox(4);
        PasswordRow.setAlignment(Pos.CENTER_LEFT);

        for (int I = 0; I < 10; I++) {
            PasswordBoxes[I] = CreatePasswordBox(I);
            PasswordRow.getChildren().add(PasswordBoxes[I]);
        }

        CenterPane.add(PasswordRow, 1, 3, 3, 1);
    }

    private TextField CreatePasswordBox(int Index) {
        TextField Box = new TextField();
        Box.setPrefWidth(30);
        Box.setPrefHeight(30);
        Box.setMaxWidth(30);
        Box.setAlignment(Pos.CENTER);
        Box.setStyle(
            "-fx-font-size: 13; -fx-font-weight: bold; " +
            "-fx-background-color: #E8E8E8; -fx-border-color: #999999; -fx-border-width: 1;"
        );
        Box.setEditable(false);

        Box.textProperty().addListener((Obs, OldVal, NewVal) -> {
            if (NewVal.length() > 1) {
                PasswordBoxes[Index].setText(NewVal.substring(0, 1));
            }
            if (NewVal.length() == 1) {
                FocusNextBox(Index);
            }
        });

        return Box;
    }

    private HBox BuildLoginButtons(Stage PrimaryStage) {
        HBox ButtonRow = new HBox(12);
        ButtonRow.setAlignment(Pos.CENTER);
        ButtonRow.setPadding(new Insets(5, 0, 0, 0));

        Button LoginBtn = new Button("Login");
        LoginBtn.setPrefWidth(80);
        LoginBtn.setStyle("-fx-font-size: 12;");
        LoginBtn.setOnAction(E -> HandleLogin(PrimaryStage));

        Button CancelBtn = new Button("Cancel");
        CancelBtn.setPrefWidth(80);
        CancelBtn.setStyle("-fx-font-size: 12;");
        CancelBtn.setOnAction(E -> PrimaryStage.close());

        ButtonRow.getChildren().addAll(LoginBtn, CancelBtn);
        return ButtonRow;
    }

    private HBox BuildFooter() {
        HBox Footer = new HBox();
        Footer.setStyle("-fx-background-color: #E8E8E8; -fx-padding: 5 10 5 10;");
        Footer.setAlignment(Pos.CENTER_RIGHT);
        Label Copyright = new Label("Copyright 2010 - 2011 CATALYST IT Solutions (Pvt.) Limited. All Rights Reserved.");
        Copyright.setStyle("-fx-font-size: 10; -fx-text-fill: #666666;");
        Footer.getChildren().add(Copyright);
        return Footer;
    }

    private void GenerateChallenge() {
        RequiredPositions.clear();
        Random Rng = new Random();
        Set<Integer> Chosen = new LinkedHashSet<>();
        while (Chosen.size() < 4) {
            Chosen.add(Rng.nextInt(10));
        }
        RequiredPositions.addAll(Chosen);
        Collections.sort(RequiredPositions);

        for (int I = 0; I < 10; I++) {
            boolean IsRequired = RequiredPositions.contains(I);
            PasswordBoxes[I].setEditable(IsRequired);
            String BgColor = IsRequired ? "#FFFFFF" : "#E8E8E8";
            PasswordBoxes[I].setStyle(
                "-fx-font-size: 13; -fx-font-weight: bold; " +
                "-fx-background-color: " + BgColor + "; -fx-border-color: #999999; -fx-border-width: 1;"
            );
            PasswordBoxes[I].clear();
        }

        if (!RequiredPositions.isEmpty()) {
            PasswordBoxes[RequiredPositions.get(0)].requestFocus();
        }
    }

    private String BuildChallengeText() {
        StringBuilder Sb = new StringBuilder("Please enter ");
        for (int I = 0; I < RequiredPositions.size(); I++) {
            int Pos = RequiredPositions.get(I) + 1;
            Sb.append(GetOrdinal(Pos));
            if (I < RequiredPositions.size() - 2) {
                Sb.append(", ");
            } else if (I == RequiredPositions.size() - 2) {
                Sb.append(" & ");
            }
        }
        Sb.append(" character of your password.");
        return Sb.toString();
    }

    private String GetOrdinal(int N) {
        String[] Suffixes = {"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"};
        if (N % 100 >= 11 && N % 100 <= 13) {
            return N + "th";
        }
        return N + Suffixes[N % 10];
    }

    private void FocusNextBox(int CurrentIndex) {
        for (int I = 0; I < RequiredPositions.size() - 1; I++) {
            if (RequiredPositions.get(I) == CurrentIndex) {
                PasswordBoxes[RequiredPositions.get(I + 1)].requestFocus();
                return;
            }
        }
    }

    private void HandleLogin(Stage PrimaryStage) {
        String ValidUserId = "demo";
        String ValidPassword = "1234";
        String UserId = UserIdField.getText().trim();

        StringBuilder EnteredPassword = new StringBuilder();
        for (int Pos : RequiredPositions) {
            EnteredPassword.append(PasswordBoxes[Pos].getText());
        }

        if (UserId.isEmpty()) {
            ErrorLabel.setText("User ID cannot be empty!");
            ErrorLabel.setVisible(true);
            return;
        }

        boolean AllFilled = CheckAllBoxesFilled();
        if (!AllFilled) {
            ErrorLabel.setText("Please fill all highlighted boxes!");
            ErrorLabel.setVisible(true);
            return;
        }

        if (UserId.equals(ValidUserId) && EnteredPassword.toString().equals(ValidPassword)) {
            ErrorLabel.setVisible(false);
            AKDMainDashboard Dashboard = new AKDMainDashboard();
            Stage DashboardStage = new Stage();
            Dashboard.start(DashboardStage);
            PrimaryStage.close();
        } else {
            ErrorLabel.setText("Invalid login credentials!");
            ErrorLabel.setVisible(true);
        }
    }

    private boolean CheckAllBoxesFilled() {
        for (int Pos : RequiredPositions) {
            if (PasswordBoxes[Pos].getText().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private VBox BuildExampleSection() {
        VBox Box = new VBox(4);
        Box.setPadding(new Insets(10, 0, 0, 0));
        Box.setStyle("-fx-border-color: #CCCCCC; -fx-border-width: 1 0 0 0; -fx-padding: 10 0 0 0;");

        Label ExampleTitle = new Label("Example: If your Password is q a Z s 1 2 q G, you would be required to enter");
        ExampleTitle.setFont(Font.font("Arial", 11));

        Label ExampleSub = new Label("the missing characters in white boxes.");
        ExampleSub.setFont(Font.font("Arial", 11));

        HBox ExampleBoxes = BuildExampleBoxes();
        HBox LetterRow = BuildExampleLetterRow();

        Box.getChildren().addAll(ExampleTitle, ExampleSub, ExampleBoxes, LetterRow);
        return Box;
    }

    private HBox BuildExampleBoxes() {
        HBox ExampleBoxes = new HBox(3);
        ExampleBoxes.setPadding(new Insets(5, 0, 0, 0));
        String[] Chars = {"q", "a", "Z", "s", "1", "2", "q", "G"};
        int[] WhiteIndices = {1, 3, 5, 6};
        Set<Integer> WhiteSet = new HashSet<>();
        for (int W : WhiteIndices) {
            WhiteSet.add(W);
        }

        for (int I = 0; I < 8; I++) {
            TextField Eb = new TextField(WhiteSet.contains(I) ? Chars[I] : "");
            Eb.setPrefWidth(28);
            Eb.setPrefHeight(28);
            Eb.setMaxWidth(28);
            Eb.setAlignment(Pos.CENTER);
            Eb.setEditable(false);
            String BgColor = WhiteSet.contains(I) ? "#FFFFFF" : "#E8E8E8";
            Eb.setStyle(
                "-fx-font-size: 12; -fx-font-weight: bold; " +
                "-fx-background-color: " + BgColor + "; -fx-border-color: #999999; -fx-border-width: 1;"
            );
            ExampleBoxes.getChildren().add(Eb);
        }
        return ExampleBoxes;
    }

    private HBox BuildExampleLetterRow() {
        String[] Chars = {"q", "a", "Z", "s", "1", "2", "q", "G"};
        HBox LetterRow = new HBox(3);
        LetterRow.setPadding(new Insets(2, 0, 0, 0));
        for (String C : Chars) {
            Label Lbl = new Label(C);
            Lbl.setPrefWidth(28);
            Lbl.setAlignment(Pos.CENTER);
            Lbl.setFont(Font.font("Arial", FontWeight.BOLD, 11));
            LetterRow.getChildren().add(Lbl);
        }
        return LetterRow;
    }

    private void ShowForgotPasswordDialog(Stage Owner) {
        Stage DialogStage = new Stage();
        DialogStage.setTitle("Forgot Password");
        DialogStage.initModality(Modality.WINDOW_MODAL);
        DialogStage.initOwner(Owner);
        DialogStage.setResizable(false);

        VBox DialogRoot = new VBox(12);
        DialogRoot.setPadding(new Insets(0));
        DialogRoot.setStyle("-fx-background-color: #FFFFFF;");

        VBox DialogTop = BuildDialogTitleBar("Forgot Password");
        VBox DialogBody = BuildForgotPasswordBody(DialogStage);

        HBox DlgFooter = new HBox();
        DlgFooter.setStyle("-fx-background-color: #E8E8E8; -fx-padding: 5 10 5 10;");
        DlgFooter.setAlignment(Pos.CENTER_RIGHT);
        Label DlgCopy = new Label("Copyright 2010 - 2011 CATALYST IT Solutions (Pvt.) Limited. All Rights Reserved.");
        DlgCopy.setStyle("-fx-font-size: 10; -fx-text-fill: #666666;");
        DlgFooter.getChildren().add(DlgCopy);

        DialogRoot.getChildren().addAll(DialogTop, DialogBody, new Region(), DlgFooter);
        VBox.setVgrow(DialogBody, Priority.ALWAYS);

        Scene DlgScene = new Scene(DialogRoot, 420, 300);
        DialogStage.setScene(DlgScene);
        DialogStage.showAndWait();
    }

    private VBox BuildDialogTitleBar(String Title) {
        VBox DialogTop = new VBox();
        DialogTop.setStyle("-fx-background-color: #3366CC; -fx-padding: 6;");
        Label DialogTitle = new Label("  " + Title);
        DialogTitle.setStyle("-fx-text-fill: white; -fx-font-size: 13; -fx-font-weight: bold;");
        DialogTop.getChildren().add(DialogTitle);
        return DialogTop;
    }

    private VBox BuildForgotPasswordBody(Stage DialogStage) {
        VBox DialogBody = new VBox(12);
        DialogBody.setPadding(new Insets(20, 30, 10, 30));

        HBox BrandRow = new HBox(20);
        BrandRow.setAlignment(Pos.CENTER);
        Text DlgBrand = new Text("TRADE CAST");
        DlgBrand.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        DlgBrand.setFill(Color.web("#3366CC"));

        GridPane FormGrid = new GridPane();
        FormGrid.setHgap(10);
        FormGrid.setVgap(10);

        Label DlgUserLabel = new Label("User ID");
        DlgUserLabel.setFont(Font.font("Arial", 13));
        TextField DlgUserField = new TextField();
        DlgUserField.setPrefWidth(220);

        Label DlgEmailLabel = new Label("Email Address");
        DlgEmailLabel.setFont(Font.font("Arial", 13));
        TextField DlgEmailField = new TextField();
        DlgEmailField.setPrefWidth(220);

        FormGrid.add(DlgUserLabel, 0, 0);
        FormGrid.add(DlgUserField, 1, 0);
        FormGrid.add(DlgEmailLabel, 0, 1);
        FormGrid.add(DlgEmailField, 1, 1);

        HBox DlgButtons = BuildForgotPasswordButtons(DialogStage, DlgUserField, DlgEmailField);

        DialogBody.getChildren().addAll(BrandRow, FormGrid, DlgButtons);
        return DialogBody;
    }

    private HBox BuildForgotPasswordButtons(Stage DialogStage, TextField DlgUserField, TextField DlgEmailField) {
        HBox DlgButtons = new HBox(12);
        DlgButtons.setAlignment(Pos.CENTER);
        DlgButtons.setPadding(new Insets(10, 0, 0, 0));

        Button OkBtn = new Button("OK");
        OkBtn.setPrefWidth(70);
        OkBtn.setOnAction(E -> {
            String Uid = DlgUserField.getText().trim();
            String Email = DlgEmailField.getText().trim();
            if (!Uid.isEmpty() && !Email.isEmpty()) {
                Alert Confirmation = new Alert(Alert.AlertType.INFORMATION);
                Confirmation.setTitle("Password Reset");
                Confirmation.setHeaderText(null);
                Confirmation.setContentText("A new password has been sent to " + Email);
                Confirmation.showAndWait();
                DialogStage.close();
            }
        });

        Button DlgCancelBtn = new Button("Cancel");
        DlgCancelBtn.setPrefWidth(70);
        DlgCancelBtn.setOnAction(E -> DialogStage.close());

        DlgButtons.getChildren().addAll(OkBtn, DlgCancelBtn);
        return DlgButtons;
    }

    public static void main(String[] Args) {
        launch(Args);
    }
}