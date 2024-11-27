package p.project.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ShopMainMenuController {

    @FXML
    private void signOut(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/p/project/logIn-view.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 500, 500); // Adjust size if necessary
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void manageInventory(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/p/project/InventoryView.fxml"));
            Parent root = loader.load();

            InventoryController controller = loader.getController();
            controller.initialize(Abbas_Controller.shopID);

            // Set the new scene
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setTitle("Inventory Management");
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception (perhaps show an error dialog)
        }
    }
}
