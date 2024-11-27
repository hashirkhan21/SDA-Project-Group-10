package p.project.Controllers;

import javafx.scene.control.Alert;
import p.project.Classes.*;
import p.project.DBHandling.MySQLConnection;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MainController {
    private static MainController instance;
    private String selectedShop;
    private CustomList cart; // Use CustomList as the cart
    private String paymentMethod;
    private String orderDetails;
    public int userID;
    public int riderID;
    public int customListID;
    public String accountName ;

    private MainController() {
        cart = new CustomList(); // Initialize the cart as a CustomList
        riderID = 7;
        accountName  = "user1";
        userID = 1;
        customListID = 0;
    }

    public int getUserID(){return userID;}
    public static MainController getInstance() {
        if (instance == null) {
            instance = new MainController();
        }
        return instance;
    }

    public int getShopID(String shopName) {
        Shop shop = new Shop();

        return shop.getShopID(shopName);
    }

    public int createNewCustomList(int userID) throws Exception {
        if (customListID == 0) {
            try {
                // Insert the new custom list into the CustomList table
                int rowsAffected = MySQLConnection.executeUpdate(
                        "INSERT INTO CustomList (userID) VALUES (?)", userID);

                // Ensure the insertion was successful
                if (rowsAffected == 0) {
                    throw new RuntimeException("Failed to insert a new CustomList.");
                }

                // Retrieve the newly created customListID by using userID and ordering by the ID in descending order
                String selectQuery = "SELECT customListID FROM CustomList WHERE userID = ? ORDER BY customListID DESC LIMIT 1";
                try (ResultSet newRs = MySQLConnection.executePreparedQuery(selectQuery, userID)) {
                    if (newRs.next()) {
                        return newRs.getInt("customListID");
                    } else {
                        throw new RuntimeException("Failed to retrieve newly created CustomList ID.");
                    }
                }
            } catch (Exception e) {
                showError("Failed to create CustomList for user. Please ensure the user exists and all necessary fields are provided.");
                throw e;
            }
        } else {
            return customListID;
        }
    }

    public void clearCart() {
        cart.getCustomList().clear();
        System.out.println("Cart cleared.");
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setOrderDetails(String orderDetails) {
        this.orderDetails = orderDetails;
    }

    public void insertOrderIntoDB(int userID, int customListID, String address, String phoneNumber, String paymentMethod, double totalAmount) {
        try {
            // Get the current time as a formatted string
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            String currentDate = now.format(dateFormatter);
            String currentTime = now.format(timeFormatter);

            // Insert the order into the database
            MySQLConnection.executeUpdate(
                    "INSERT INTO OrderTable (userID, customListID, amount, status, address, phoneNumber, date, time) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    userID, customListID, totalAmount, "Pending", address, phoneNumber, currentDate, currentTime);

            System.out.println("Order successfully inserted into the database.");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to insert the order into the database.");
        }
    }

    public double calculateTotalAmount() {
        double totalAmount = 0.0;
        for (SaleLineItem1 item : cart.getCustomList()) {
            totalAmount += item.getPrice() * item.getQuantity();
        }
        return totalAmount;
    }

    public List<InventoryItem> getShopItems(int shopID) {
        return Shop.getItemsForShop(shopID);
    }

    public List<Shop> getAllShops() {
        return Shop.loadAllShops();
    }

    public SaleLineItem1 addToCart(InventoryItem inventoryItem , Item item , int quantity , int shopID) {
        SaleLineItem1 saleLineItem = new SaleLineItem1(
                inventoryItem.getItemID(), item.getItemName(),
                item.getPrice(), quantity, shopID);
        cart.additemstoList(saleLineItem, customListID);
        return  saleLineItem;
    }

    public OptimalList getLatestOptimalList(int userID) {
        OptimalList o = new OptimalList();
        return  o.getLatestOptimalList(userID);
    }

    public int createCustomListFromOptimalList(OptimalList optimalList) throws Exception {
        if (optimalList == null) {
            throw new Exception("OptimalList is null. Cannot create a custom list.");
        }

        // Create a new custom list
        int customListID = createNewCustomList(optimalList.getUserID());

        // Add items from the optimal list to the new custom list
        for (SaleLineItem1 item : optimalList.getOptimalList1()) {
            addToCustomList(customListID, item);
        }

        return customListID;
    }

    private void addToCustomList(int customListID, SaleLineItem1 item) {
        try {
            MySQLConnection.executeUpdate(
                    "INSERT INTO CustomListSaleLineItem (customListID, itemID, quantity, shopID) VALUES (?, ?, ?, ?)",
                    customListID, item.getItemID(), item.getQuantity(), item.getShopID()
            );
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to add item to custom list: " + item.getName());
        }
    }

    public void showError(String message) {
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

    public List<Order> getPendingOrders() {
        Order o = new Order();
        return o.getPendingOrders();
    }

    public List<SaleLineItem1> getOrderDetails(int customListID) {
        List<SaleLineItem1> items = new ArrayList<>();
        try (ResultSet rs = MySQLConnection.executePreparedQuery(
                "SELECT c.itemID, i.itemName, c.quantity, c.shopID " +
                        "FROM CustomListSaleLineItem c " +
                        "JOIN Item i ON c.itemID = i.itemID " +
                        "WHERE c.customListID = ?", customListID)) {

            while (rs.next()) {
                items.add(new SaleLineItem1(
                        rs.getInt("itemID"),
                        rs.getString("itemName"),
                        0.0,  // Price not needed for this
                        rs.getInt("quantity"),
                        rs.getInt("shopID")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to fetch order details.");
        }
        return items;
    }

    public String generateOrderCode(int orderID) {
        String orderCode = "ORD" + (int) (Math.random() * 10000);
        try {
            MySQLConnection.executeUpdate("INSERT INTO OrderCodes (orderID, orderCode) VALUES (?, ?)", orderID, orderCode);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to generate order code.");
        }
        return orderCode;
    }

    public void markOrderAsPicked(int orderID, String orderCode, int riderID) {
        Order o = new Order();
        o.markOrderAsPicked( orderID,  orderCode,  riderID);
    }


    public int getRiderID() {
        return riderID;
    }

    public double calculateTotalAmount(int userID, int customListID) {
        double totalAmount = 0.0;

        String query = "SELECT i.price, c.quantity " +
                "FROM CustomListSaleLineItem c " +
                "JOIN Item i ON c.itemID = i.itemID " +
                "WHERE c.customListID = ? AND EXISTS (SELECT 1 FROM CustomList WHERE userID = ? AND customListID = ?)";

        try (ResultSet rs = MySQLConnection.executePreparedQuery(query, customListID, userID, customListID)) {
            while (rs.next()) {
                double price = rs.getDouble("price");
                int quantity = rs.getInt("quantity");
                totalAmount += price * quantity;
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to calculate total amount for the custom list.");
        }
        return totalAmount;
    }

}