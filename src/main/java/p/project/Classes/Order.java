package p.project.Classes;

import p.project.DBHandling.MySQLConnection;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private int orderID;
    private int userID;
    private double amount;
    private String status;
    private String address;
    private String phoneNumber;
    private String date;
    private String time;
    private int customListID;

    // Constructor
    public Order(int orderID, int userID, double amount, String status, String address, String phoneNumber, String date, String time, int customListID) {
        this.orderID = orderID;
        this.userID = userID;
        this.amount = amount;
        this.status = status;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.date = date;
        this.time = time;
        this.customListID = customListID;
    }

    public Order(int orderID, int userID, double amount, String status, int customListID) {
        this.orderID = orderID;
        this.userID = userID;
        this.amount = amount;
        this.status = status;
        this.customListID = customListID;

    }

    public Order() {

    }

    public Order(int orderID, int userID, double amount, String status, String address, String phoneNumber,int customListID) {
        this.orderID = orderID;
        this.userID = userID;
        this.amount = amount;
        this.status = status;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.customListID = customListID;
    }

    // Getters
    public int getOrderID() {
        return orderID;
    }

    public int getUserID() {
        return userID;
    }

    public double getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public int getCustomListID() {
        return customListID;
    }

    // Optional: Override toString for better debugging
    @Override
    public String toString() {
        return String.format(
                "OrderID: %d, UserID: %d, Amount: %.2f, Status: %s, Address: %s, Phone: %s, Date: %s, Time: %s, CustomListID: %d",
                orderID, userID, amount, status, address, phoneNumber, date, time, customListID);
    }


    public List<Order> getPendingOrders() {
        List<Order> orders = new ArrayList<>();
        try {
            ResultSet rs = MySQLConnection.executeQuery(
                    "SELECT ID AS order_id, " +
                            "userID AS user_id, " +
                            "amount, " +
                            "status, " +
                            "address, " +
                            "phoneNumber AS phone_number, " +
                            "customListID " +
                            "FROM ordertable " +
                            "WHERE status = 'Pending'"
            );
            while (rs.next()) {
                orders.add(new Order(
                        rs.getInt("order_id"),      // Column alias for ID
                        rs.getInt("user_id"),       // Column alias for userID
                        rs.getDouble("amount"),     // Direct column
                        rs.getString("status"),     // Direct column
                        rs.getString("address"),    // Direct column
                        rs.getString("phone_number"), // Column alias for phoneNumber
                        rs.getInt("customListID")   // Direct column
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orders;
    }


    public void markOrderAsPicked(int orderID, String orderCode, int riderID) {
        try {
            MySQLConnection.executeUpdate(
                    "UPDATE OrderTable SET status = 'Picked' WHERE ID = ?", orderID);

            MySQLConnection.executeUpdate(
                    "INSERT INTO RiderOrder (riderID, orderID) VALUES (?, ?)", riderID, orderID);

            System.out.println("Order marked as 'Picked'.");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to mark order as picked.");
        }
    }
}
