package p.project.Classes;

import p.project.DBHandling.MySQLConnection;

import java.sql.ResultSet;
import java.util.ArrayList;

public class CustomList {
    public int id;
    private ArrayList<SaleLineItem1> customList;

    public CustomList() {
        customList = new ArrayList<>();
    }

    public ArrayList<SaleLineItem1> getCustomList() {
        return customList;
    }

    public void additemstoList(SaleLineItem1 saleLineItem , int customListID){

        customList.add(saleLineItem);
        // Insert or update in the database
        try {
            // First, check if this item is already in the custom list
            ResultSet rs = MySQLConnection.executePreparedQuery(
                    "SELECT quantity FROM CustomListSaleLineItem WHERE customListID = ? AND itemID = ? AND shopID = ?",
                    customListID, saleLineItem.getItemID(), saleLineItem.getShopID());

            if (rs.next()) {
                // Item already exists, update quantity
                int existingQuantity = rs.getInt("quantity");
                int newQuantity = existingQuantity + saleLineItem.getQuantity();

                MySQLConnection.executeUpdate(
                        "UPDATE CustomListSaleLineItem SET quantity = ? WHERE customListID = ? AND itemID = ? AND shopID = ?",
                        newQuantity, customListID, saleLineItem.getItemID(), saleLineItem.getShopID());
            } else {
                // Item does not exist, insert it
                MySQLConnection.executeUpdate(
                        "INSERT INTO CustomListSaleLineItem (customListID, itemID, quantity, shopID) VALUES (?, ?, ?, ?)",
                        customListID, saleLineItem.getItemID(), saleLineItem.getQuantity(), saleLineItem.getShopID());
            }

            System.out.println("Item added/updated in the database successfully.");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to add/update item in the custom list in the database.");
        }
    }
}