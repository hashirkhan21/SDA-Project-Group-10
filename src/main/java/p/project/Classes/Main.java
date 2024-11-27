package p.project.Classes;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import p.project.Controllers.logINController;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Correct the path to the FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/p/project/logIn-view.fxml"));
        Parent root = loader.load();
        logINController controller = loader.getController();

        primaryStage.setTitle("Log In");
        primaryStage.setScene(new Scene(root, 400, 300));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}