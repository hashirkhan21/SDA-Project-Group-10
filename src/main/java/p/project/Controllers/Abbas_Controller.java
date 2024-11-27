package p.project.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import p.project.Classes.*;

import java.util.ArrayList;

public class Abbas_Controller {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    public static int shopID;
    public static int userID;
    public static int adminID;

    // Handle Feedback

    public static ArrayList<Feedback> handleFeedback() {
        return Feedback.getAllFeedbacks();
    }

    public static Feedback selectFeedback(int feedbackID) {
        return Feedback.getFeedback(feedbackID);
    }

    public static void acceptFeedback(int feedbackID, String comments, String status, String priority) {
        Admin.addEscalationResponse(feedbackID, comments, status, priority);
    }

    public static void rejectFeedback(int feedbackID, String comments, String status) {
        Admin.addRejectionResponse(feedbackID, comments, status);
    }


    // Manage Inventory

    public static ArrayList<InventoryItem> manageInventory(int shopID) {
        return Shop.getShopInventory(shopID);
    }

    public static void addItemToShop(int shopID, String name, String category, double price, int stock) {
        Shop.addItemToShop(shopID, name, category, price, stock);
    }

    public static void editItemInShop(int shopID, int itemID, String name, String category, double price, int stock) {
        Shop.editItemInShop(shopID, itemID, name, category, price, stock);
    }

    public static void deleteItemFromShop(int shopID, int itemID) {
        Shop.deleteItemFromShop(shopID, itemID);
    }

    // Generate Optimal List

    public static OptimalList generateOptimalGroceryList(int userID, int budget, int familySize, String location, int[] percentages) {
        return OptimalList.generateList(userID, budget, familySize, location, percentages);
    } // UI controller of generate optimal list should store the optimal list ID in a class variable for future use

    public static void confirmOptimalListItem(int optimalListID, int itemID) {
        // sequence diagram says do nothing
    }

    public static boolean modifyOptimalListItem(int optimalListID, int itemID, int quantity) {
        return OptimalList.modifyOptimalListItem(optimalListID, itemID, quantity);
    }

    public static boolean deleteOptimalListItem(int optimalListID, int itemID) {
        return OptimalList.deleteOptimalListItem(optimalListID, itemID);
    }


    // Share Optimal List

    public static boolean shareOptimalList(int month, String receiver) {

        return false;
    }
}