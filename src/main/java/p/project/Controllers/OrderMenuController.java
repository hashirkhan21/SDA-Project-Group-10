package p.project.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import p.project.DBHandling.MySQLConnection;
import p.project.Classes.OptimalList;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class OrderMenuController {
    private MainController mainController;

    public void  initialize(){
        mainController = MainController.getInstance();
    }

    @FXML
    private void selectCustomList(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/p/project/custom_list.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/p/project/style.css").toExternalForm());
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void selectOptimalList(javafx.event.ActionEvent event) {
        try {
            // Fetch the latest optimal list for the user
            OptimalList optimalList = mainController.getLatestOptimalList(mainController.getUserID());

            if (optimalList == null) {
                mainController.showError("No optimal list found for the user.");
                return;
            }

            // Create a custom list from the optimal list
            int customListID = mainController.createCustomListFromOptimalList(optimalList);
            mainController.customListID = customListID; // Save the custom list ID

            // Navigate to the custom list page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/p/project/custom_list.fxml"));
            Parent root = loader.load();


            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/p/project/style.css").toExternalForm());
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);

        } catch (Exception e) {
            e.printStackTrace();
            mainController.showError("Error while processing the optimal list. Please try again.");
        }
    }



    @FXML
    private void showOrderCodes() {
        try {
            int userID = mainController.getUserID(); // Logged-in user's ID
            List<String> orderCodes = fetchOrderCodesForUser(userID);

            if (orderCodes.isEmpty()) {
                showInfo("No order codes found for your orders.");
            } else {
                StringBuilder codes = new StringBuilder("Your Order Codes:\n");
                for (String code : orderCodes) {
                    codes.append("- ").append(code).append("\n");
                }
                showInfo(codes.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to fetch order codes. Please try again.");
        }
    }


    private List<String> fetchOrderCodesForUser(int userID) {
        List<String> orderCodes = new ArrayList<>();
        try {
            String query = """
                SELECT oc.orderCode
                FROM OrderCodes oc
                JOIN OrderTable ot ON oc.orderID = ot.ID
                WHERE ot.userID = ?;
                """;
            ResultSet rs = MySQLConnection.executePreparedQuery(query, userID);

            while (rs.next()) {
                orderCodes.add(rs.getString("orderCode"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching order codes.");
        }
        return orderCodes;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void goBackToMainMenu(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/p/project/main_menu.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root,500 ,500);
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            scene.getStylesheets().add(getClass().getResource("/p/project/style.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to navigate back to the main menu.");
        }
    }

}
