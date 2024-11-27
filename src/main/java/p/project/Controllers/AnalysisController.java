package p.project.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import p.project.DBHandling.MySQLConnection;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class AnalysisController {

    MainController mainController;
    @FXML
    private ComboBox<String> optimalListComboBox;

    @FXML
    private ComboBox<String> customListComboBox;

    @FXML
    private DatePicker dateFilterDatePicker;

    @FXML
    private PieChart optimalPieChart;

    @FXML
    private PieChart customPieChart;

    @FXML
    private Label optimalTotalCostLabel;

    @FXML
    private Label customTotalCostLabel;

    @FXML
    private Label optimalCategoryCostLabel;

    @FXML
    private Label customCategoryCostLabel;

    @FXML
    private ListView<String> optimalSaleLineItemListView; // ListView to display SaleLineItems for optimal list

    @FXML
    private ListView<String> customSaleLineItemListView; // ListView to display SaleLineItems for custom list

    private int userID;

    public void initialize(int userID) {
        this.userID = userID;
        mainController = MainController.getInstance();
        loadListData();
    }

    // Load available optimal lists and custom lists into combo boxes
    private void loadListData() {
        ObservableList<String> optimalLists = getListNames("OptimalList");
        ObservableList<String> customLists = getListNames("CustomList");

        optimalListComboBox.setItems(optimalLists);
        customListComboBox.setItems(customLists);

    }

    // Fetch list names (date and ID) for the user from the DB
    private ObservableList<String> getListNames(String listType) {
        return mainController.getListNames(listType);
    }

    // Action for when the user clicks "Show Comparison"
// Action for when the user clicks "Show Comparison"
    @FXML
    private void onShowComparisonClick() {
        String selectedOptimalList = optimalListComboBox.getValue();
        String selectedCustomList = customListComboBox.getValue();

        if (selectedOptimalList != null && selectedCustomList != null) {
            int optimalListID = Integer.parseInt(selectedOptimalList.split(" - ")[0]);
            int customListID = Integer.parseInt(selectedCustomList.split(" - ")[0]);

            // Display data in PieCharts
            displayComparisonCharts(optimalListID, customListID);
        }
    }

    // Display the pie charts based on selected data
    private void displayComparisonCharts(int optimalListID, int customListID) {
        Map<String, Double> optimalData = getCategoryDataForList("OptimalSaleLineItem", optimalListID);
        Map<String, Double> customData = getCategoryDataForList("CustomListSaleLineItem", customListID);

        // Display data in Pie Charts
        showPieChart(optimalPieChart, optimalData, optimalTotalCostLabel, optimalCategoryCostLabel, optimalSaleLineItemListView, "OptimalList", optimalListID);
        showPieChart(customPieChart, customData, customTotalCostLabel, customCategoryCostLabel, customSaleLineItemListView, "CustomList", customListID);
    }


    // Fetch total price per category for a given list ID
    private Map<String, Double> getCategoryDataForList(String tableName, int listID) {
       return mainController.getCategoryDataForList(tableName,listID);
    }

    // Helper function to show data in PieChart
    private void showPieChart(PieChart pieChart, Map<String, Double> data, Label totalCostLabel, Label categoryCostLabel, ListView<String> saleLineItemListView, String listType, int listID) {
        pieChart.getData().clear();
        double totalCost = 0.0;

        for (Map.Entry<String, Double> entry : data.entrySet()) {
            String category = entry.getKey();
            double value = entry.getValue();
            totalCost += value;

            PieChart.Data pieData = new PieChart.Data(category, value);
            pieChart.getData().add(pieData);

            // Add event handler to display the category cost and SaleLineItems when the pie slice is clicked
            pieData.getNode().setOnMouseClicked(event -> {
                categoryCostLabel.setText("Category Cost: " + category + " - $" + String.format("%.2f", value));
                // Fetch and display SaleLineItems for this category and listID
                displaySaleLineItems(category, saleLineItemListView, listType, listID);
            });
        }

        totalCostLabel.setText("Total Cost: $" + String.format("%.2f", totalCost));
    }

    // Fetch and display SaleLineItems for the selected category
    private void displaySaleLineItems(String category, ListView<String> saleLineItemListView, String listType, int listID) {
        ObservableList<String> saleLineItems = FXCollections.observableArrayList();

        String query;
        if ("OptimalList".equals(listType)) {
            // Query for OptimalListSaleLineItem
            query = "SELECT i.itemName, o.quantity, i.price " +
                    "FROM OptimalSaleLineItem o " +
                    "JOIN Item i ON o.itemID = i.itemID " +
                    "WHERE i.category = ? AND o.optimalListID = ?";
        } else {
            // Query for CustomListSaleLineItem
            query = "SELECT i.itemName, c.quantity, i.price " +
                    "FROM CustomListSaleLineItem c " +
                    "JOIN Item i ON c.itemID = i.itemID " +
                    "WHERE i.category = ? AND c.customListID = ?";
        }

        try (ResultSet rs = MySQLConnection.executePreparedQuery(query, category, listID)) {
            while (rs.next()) {
                String itemName = rs.getString("itemName");
                int quantity = rs.getInt("quantity");
                double price = rs.getDouble("price");
                saleLineItems.add(itemName + " - Quantity: " + quantity + " - Price: $" + String.format("%.2f", price));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        saleLineItemListView.setItems(saleLineItems);
    }


    @FXML
    private void handleHome(ActionEvent event) {
        try {
            // Load the main_menu.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/p/project/main_menu.fxml"));
            Parent root = loader.load();

            // Get the current stage from the event source
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the new scene and display it
            Scene scene = new Scene(root, 500, 500);
            currentStage.setScene(scene);
            currentStage.setTitle("Main Menu");
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
