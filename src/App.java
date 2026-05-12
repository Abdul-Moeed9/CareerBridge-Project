import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // This loads the FXML layout designed in Scene Builder
            Parent root = FXMLLoader.load(getClass().getResource("MainScene.fxml"));
            
            // Set the scene using the root from FXML
            Scene scene = new Scene(root);
            
            primaryStage.setTitle("CareerBridge - Development Mode");
            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
            primaryStage.setFullScreen(true);
            primaryStage.setFullScreenExitHint("");
            primaryStage.setFullScreenExitKeyCombination(javafx.scene.input.KeyCombination.NO_MATCH);
            primaryStage.show();
            
        } catch (Exception e) {
            // This will tell you if the FXML file is missing or has a bug
            System.out.println("Error: Could not load FXML file.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}