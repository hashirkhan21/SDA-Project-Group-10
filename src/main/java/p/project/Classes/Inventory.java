package p.project.Classes;

import p.project.DBHandling.DBHandler;

import java.util.ArrayList;

public class Inventory {
private ArrayList<InventoryItem> inventoryList;

public Inventory() {
    inventoryList = new ArrayList<>();
}

public static ArrayList<InventoryItem> getInventory(int shopID) {
        return DBHandler.getInventoryFromDB(shopID);
    }

// Add new InventoryItem
    public static void addItem(int shopID, String name, String category, double price, int stock) {
        Item item = new Item(name, price, category);
        int itemID = Item.addItem(item); // returns -1 if id not found
        item.setItemID(itemID);

        InventoryItem inventoryItem = new InventoryItem(item, itemID, stock);
        int inventoryItemID = InventoryItem.addInventoryItem(inventoryItem);

        int inventoryID = DBHandler.getInventoryIDFromDB(shopID);
        DBHandler.addInventoryInventoryItem(inventoryID, inventoryItemID);
    }

    public static void editItem(int shopID, int itemID, String name, String category, double price, int stock) {
        Item item = new Item(itemID, name, price, category);
        Item.updateItem(item);

        int inventoryID = DBHandler.getInventoryIDFromDB(shopID);



        InventoryItem inventoryItem = new InventoryItem(item, item.getItemID(), stock);
        InventoryItem.updateInventoryItem(inventoryItem, inventoryID);
    }

    public static void deleteItem(int shopID, int itemID) {
        int inventoryID = DBHandler.getInventoryIDFromDB(shopID);

        DBHandler.deleteItemFromDB(inventoryID, itemID);
    }

    // Getters
    public ArrayList<InventoryItem> getInventoryList() { return inventoryList; }

    // Setters
    public void setInventoryList(ArrayList<InventoryItem> inventoryList) { this.inventoryList = inventoryList; }
}