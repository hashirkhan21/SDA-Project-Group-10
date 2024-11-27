package p.project.Classes;

import p.project.DBHandling.DB;
import p.project.DBHandling.MySQLConnection;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Shop {
    private int shopID;
    private String name;
    private String location;
    private Inventory inventory;
    private Account account;
    private String status;

    public Shop() {}

    //ABBAS
    public static ArrayList<InventoryItem> getShopInventory(int shopID) {
        return Inventory.getInventory(shopID);
    }
    public static void addItemToShop(int shopID, String name, String category, double price, int stock) {
        Inventory.addItem(shopID, name, category, price, stock);
    }
    public static void editItemInShop(int shopID, int itemID, String name, String category, double price, int stock) {
        Inventory.editItem(shopID, itemID, name, category, price, stock);
    }
    public static void deleteItemFromShop(int shopID, int itemID) {
        Inventory.deleteItem(shopID, itemID);
    }

    public static List<Shop> loadAllShops() {
        List<Shop> shopList = new ArrayList<>();
        try {
            ResultSet rs = MySQLConnection.executeQuery("SELECT shopID, name, location, inventoryID \n" +
                    "FROM Shop\n" +
                    "WHERE status = 'approved';");
            while (rs.next()) {
                Shop shop = new Shop();
                shop.setShopID(rs.getInt("shopID"));
                shop.setName(rs.getString("name"));
                shop.SetLocation(rs.getString("location"));
                // Assuming inventory loading can be added if needed
                shopList.add(shop);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to load shops from the database.");
        }
        return shopList;
    }

    private void SetLocation(String location) {
        this.location = location;
    }

    public static List<InventoryItem> getItemsForShop(int shopID) {
        List<InventoryItem> inventoryItemList = new ArrayList<>();
        try {
            ResultSet rs = MySQLConnection.executePreparedQuery(
                    "SELECT i.itemID, i.itemName, i.price, ii.stockLevel, ii.inventoryItemID " +
                            "FROM Item i " +
                            "JOIN InventoryItem ii ON i.itemID = ii.itemID " +
                            "JOIN Inventory_InventoryItem iii ON ii.inventoryItemID = iii.inventoryItemID " +
                            "JOIN Inventory inv ON iii.inventoryID = inv.inventoryID " +
                            "JOIN Shop s ON inv.inventoryID = s.inventoryID " +
                            "WHERE s.shopID = ?", shopID);

            while (rs.next()) {
                Item item = new Item();
                item.setItemID(rs.getInt("itemID"));
                item.setItemName(rs.getString("itemName"));
                item.setPrice(rs.getDouble("price"));

                InventoryItem inventoryItem = new InventoryItem();
                inventoryItem.setItem(item);
                inventoryItem.setItemID(rs.getInt("itemID"));
                inventoryItem.setStockLevel(rs.getInt("stockLevel"));
                inventoryItemList.add(inventoryItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to load inventory items for shop.");
        }
        return inventoryItemList;
    }


    public int getShopID(String shopName){
        try (ResultSet rs = MySQLConnection.executePreparedQuery(
                "SELECT shopID FROM Shop WHERE name = ?", shopName)) {
            if (rs.next()) {
                return rs.getInt("shopID");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;

    }

    // Public getters to access private fields
    public int getShopID() { return shopID; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public Inventory getInventory() { return inventory; }

    // Public setters to modify private fields
    public void setShopID(int shopID) { this.shopID = shopID; }
    public void setName(String name) { this.name = name; }

    public void setInventory(Inventory inventory) { this.inventory = inventory; }
    public String getStatus(){return status;}
    public void setLocation(String location) { this.location = location; DB.insertLocation(location);}


    public void setStatus(String status){this.status = status;}
    public void setAccount(Account account){this.account =account;}
    // Function that do ACTUAL WORK

    public boolean createAccount(Account account){
        this.account = account;
        return true;
    }

    public boolean logIN(Account account) {
        String accountName = account.getName();
        Shop details = DB.getShopDetails(accountName);

        if (details != null) {
            this.shopID = details.getShopID();
            this.name = details.getName();
            this.location = details.getLocation();
            this.inventory = null;
            this.status = details.getStatus();
            this.account = account;

            System.out.println("Shop details loaded successfully for account: " + accountName);
            return true;
        } else {
            System.out.println("No shop profile found for the account: " + accountName);
            return false;
        }
    }

    public void updateStatus(String status){
        DB.updateShopStatus(account, status);
    }

}