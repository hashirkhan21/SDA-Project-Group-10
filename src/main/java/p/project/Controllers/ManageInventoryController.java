package p.project.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.Node;

public class ManageInventoryController {

    @FXML
    private TextField itemNameField;
    @FXML
    private TextField priceField;
    @FXML
    private TextField categoryField;
    @FXML
    private TextField stockLevelField;
    @FXML
    private Button addItemButton;
    @FXML
    private Button backButton;

    // Handler for the 'Add Item' button
    @FXML
    private void handleAddItem() {
        String itemName = itemNameField.getText();
        String category = categoryField.getText();
        double price;
        int stockLevel;

        try {
            price = Double.parseDouble(priceField.getText());
            stockLevel = Integer.parseInt(stockLevelField.getText());

            // Here you can call the backend or logic to add the new inventory item
            // For now, just printing the details for confirmation
            System.out.println("Item Added: ");
            System.out.println("Name: " + itemName);
            System.out.println("Category: " + category);
            System.out.println("Price: " + price);
            System.out.println("Stock Level: " + stockLevel);

            Abbas_Controller.addItemToShop(Abbas_Controller.shopID, itemName, category, price, stockLevel);

            // Clear the fields after adding the item
            itemNameField.clear();
            priceField.clear();
            categoryField.clear();
            stockLevelField.clear();

        } catch (NumberFormatException e) {
            System.out.println("Error: Please enter valid numbers for price and stock level.");
        }
    }

    // Handler for the 'Back' button
    @FXML
    private void handleBackAction(ActionEvent event) {
        try {
            // Load the previous screen or home screen (replace "InventoryView.fxml" with your actual FXML file)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/p/project/InventoryView.fxml"));
            Parent root = loader.load();

            InventoryController controller = loader.getController();
            controller.initialize(Abbas_Controller.shopID);

            // Get the current stage
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 800, 600);

            // Set the new scene and show it
            currentStage.setScene(scene);
            currentStage.setTitle("Inventory Management");
            currentStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
