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

public class UpdateInventoryController {

    @FXML
    private TextField itemNameField;
    @FXML
    private TextField priceField;
    @FXML
    private TextField categoryField;
    @FXML
    private TextField stockLevelField;
    @FXML
    private Button updateItemButton;
    @FXML
    private Button backButton;

    // Static variable to hold itemID for global access within the class
    private static int itemID;

    // Initialize method that accepts itemID to populate fields with item details
    public void initialize(int itemID) {
        // Assign the passed itemID to the static variable
        UpdateInventoryController.itemID = itemID;
    }

    // Handler for the 'Update Item' button
    @FXML
    private void handleUpdateItem() {
        String itemName = itemNameField.getText();
        String category = categoryField.getText();
        double price;
        int stockLevel;

        try {
            price = Double.parseDouble(priceField.getText());
            stockLevel = Integer.parseInt(stockLevelField.getText());

            // Call backend to update the inventory item based on static itemID
            Abbas_Controller.editItemInShop(Abbas_Controller.shopID, itemID, itemName, category, price, stockLevel);

            // Clear the fields after updating the item
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
            // Load the previous screen or inventory screen
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
