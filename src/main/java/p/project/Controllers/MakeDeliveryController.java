package p.project.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import p.project.Classes.Order;
import p.project.Classes.SaleLineItem1;

import java.io.IOException;
import java.util.List;

public class MakeDeliveryController {

    @FXML
    private TableView<Order> orderTable;

    @FXML
    private TableColumn<Order, Integer> orderIDColumn;

    @FXML
    private TableColumn<Order, Integer> userIDColumn;

    @FXML
    private TableColumn<Order, Double> amountColumn;

    @FXML
    private TableColumn<Order, String> statusColumn;

    @FXML
    private TableColumn<Order, String> addressColumn;

    @FXML
    private TableColumn<Order, String> phoneNumberColumn;

    private MainController mainController;

    @FXML
    private void initialize() {
        mainController = MainController.getInstance();

        // Link columns with properties using their respective getters
        orderIDColumn.setCellValueFactory(new PropertyValueFactory<>("orderID"));
        userIDColumn.setCellValueFactory(new PropertyValueFactory<>("userID"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

        // Load initial data
        loadPendingOrders();
    }

    private void loadPendingOrders() {
        List<Order> orders = mainController.getPendingOrders();
        orderTable.getItems().clear();
        orderTable.getItems().addAll(orders);
    }

    @FXML
    private void showOrderDetails() {
        Order selectedOrder = orderTable.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            showError("Please select an order to view details.");
            return;
        }

        // Fetch and display custom list details
        List<SaleLineItem1> items = mainController.getOrderDetails(selectedOrder.getCustomListID());
        StringBuilder details = new StringBuilder("Order Details:\n");
        for (SaleLineItem1 item : items) {
            details.append(String.format("Item: %s, Quantity: %d, Shop: %d\n",
                    item.getName(), item.getQuantity(), item.getShopID()));
        }

        details.append(String.format("\nAddress: %s\nPhone Number: %s",
                selectedOrder.getAddress(), selectedOrder.getPhoneNumber()));

        showInfo(details.toString());
    }

    @FXML
    private void makeOrder() {
        Order selectedOrder = orderTable.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            showError("Please select an order to make.");
            return;
        }

        try {
            // Generate random Order Code
            String orderCode = mainController.generateOrderCode(selectedOrder.getOrderID());

            // Update the order status and insert into RiderOrder
            mainController.markOrderAsPicked(selectedOrder.getOrderID(), orderCode, mainController.getRiderID());

            showInfo("Order marked as 'Picked'. Order Code: " + orderCode);

            // Refresh pending orders
            loadPendingOrders();
        } catch (Exception e) {
            showError("Failed to make order: " + e.getMessage());
        }
    }

    @FXML
    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/p/project/rider_main_menu.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root,500,500);
            Stage stage = (Stage) orderTable.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to navigate back to the Main Menu.");
        }
    }


    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
