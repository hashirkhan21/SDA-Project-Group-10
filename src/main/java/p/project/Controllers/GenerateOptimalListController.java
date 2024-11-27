package p.project.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import p.project.Classes.Main;

import java.io.IOException;

public class GenerateOptimalListController {

    // Binding input fields from FXML
    @FXML
    private TextField budgetField;

    @FXML
    private TextField familySizeField;

    @FXML
    private TextField locationField;

    @FXML
    private TextField foodEssentialsField;

    @FXML
    private TextField utilitiesField;

    @FXML
    private TextField cosmeticsField;

    // Handle Submit Button
    @FXML
    private void handleSubmit(ActionEvent event) {
        try {
            int budget = Integer.parseInt(budgetField.getText()); // Parse budget
            int familySize = Integer.parseInt(familySizeField.getText()); // Parse family size
            String location = locationField.getText(); // Get location
            int foodPercentage = Integer.parseInt(foodEssentialsField.getText()); // Food percentage
            int utilitiesPercentage = Integer.parseInt(utilitiesField.getText()); // Utilities percentage
            int cosmeticsPercentage = Integer.parseInt(cosmeticsField.getText()); // Cosmetics percentage

            // Pass the values to the backend or process as needed
            System.out.println("Budget: " + budget);
            System.out.println("Family Size: " + familySize);
            System.out.println("Location: " + location);
            System.out.println("Percentages - Food: " + foodPercentage +
                    ", Utilities: " + utilitiesPercentage +
                    ", Cosmetics: " + cosmeticsPercentage);

            // backend logic here

            // Load the OptimalListView
            try {
                FXMLLoader loader = new FXMLLoader(Main.class.getResource("/p/project/OptimalListView.fxml"));
                Parent root = loader.load();

                // Load inventory data
                int[] percentages = {foodPercentage, utilitiesPercentage, cosmeticsPercentage};
                OptimalListViewController controller = loader.getController();
                controller.initialize(budget, familySize, location, percentages);


                // Get the current stage using the button that triggered the event
                Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                // Set the new scene and show it
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/p/project/style.css").toExternalForm());
                currentStage.setScene(scene);
                currentStage.setTitle("Optimal Grocery List");
                currentStage.show();

//                primaryStage.setTitle("Generated Optimal List");
//                Scene scene = new Scene(root,800,600);
//                Stage stage = (Stage) addressField.getScene().getWindow(); // Get the current window from the addressField
//                stage.setScene(scene);

                // Get the stage from the current scene

            } catch (IOException e) {
                e.printStackTrace();
            }
//
        } catch (NumberFormatException e) {
            System.err.println("Invalid input! Please enter valid numbers where required.");
        }
    }

    // Handle Home Button
    @FXML
    private void handleHome(ActionEvent event) {
        try {
            // Load the main_menu.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/p/project/main_menu.fxml"));
            Parent root = loader.load();

            // Get the current stage from the event source
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the new scene and display it
            Scene scene = new Scene(root,500 ,500);
            currentStage.setScene(scene);
            currentStage.setTitle("Main Menu");
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
