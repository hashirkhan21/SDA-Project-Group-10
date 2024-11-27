package p.project.Classes;
import p.project.DBHandling.DBHandler;
import p.project.DBHandling.MySQLConnection;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OptimalList {
    private

    int ID;
    int userID;
    String date;
    ArrayList<String> categories;
    int[] percentages;
    double totalCost;
    ArrayList<SaleLineItem1> optimalList1;
    ArrayList<SaleLineItem> optimalList;
    public

    OptimalList() {}
    public OptimalList(int userID, String currentDate, ArrayList<String> categories, int[] percentages) {
        this.userID = userID;
        this.date = currentDate;
        this.categories = new ArrayList<>(categories);
        this.percentages = percentages.clone();
    }


    public OptimalList getLatestOptimalList(int userID){
        OptimalList optimalList = null;
        try {
            // Fetch the latest optimal list for the user
            String query = "SELECT * FROM OptimalList WHERE userID = ? ORDER BY date DESC LIMIT 1";
            ResultSet resultSet = MySQLConnection.executePreparedQuery(query, userID);

            if (resultSet.next()) {
                optimalList = new OptimalList();
                optimalList.setID(resultSet.getInt("ID"));
                optimalList.setUserID(resultSet.getInt("userID"));
                optimalList.setDate(resultSet.getString("date"));
                optimalList.setTotalCost(resultSet.getDouble("totalCost"));

                // Fetch the optimal list sale line items
                String saleLineItemsQuery = "SELECT * FROM OptimalSaleLineItem WHERE optimalListID = ?";
                ResultSet saleLineItemsResult = MySQLConnection.executePreparedQuery(saleLineItemsQuery, optimalList.getID());

                ArrayList<SaleLineItem1> saleLineItems = new ArrayList<>();
                while (saleLineItemsResult.next()) {
                    SaleLineItem1 item = new SaleLineItem1(
                            saleLineItemsResult.getInt("itemID"),
                            "Item Name Placeholder", // Replace with a JOIN query if needed to fetch item name
                            0.0, // Replace with a JOIN query if needed to fetch price
                            saleLineItemsResult.getInt("quantity"),
                            saleLineItemsResult.getInt("shopID")
                    );
                    saleLineItems.add(item);
                }
                optimalList.setOptimalList1(saleLineItems);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to fetch the latest optimal list. Please try again.");
        }

        return optimalList;
    }
    private static void showError(String s) {
    }


    public static OptimalList generateList(int userID, int budget, int familySize, String location, int[] percentages) {
        // Creates user object

        User user = new User(userID, budget, location, familySize);

        /////////// save user details to DB

        User.addUserDetails(user);

        // creates an optimal list object

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = dateFormat.format(new Date());

        ArrayList<String> categories = new ArrayList<String>();
        categories.add("Food Essentials");
        categories.add("Utilities");
        categories.add("Cosmetics");

        OptimalList optimalListClass = new OptimalList(userID, currentDate, categories, percentages);

        //////// save optimal list to db and fetch optimal list ID and save in optimal list

        int optimalListClassID = DBHandler.addOptimalList(optimalListClass);

        optimalListClass.setID(optimalListClassID);
        // Fetch all shops in the same location as user

        int[] allShops = DBHandler.getAllShopsInLocation(location);

        // initiailize total cost

        double totalCost = 0.0;

        if(allShops.length > 0) {

            // Make list to be populated

            ArrayList<SaleLineItem> optimalList = new ArrayList<>();

            // Fetch inventories of all shops received

            ArrayList<ArrayList<InventoryItem>> allInventories = new ArrayList<>();
            ArrayList<InventoryItem> currentInventory = new ArrayList<>();

            for(int i = 0; i < allShops.length; i++) {
                currentInventory = DBHandler.getInventoryFromDB(allShops[i]);
                allInventories.add(currentInventory);
            }

            // apply algorithm on fetched items

            // calculate budget (by percentage) per category

            double[] budgetPerCategory = {(percentages[0] / 100.0 * budget), (percentages[1] / 100.0 * budget), (percentages[2] / 100.0 * budget)};

            // start filling the list with that item category till budget is exhausted or lists are finished

            Item tempItem;
            SaleLineItem tempSaleLineItem;


            double tempBudget = budgetPerCategory[0];
            int inventoryCount = 0;

            while(tempBudget > 0 && inventoryCount < allInventories.size()) {

                currentInventory = allInventories.get(inventoryCount);

                for(int i = 0; i < currentInventory.size(); i++) {

                    tempItem = currentInventory.get(i).getItem();

                    if(tempItem.getCategory().equals(categories.getFirst())) {
                        // make sale line item and add to optimal list.
                        if(tempBudget - tempItem.price < 0) {
                            tempBudget -= tempItem.price;
                            break;
                        }

                        // creates a sale line item array and stores in optimalList attribute

                        tempSaleLineItem = new SaleLineItem(tempItem, familySize, allShops[inventoryCount]);
                        optimalList.add(tempSaleLineItem);
                    }
                }

                inventoryCount++;
            }


            // for category 2

            tempBudget = budgetPerCategory[1];
            inventoryCount = 0;

            while(tempBudget > 0 && inventoryCount < allInventories.size()) {

                currentInventory = allInventories.get(inventoryCount);

                for(int i = 0; i < currentInventory.size(); i++) {

                    tempItem = currentInventory.get(i).getItem();

                    if(tempItem.getCategory().equals(categories.get(1))) {
                        // make sale line item and add to optimal list.
                        if(tempBudget - tempItem.price < 0) {
                            tempBudget -= tempItem.price;
                            break;
                        }

                        // creates a sale line item array and stores in optimalList attribute

                        tempSaleLineItem = new SaleLineItem(tempItem, familySize, allShops[inventoryCount]);
                        optimalList.add(tempSaleLineItem);
                    }
                }

                inventoryCount++;
            }

            // for category 3


            tempBudget = budgetPerCategory[2];
            inventoryCount = 0;

            while(tempBudget > 0 && inventoryCount < allInventories.size()) {

                currentInventory = allInventories.get(inventoryCount);

                for(int i = 0; i < currentInventory.size(); i++) {

                    tempItem = currentInventory.get(i).getItem();

                    if(tempItem.getCategory().equals(categories.get(2))) {
                        // make sale line item and add to optimal list.
                        if(tempBudget - tempItem.price < 0) {
                            tempBudget -= tempItem.price;
                            break;
                        }

                        // creates a sale line item array and stores in optimalList attribute

                        tempSaleLineItem = new SaleLineItem(tempItem, familySize, allShops[inventoryCount]);
                        optimalList.add(tempSaleLineItem);
                    }
                }

                inventoryCount++;
            }

            // Calculate total cost of all items and save

            for(int i = 0; i < optimalList.size(); i++) {
                totalCost += optimalList.get(i).getItem().getPrice();
            }

            optimalListClass.setTotalCost(totalCost);

            // save newly created optimal list in object of OptimalList class

            optimalListClass.setOptimalList(optimalList);

            for(int i = 0; i < optimalList.size(); i++) {
                tempItem = optimalList.get(i).getItem();

                System.out.println("Name: " + tempItem.getItemName() + " Category: " + tempItem.getCategory() + ", ShopID: " + optimalList.get(i).getShop());
            }

            // saves user details, optimal list, and sale line item array into db (sale line item array will be stored in optimalSaleLineItem table in db)

            DBHandler.addOptimalListItems(optimalList, optimalListClassID);
        }


        return optimalListClass;
    }
    public static boolean modifyOptimalListItem(int optimalListID, int itemID, int quantity) {
        // modifies OptimalSaleLineItem in database through its class "Sale Line Item"
        return false;
    }
    public static boolean deleteOptimalListItem(int optimalListID, int itemID) {
        // deletes OptimalSaleLineItem in databse through its class "Sale Line Item"

        SaleLineItem.deleteOptimalListItem(optimalListID, itemID);
        return false;
    }



    // Getters

    public  ArrayList<SaleLineItem> getOptimalList() { return optimalList; }
    public  void setOptimalList(ArrayList<SaleLineItem> optimalList) { this.optimalList = optimalList; }

    // Getters
    public int getID() { return ID; }
    public int getUserID() { return userID; }
    public String getDate() { return date; }
    public ArrayList<String> getCategories() { return categories; }
    public int[] getPercentages() { return percentages; }
    public double getTotalCost() { return totalCost; }
    public  ArrayList<SaleLineItem1> getOptimalList1() { return optimalList1; }

    // Setters
    public void setID(int ID) { this.ID = ID; }
    public  void setUserID(int userID) { this.userID = userID; }
    public void setDate(String date) { this.date = date; }
    public  void setCategories(ArrayList<String> categories) { this.categories = categories; }
    public  void setPercentages(int[] percentages) { this.percentages = percentages; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }
    public void setOptimalList1(ArrayList<SaleLineItem1> optimalList1) { this.optimalList1 = optimalList1; }

}
