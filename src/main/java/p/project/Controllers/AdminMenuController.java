package p.project.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminMenuController {

    @FXML
    private void signOut(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/p/project/logIn-view.fxml"));
            Parent root = loader.load();

            // Get the current stage from the event source
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/p/project/style.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Log In");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not return to login page.");
        }
    }

    @FXML
    private void goToApproveShops(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/p/project/viewApplications-view.fxml"));
            Parent root = loader.load();

            // Get the current stage from the event source
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 500, 500);
            scene.getStylesheets().add(getClass().getResource("/p/project/style.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Approve Shops");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load the Approve Shops page.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void goToHandleFeedback(javafx.event.ActionEvent event) {
        try {
            // Load FeedbackView.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/p/project/FeedbackView.fxml"));
            Parent root = loader.load();

            // Get the current stage from the event source
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 800, 500); // Adjust size if necessary
            scene.getStylesheets().add(getClass().getResource("/p/project/style.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Handle Feedback");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load the Feedback page.");
        }
    }

}

