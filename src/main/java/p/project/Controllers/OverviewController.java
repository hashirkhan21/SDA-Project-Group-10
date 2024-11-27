package p.project.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import p.project.DBHandling.MySQLConnection;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OverviewController {

    @FXML
    private ListView<String> cartListView; // Shows itemID, itemName, quantity, shopName

    @FXML
    private Button removeItemButton;

    @FXML
    private Button proceedToCheckoutButton;

    @FXML
    private Button addMoreItemsButton;

    private MainController mainController;


    public void  initialize(){
        mainController = MainController.getInstance();
        loadCartItemsFromDB();
    }
    @FXML
    public void loadCartItemsFromDB() {
        // Clear the ListView before adding items
        cartListView.getItems().clear();

        // Ensure the mainController is linked
        if (mainController == null) {
            System.err.println("MainController is not set in OverviewController.");
            return;
        }

        int customListID = mainController.customListID;

        // Query to fetch items in the cart from the database
        try (ResultSet resultSet = MySQLConnection.executePreparedQuery(
                "SELECT c.itemID, i.itemName, c.quantity, s.name AS shopName, c.shopID " +
                        "FROM CustomListSaleLineItem c " +
                        "JOIN Item i ON c.itemID = i.itemID " +
                        "JOIN Shop s ON c.shopID = s.shopID " +
                        "WHERE c.customListID = ?", customListID)) {

            while (resultSet.next()) {
                int itemID = resultSet.getInt("itemID");
                String itemName = resultSet.getString("itemName");
                int quantity = resultSet.getInt("quantity");
                String shopName = resultSet.getString("shopName");
                int shopID = resultSet.getInt("shopID");

                // Add item details to ListView
                String itemDetails = String.format("Item ID: %d, Name: %s, Quantity: %d, Shop Name: %s (Shop ID: %d)",
                        itemID, itemName, quantity, shopName, shopID);
                System.out.println("Adding item to ListView: " + itemDetails);
                cartListView.getItems().add(itemDetails);
            }

            if (cartListView.getItems().isEmpty()) {
                System.out.println("The cart is empty. No items to display.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error fetching cart items from database.");
        }
    }

    @FXML
    private void removeSelectedItem() {
        String selectedItem = cartListView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            // No item selected
            return;
        }

        // Extract itemID from selected item string
        int itemID = Integer.parseInt(selectedItem.split(",")[0].split(":")[1].trim());
        int shopID = Integer.parseInt(selectedItem.split("\\(Shop ID: ")[1].replace(")", "").trim());

        // Remove from the database
        MySQLConnection.executeUpdate(
                "DELETE FROM CustomListSaleLineItem WHERE customListID = ? AND itemID = ? AND shopID = ?",
                mainController.customListID, itemID, shopID
        );
        System.out.println("Removed item with ID " + itemID + " from the cart.");

        // Reload the items to refresh the ListView
        loadCartItemsFromDB();
    }

    @FXML
    private void proceedToCheckout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/p/project/checkout.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) cartListView.getScene().getWindow();
            //scene.getStylesheets().add(getClass().getResource("/p/project/style.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addMoreItems() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/p/project/custom_list.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) cartListView.getScene().getWindow();
            scene.getStylesheets().add(getClass().getResource("/p/project/style.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading custom list.");
        }
    }
}
