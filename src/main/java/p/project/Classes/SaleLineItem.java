package p.project.Classes;

import p.project.DBHandling.DBHandler;

public class SaleLineItem {

    private

    Item item;
    int quantity;
    int shopID;

    public

    SaleLineItem() {}

    public  SaleLineItem(Item item, int quantity, int shopID) {
        this.item = new Item(item);
        this.quantity = quantity;
        this.shopID = shopID;
    }

    public static void deleteOptimalListItem(int optimalListID, int itemID) {
        DBHandler.deleteOptimalListItemFromDB(optimalListID, itemID);
    }


    // Getters
    public  Item getItem() { return item; }
    public  int getQuantity() { return quantity; }
    public int getShop() { return shopID; }

    // Setters
    public void setItem(Item item) { this.item = item; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setShop(int shopID) { this.shopID = shopID; }

}
