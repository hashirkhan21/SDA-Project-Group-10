package p.project.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import p.project.DBHandling.MySQLConnection;
import p.project.Classes.Order;
import p.project.Classes.SaleLineItem1;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CompleteDeliveryController {
    @FXML
    private TableView<Order> pickedOrdersTable;

    @FXML
    private TableColumn<Order, Integer> orderIDColumn;

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
    public void initialize() {
        mainController = MainController.getInstance();

        // Configure table columns
        orderIDColumn.setCellValueFactory(new PropertyValueFactory<>("orderID"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

        // Load picked-up orders
        loadPickedOrders();
    }

    private void loadPickedOrders() {
        try {
            List<Order> pickedOrders = fetchPickedOrdersForRider(mainController.getRiderID());
            pickedOrdersTable.getItems().clear();
            pickedOrdersTable.getItems().addAll(pickedOrders);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load picked-up orders.");
        }
    }

    private List<Order> fetchPickedOrdersForRider(int riderID) {
        List<Order> orders = new ArrayList<>();
        try {
            String query = """
                SELECT o.ID AS order_id, o.amount, o.status, o.address, o.phoneNumber, o.customListID
                FROM RiderOrder ro
                JOIN OrderTable o ON ro.orderID = o.ID
                WHERE ro.riderID = ?
                """;
            ResultSet rs = MySQLConnection.executePreparedQuery(query, riderID);
            while (rs.next()) {
                orders.add(new Order(
                        rs.getInt("order_id"),
                        mainController.getUserID(),
                        rs.getDouble("amount"),
                        rs.getString("status"),
                        rs.getString("address"),
                        rs.getString("phoneNumber"),
                        rs.getInt("customListID")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orders;
    }

    @FXML
    private void viewCustomListDetails() {
        Order selectedOrder = pickedOrdersTable.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            showError("Please select an order to view its custom list details.");
            return;
        }

        List<SaleLineItem1> items = mainController.getOrderDetails(selectedOrder.getCustomListID());
        StringBuilder details = new StringBuilder("Custom List Details:\n");
        for (SaleLineItem1 item : items) {
            details.append(String.format("- Item: %s, Quantity: %d, Shop ID: %d\n",
                    item.getName(), item.getQuantity(), item.getShopID()));
        }
        showInfo(details.toString());
    }

    @FXML
    private void completeDelivery() {
        Order selectedOrder = pickedOrdersTable.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            showError("Please select an order to complete delivery.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Complete Delivery");
        dialog.setHeaderText("Enter the Order Code for this delivery");
        dialog.setContentText("Order Code:");

        String orderCode = dialog.showAndWait().orElse(null);
        if (orderCode == null || orderCode.isBlank()) {
            showError("Order code is required to complete the delivery.");
            return;
        }

        // Validate the order code
        if (!validateOrderCode(selectedOrder.getOrderID(), orderCode)) {
            showError("Invalid order code. Please try again.");
            return;
        }

        // Complete the delivery
        try {
            MySQLConnection.executeUpdate("DELETE FROM OrderCodes WHERE orderID = ? AND orderCode = ?", selectedOrder.getOrderID(), orderCode);
            MySQLConnection.executeUpdate("DELETE FROM RiderOrder WHERE orderID = ?", selectedOrder.getOrderID());
            MySQLConnection.executeUpdate("UPDATE OrderTable SET status = 'Complete' WHERE ID = ?", selectedOrder.getOrderID());

            showInfo("Delivery completed successfully.");
            loadPickedOrders(); // Refresh the table
            goBackToMainMenu(); // Navigate back to the main menu
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to complete the delivery.");
        }
    }

    private boolean validateOrderCode(int orderID, String orderCode) {
        try {
            String query = "SELECT COUNT(*) FROM OrderCodes WHERE orderID = ? AND orderCode = ?";
            ResultSet rs = MySQLConnection.executePreparedQuery(query, orderID, orderCode);
            if (rs.next() && rs.getInt(1) > 0) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @FXML
    private void goBackToMainMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/p/project/rider_main_menu.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root,500,500);
            Stage stage = (Stage) pickedOrdersTable.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to navigate back to the main menu.");
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
