package p.project.Controllers;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import p.project.Classes.InventoryItem;
import p.project.Classes.Main;


import java.io.IOException;
import java.util.ArrayList;

public class InventoryController {
    @FXML
    private TableView<InventoryItem> inventoryTable;

    @FXML
    private TableColumn<InventoryItem, String> itemNameColumn;

    @FXML
    private TableColumn<InventoryItem, Double> priceColumn;

    @FXML
    private TableColumn<InventoryItem, String> categoryColumn;

    @FXML
    private TableColumn<InventoryItem, Integer> stockLevelColumn;

    @FXML
    private TableColumn<InventoryItem, Void> actionsColumn;

    // Static ArrayList to hold the inventory data
    private static ArrayList<InventoryItem> inventoryList;

    private ObservableList<InventoryItem> inventoryItems;

    public void initialize(int shopID) {
        // Call Abbas_Controller to get the inventory data
        inventoryList = Abbas_Controller.manageInventory(shopID);

        // Set up the table columns
        itemNameColumn.setCellValueFactory(param -> {
            // Access the itemName from the nested Item object
            if (param.getValue() != null && param.getValue().getItem() != null) {
                return new ReadOnlyStringWrapper(param.getValue().getItem().getItemName());
            }
            return new ReadOnlyStringWrapper(""); // Fallback in case of null
        });

        priceColumn.setCellValueFactory(param -> {
            // Access the price from the nested Item object
            if (param.getValue() != null && param.getValue().getItem() != null) {
                return new ReadOnlyObjectWrapper<>(param.getValue().getItem().getPrice());
            }
            return new ReadOnlyObjectWrapper<>(0.0); // Fallback in case of null
        });

        categoryColumn.setCellValueFactory(param -> {
            // Access the category from the nested Item object
            if (param.getValue() != null && param.getValue().getItem() != null) {
                return new ReadOnlyStringWrapper(param.getValue().getItem().getCategory());
            }
            return new ReadOnlyStringWrapper(""); // Fallback in case of null
        });

        stockLevelColumn.setCellValueFactory(param -> {
            // Access the stockLevel directly from the InventoryItem object
            if (param.getValue() != null) {
                return new ReadOnlyObjectWrapper<>(param.getValue().getStockLevel());
            }
            return new ReadOnlyObjectWrapper<>(0); // Fallback in case of null
        });

        // Set up the actions column
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");

            {
                editButton.setOnAction(event -> {
                    InventoryItem item = getTableRow().getItem();
                    if (item != null) {
                        onEditItem(event, item);
                    }
                });

                deleteButton.setOnAction(event -> {
                    InventoryItem item = getTableRow().getItem();
                    if (item != null) {
                        onDeleteItem(item);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox actions = new HBox(5, editButton, deleteButton);
                    setGraphic(actions);
                }
            }
        });

        // Load the data into the table
        inventoryItems = FXCollections.observableArrayList();
        populateTable(); // Call the method to populate the table
        inventoryTable.setItems(inventoryItems);
    }

    private void populateTable() {
        // Iterate over the inventoryList and populate the table
        for (InventoryItem inventoryItem : inventoryList) {
            // TODO: Add any additional logic if required to process the inventoryItem before adding
            inventoryItems.add(inventoryItem); // Add the item to the observable list
        }
    }

    @FXML
    private void onAddProduct(ActionEvent event) {
        // Logic for adding a product
        System.out.println("Add Product button clicked");

        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/p/project/ManageInventoryView.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 800, 600); // Set the size you want

            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(scene);
            currentStage.setTitle("Add Item");
            currentStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onEditItem(ActionEvent event, InventoryItem item) {
        // Logic for editing an item
        System.out.println("Editing: " + item.getItem().getItemName());

        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/p/project/UpdateInventoryView.fxml"));
            Parent root = loader.load();

            UpdateInventoryController controller = loader.getController();
            controller.initialize(item.getItem().getItemID());

            Scene scene = new Scene(root, 800, 600); // Set the size you want

            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(scene);
            currentStage.setTitle("Update Item");
            currentStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            // Load the Shop Main Menu FXML
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/p/project/shop_main_menu.fxml"));
            Parent root = loader.load();

            // Set the new scene
            Scene scene = new Scene(root, 800, 600); // Adjust dimensions as needed
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(scene);
            currentStage.setTitle("Shop Main Menu");
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void onDeleteItem(InventoryItem item) {
        inventoryItems.remove(item);
        System.out.println("Deleted: " + item.getItem().getItemName() + ", ID: " + item.getItemID());


        Abbas_Controller.deleteItemFromShop(Abbas_Controller.shopID, item.getItem().getItemID());
    }
}
