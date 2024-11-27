package p.project.Controllers;

import javafx.beans.property.*;
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
import p.project.Classes.OptimalList;
import p.project.Classes.SaleLineItem;

import java.io.IOException;
import java.util.ArrayList;

public class OptimalListViewController {

    @FXML
    private TableView<SaleLineItem> saleLineItemTable;

    @FXML
    private TableColumn<SaleLineItem, String> itemNameColumn;

    @FXML
    private TableColumn<SaleLineItem, String> categoryColumn;

    @FXML
    private TableColumn<SaleLineItem, Double> priceColumn;

    @FXML
    private TableColumn<SaleLineItem, Integer> quantityColumn;

    @FXML
    private TableColumn<SaleLineItem, Integer> shopIDColumn;

    @FXML
    private TableColumn<SaleLineItem, Void> actionColumn; // New column for actions

    private static ArrayList<SaleLineItem> saleLineItemsList;

    private ObservableList<SaleLineItem> observableSaleLineItems;

    public void initialize(int budget, int familySize, String location, int[] percentages) {

        OptimalList optimalList = Abbas_Controller.generateOptimalGroceryList(Abbas_Controller.userID, budget, familySize, location, percentages);
        saleLineItemsList = optimalList.getOptimalList();

        itemNameColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getItem().getItemName()));
        categoryColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getItem().getCategory()));
        priceColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getItem().getPrice()));
        quantityColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getQuantity()));
        shopIDColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getShop()));

        // Set up the action column
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox actionBox = new HBox(10, editButton, deleteButton);

            {
                editButton.setOnAction(event -> {
                    SaleLineItem item = getTableView().getItems().get(getIndex());
                    handleEdit(item); // Call the edit handler
                });

                deleteButton.setOnAction(event -> {
                    SaleLineItem item = getTableView().getItems().get(getIndex());
                    handleDelete(item, optimalList.getID()); // Call the delete handler
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(actionBox);
                }
            }
        });

        observableSaleLineItems = FXCollections.observableArrayList();
        populateTable();
        saleLineItemTable.setItems(observableSaleLineItems);
    }

    private void populateTable() {
        observableSaleLineItems.addAll(saleLineItemsList);
    }

    private void handleEdit(SaleLineItem item) {
        // Implement your edit logic
        System.out.println("Editing: " + item.getItem().getItemName());
    }

    private void handleDelete(SaleLineItem item, int optimalListID) {
        // Implement your delete logic
        System.out.println("Deleting: " + item.getItem().getItemName());
        observableSaleLineItems.remove(item); // Example deletion from table

        Abbas_Controller.deleteOptimalListItem(optimalListID, item.getItem().getItemID());
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
