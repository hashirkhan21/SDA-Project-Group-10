package p.project.Classes;

import p.project.DBHandling.MySQLConnection;

public class SaleLineItem1 {
    private int itemID;
    private String name; // Item name
    private double price;
    private int quantity;
    private int shopID;

    // Constructor
    public SaleLineItem1(int itemID, String name, double price, int quantity, int shopID) {
        this.itemID = itemID;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.shopID = shopID;
    }

    // Getter methods
    public int getItemID() { return itemID; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public int getShopID() { return shopID; }

    // Save this SaleLineItem to the database
    public void saveToDB() {
        try {
            MySQLConnection.executeUpdate(
                    "INSERT INTO SaleLineItem (itemID, quantity, shopID) VALUES (?, ?, ?)",
                    itemID, quantity, shopID
            );
            System.out.println("SaleLineItem saved to DB: " + name);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to save SaleLineItem to database.");
        }
    }

    public void setQuantity(int updatedQuantity) {
        quantity = updatedQuantity;
    }
}












