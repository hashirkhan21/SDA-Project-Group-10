package p.project.Classes;

import p.project.DBHandling.DBHandler;

public class InventoryItem {
    private Item item;  // Item linked to InventoryItem
    private int inventoryItemID;  // Unique identifier for inventory item
    private int stockLevel;
    private int itemID;

    public InventoryItem() {}
    //ABBAS
    public InventoryItem(Item item, int stockLevel) {
        this.item = item;
        this.stockLevel = stockLevel;
    }
    public InventoryItem(Item item, int itemID, int stockLevel) {
        this.item = item;
        this.itemID = itemID;
        this.stockLevel = stockLevel;
    }
    public static int addInventoryItem(InventoryItem inventoryItem) {
        return DBHandler.addInventoryItemToDB(inventoryItem);
    }
    public static void updateInventoryItem(InventoryItem inventoryItem, int inventoryID) {
        DBHandler.updateInventoryItemInDB(inventoryItem, inventoryID);
    }
    // Getters
    public int getInventoryItemID() { return inventoryItemID; }
    public int getItemID() { return item.getItemID(); }
    public int getStockLevel() { return stockLevel; }
    public Item getItem() { return item; }

    // Setters
    public void setInventoryItemID(int inventoryItemID) { this.inventoryItemID = inventoryItemID; }
    public void setStockLevel(int stockLevel) { this.stockLevel = stockLevel; }
    public void setItem(Item item) { this.item = item; }
    public void setItemID(int itemID) {
        this.itemID = itemID;
    }
    public void setItemName(String itemName) {


    }
}
