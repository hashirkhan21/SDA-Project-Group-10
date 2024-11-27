package p.project.Classes;

import p.project.DBHandling.DBHandler;

public class Item {
    private int itemID;
    private String itemName;
    public double price;
    private String category;

    public Item() {}

    //ABBAS
    public Item(String itemName, double price, String category) {
        this.itemName = itemName;
        this.price = price;
        this.category = category;
    }
    public Item(int itemID, String itemName, double price, String category) {
        this.itemID = itemID;
        this.itemName = itemName;
        this.price = price;
        this.category = category;
    }
    public Item(Item item) {
        this.itemID = item.itemID;
        this.itemName = item.itemName;
        this.price = item.price;
        this.category = item.category;
    }
    public static int addItem(Item item) {
        return DBHandler.addItemToDB(item);
    }
    public static void updateItem(Item item) {
        DBHandler.updateItemInDB(item);
    }

    // Getters
    public int getItemID() { return itemID; }
    public String getItemName() { return itemName; }
    public double getPrice() { return price; }
    public String getCategory() { return category; }

    // Setters
    public void setItemID(int itemID) { this.itemID = itemID; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public void setPrice(double price) { this.price = price; }
    public void setCategory(String category) { this.category = category; }
}
