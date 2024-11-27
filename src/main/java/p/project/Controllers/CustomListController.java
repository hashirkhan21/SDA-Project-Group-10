package p.project.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import p.project.Classes.*;

import java.io.IOException;
import java.util.List;

public class CustomListController {

    @FXML
    private ComboBox<String> shopComboBox;

    @FXML
    private VBox itemListVBox;

    private CustomList customList; // CustomList as the cart
    private MainController mainController;

    @FXML
    public void initialize() {
        // Ensure customList is initialized
        if (customList == null) {
            customList = new CustomList();
        }

        mainController = MainController.getInstance();

        int userID = mainController.userID;
        if (userID != -1) {
            try {
                int customListID = mainController.createNewCustomList(userID);
                mainController.customListID = customListID;  // Ensure this ID is set correctly
            } catch (Exception e) {
                showError("Failed to create or retrieve CustomList for the user. Please try again.");
                e.printStackTrace();
            }
        } else {
            showError("User is not valid. Please ensure you are logged in.");
        }

        loadShops();
    }

    private void loadShops() {
        if (mainController == null) {
            System.err.println("MainController is not set. Cannot load shops.");
            return;
        }

        try {
            // Get all shops using MainController
            List<Shop> shops = mainController.getAllShops();
            for (Shop shop : shops) {
                shopComboBox.getItems().add(shop.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to load shops.");
        }
    }

    @FXML
    private void loadItemsForShop() {
        if (mainController == null) {
            System.err.println("MainController is not set. Cannot load items.");
            return;
        }

        String selectedShop = shopComboBox.getValue();
        if (selectedShop == null) {
            System.err.println("No shop selected.");
            return;
        }

        int shopID = mainController.getShopID(selectedShop);
        if (shopID == -1) {
            System.err.println("Failed to retrieve shop ID.");
            return;
        }

        // Fetch inventory items for the selected shop using MainController
        try {
            List<InventoryItem> inventoryItems = mainController.getShopItems(shopID);
            itemListVBox.getChildren().clear();

            if (inventoryItems.isEmpty()) {
                itemListVBox.getChildren().add(new Label("No items available for this shop."));
            } else {
                for (InventoryItem inventoryItem : inventoryItems) {
                    Item item = inventoryItem.getItem();

                    TextField quantityField = new TextField();
                    quantityField.setPromptText("Quantity (Stock: " + inventoryItem.getStockLevel() + ")");

                    Button addToCartButton = new Button("Add to List");
                    addToCartButton.setOnAction(e -> {
                        try {
                            String quantityText = quantityField.getText();
                            if (quantityText.isEmpty()) {
                                showError("Please enter a quantity");
                                return;
                            }

                            int quantity = Integer.parseInt(quantityText);
                            if (quantity <= 0) {
                                showError("Quantity must be greater than 0");
                                return;
                            }
                            if (quantity > inventoryItem.getStockLevel()) {
                                showError("Not enough stock available");
                                return;
                            }


                           SaleLineItem1 saleLineItem = mainController.addToCart(inventoryItem,item,quantity,shopID);
                            customList.getCustomList().add(saleLineItem);
                            quantityField.clear();
                            showSuccess("Added to custom list: " + item.getItemName());
                        } catch (NumberFormatException ex) {
                            showError("Please enter a valid number");
                        }
                    });

                    VBox itemBox = new VBox();
                    itemBox.setSpacing(10);
                    itemBox.getChildren().addAll(
                            new Label(String.format("%s - Rs.%.2f", item.getItemName(), item.getPrice())),
                            quantityField,
                            addToCartButton
                    );

                    itemListVBox.getChildren().add(itemBox);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error loading items.");
        }
    }

    @FXML
    private void proceedToOverview() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/p/project/order_overview.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root,500,500);
            scene.getStylesheets().add(getClass().getResource("/p/project/style.css").toExternalForm());
            Stage stage = (Stage) itemListVBox.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error loading overview page.");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }




}
