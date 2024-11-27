package p.project.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainMenuController {

    private MainController mainController;
    public void  initialize(){
        mainController = MainController.getInstance();
        mainController.userID = Zaviyah.getInstance().getUser().getID();
        mainController.accountName = Zaviyah.getInstance().getUser().getAccount().getName();
    }

    @FXML
    private void goToPlaceOrder(ActionEvent event) {
        try {
            // Correct path
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/p/project/order_menu.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/p/project/style.css").toExternalForm());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToManageAccount(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/p/project/manageAccount-view.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 600, 600);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Manage Account");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void signOut(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/p/project/logIn-view.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 500, 500); // Adjust size if necessary
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void goToGenerateOptimalList(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/p/project/GenerateOptimalList.fxml"));
            Parent root = loader.load();

            // Load the new scene
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/p/project/style.css").toExternalForm());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Generate Optimal List");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onAnalysisButtonClick(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/p/project/analysis_view.fxml"));
            Parent root = loader.load();

            // Pass the user ID to the controller
            AnalysisController controller = loader.getController();
            controller.initialize(Zaviyah.getInstance().getUser().getID()); // Pass the user ID

            // Get the current stage from the event and set the new scene
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 1000, 600);
            stage.setScene(scene);
            stage.setTitle("List Comparison Analysis");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
