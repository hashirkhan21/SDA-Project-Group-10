package p.project.DBHandling;

import p.project.Classes.*;

import java.sql.*;
import java.util.ArrayList;

public class DBHandler {
    private static final String URL = "jdbc:mysql://localhost:3306/db1";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }


    // Handle Feedback

    public static ArrayList<Feedback> getAllFeedbacksFromDB() {
        ArrayList<Feedback> feedbackList = new ArrayList<>();
        String sql = "SELECT f.feedbackID, f.userID, f.userComments, f.adminComments, f.type, f.status, f.priority " +
                "FROM Feedback f WHERE f.status IS NULL";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                int feedbackID = rs.getInt("feedbackID");
                int userID = rs.getInt("userID");
                String userComments = rs.getString("userComments");
                String adminComments = rs.getString("adminComments");
                String type = rs.getString("type");
                String status = rs.getString("status");
                String priority = rs.getString("priority");

                Feedback feedback = new Feedback(feedbackID, userID, userComments, adminComments, type, status, priority);
                feedbackList.add(feedback);
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Handle exception
        }

        return feedbackList;
    }


    public static Feedback getFeedbackFromDB(int feedbackID) {

        Feedback feedback = null;  // Feedback object to be returned

        String sql = "SELECT feedbackID, userID, userComments, adminComments, type, status, priority " +
                "FROM Feedback WHERE feedbackID = ?";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, feedbackID);

            // Execute the query
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {

                    int userID = rs.getInt("userID");
                    String userComments = rs.getString("userComments");
                    String adminComments = rs.getString("adminComments");
                    String type = rs.getString("type");
                    String status = rs.getString("status");
                    String priority = rs.getString("priority");

                    feedback = new Feedback(feedbackID, userID, userComments, adminComments, type, status, priority);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Handle SQL exceptions
        }

        return feedback;
    }

    public static boolean updateFeedbackToDB(Feedback feedback) {
        boolean isUpdated = false;

        String sql = "UPDATE Feedback SET " +
                "userID = ?, " +
                "userComments = ?, " +
                "adminComments = ?, " +
                "type = ?, " +
                "status = ?, " +
                "priority = ? " +
                "WHERE feedbackID = ?";  // SQL query to update a feedback record based on feedbackID

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Set the parameters for the prepared statement
            statement.setInt(1, feedback.getUserID());
            statement.setString(2, feedback.getUserComments());
            statement.setString(3, feedback.getAdminComments());
            statement.setString(4, feedback.getType());
            statement.setString(5, feedback.getStatus());
            statement.setString(6, feedback.getPriority());
            statement.setInt(7, feedback.getFeedbackID());  // Use feedbackID to identify the record

            // Execute the update
            int rowsAffected = statement.executeUpdate();

            // Check if any rows were affected (indicating success)
            if (rowsAffected > 0) {
                isUpdated = true;
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Handle SQL exceptions
        }

        return isUpdated;

    }


    //////////////  Manage Inventory  /////////////

    public static ArrayList<InventoryItem> getInventoryFromDB(int shopID) {
        // bring out all inventory items of this shop
        // item object is preferable instead of item id only
        // return all the values

        ArrayList<InventoryItem> inventoryItems = new ArrayList<>();

        String itemQuery = "SELECT Item.itemID, Item.itemName, Item.price, Item.category, InventoryItem.inventoryItemID " +
                "FROM Item " +
                "JOIN InventoryItem ON Item.itemID = InventoryItem.itemID " +
                "JOIN Inventory_InventoryItem ON InventoryItem.inventoryItemID = Inventory_InventoryItem.inventoryItemID " +
                "JOIN Inventory ON Inventory_InventoryItem.inventoryID = Inventory.inventoryID " +
                "WHERE Inventory.inventoryID = (SELECT inventoryID FROM Shop WHERE shopID = ?)";

        String stockLevelQuery = "SELECT stockLevel FROM InventoryItem WHERE inventoryItemID = ?";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement itemStmt = connection.prepareStatement(itemQuery);
             PreparedStatement stockStmt = connection.prepareStatement(stockLevelQuery)) {

            // Set the shopID parameter for the item query
            itemStmt.setInt(1, shopID);

            // Execute the item query
            try (ResultSet itemRS = itemStmt.executeQuery()) {
                while (itemRS.next()) {
                    // Extract item details
                    int itemID = itemRS.getInt("itemID");
                    String itemName = itemRS.getString("itemName");
                    double price = itemRS.getDouble("price");
                    String category = itemRS.getString("category");
                    int inventoryItemID = itemRS.getInt("inventoryItemID");

                    // Create an Item object
                    Item item = new Item(itemID, itemName, price, category);

                    // Fetch the stock level for this inventory item
                    int stockLevel = 0; // Default stock level
                    stockStmt.setInt(1, inventoryItemID); // Set the inventoryItemID dynamically
                    try (ResultSet stockRS = stockStmt.executeQuery()) {
                        if (stockRS.next()) {
                            stockLevel = stockRS.getInt("stockLevel");
                        }
                    }

                    // Create an InventoryItem object and add it to the list
                    InventoryItem inventoryItem = new InventoryItem(item, stockLevel);
                    inventoryItems.add(inventoryItem);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Handle SQL exceptions
        }

        return inventoryItems;
    }

    public static int addItemToDB(Item item) {
        // add item to database
        // find id of newly added item and return

        String insertQuery = "INSERT INTO Item (itemName, price, category) VALUES (?, ?, ?)";
        int generatedItemID = -1; // Default value for error case

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {

            // Set the parameters for the query
            pstmt.setString(1, item.getItemName());
            pstmt.setDouble(2, item.getPrice());
            pstmt.setString(3, item.getCategory());

            // Execute the update
            int affectedRows = pstmt.executeUpdate();

            // Check if the insertion was successful
            if (affectedRows > 0) {
                // Retrieve the auto-generated itemID
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        generatedItemID = generatedKeys.getInt(1); // Get the first auto-generated column
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Handle SQL exceptions
        }

        return generatedItemID;
    }

    public static int addInventoryItemToDB(InventoryItem inventoryItem) {
        // take inventory item object and insert into db
        // return id of added row

        String insertQuery = "INSERT INTO InventoryItem (itemID, stockLevel) VALUES (?, ?)";
        int generatedInventoryItemID = -1; // Default value for error case

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {

            // Set the parameters for the query
            pstmt.setInt(1, inventoryItem.getItem().getItemID()); // Set the itemID from the associated Item object
            pstmt.setInt(2, inventoryItem.getStockLevel());

            // Execute the update
            int affectedRows = pstmt.executeUpdate();

            // Check if the insertion was successful
            if (affectedRows > 0) {
                // Retrieve the auto-generated inventoryItemID
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        generatedInventoryItemID = generatedKeys.getInt(1); // Get the first auto-generated column
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Handle SQL exceptions
        }

        return generatedInventoryItemID;
    }

    public static int getInventoryIDFromDB(int shopID) {

        String query = "SELECT inventoryID FROM Shop WHERE shopID = ?";
        int inventoryID = -1;

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setInt(1, shopID);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                inventoryID = rs.getInt("inventoryID"); // Retrieve the inventoryID from the result set
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return inventoryID;
    }

    public static void addInventoryInventoryItem(int inventoryID, int inventoryItemID) {
        // add row to inventory x inventory id table

        String insertQuery = "INSERT INTO Inventory_InventoryItem (inventoryID, inventoryItemID) VALUES (?, ?)";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {

            pstmt.setInt(1, inventoryID);         // Set the inventoryID
            pstmt.setInt(2, inventoryItemID);     // Set the inventoryItemID

            int affectedRows = pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace(); // Handle SQL exceptions
        }

    }

    public static void updateItemInDB(Item item) {
        // update item object in database

        String updateQuery = "UPDATE Item SET itemName = ?, price = ?, category = ? WHERE itemID = ?";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {

            pstmt.setString(1, item.getItemName());  // Set the itemName
            pstmt.setDouble(2, item.getPrice());     // Set the price
            pstmt.setString(3, item.getCategory());  // Set the category
            pstmt.setInt(4, item.getItemID());       // Set the itemID for WHERE clause

            int affectedRows = pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace(); // Handle SQL exceptions
        }
    }

    public static void updateInventoryItemInDB(InventoryItem inventoryItem, int inventoryID) {
        // update inventory item in database

        String selectQuery = "SELECT InventoryItem.inventoryItemID " +
                "FROM InventoryItem " +
                "JOIN Inventory_InventoryItem ON InventoryItem.inventoryItemID = Inventory_InventoryItem.inventoryItemID " +
                "WHERE Inventory_InventoryItem.inventoryID = ? AND InventoryItem.itemID = ? " +
                "LIMIT 1";

        String updateQuery = "UPDATE InventoryItem SET stockLevel = ? WHERE inventoryItemID = ?";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement selectStmt = connection.prepareStatement(selectQuery);
             PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {

            // Set parameters for the SELECT query
            selectStmt.setInt(1, inventoryID);
            selectStmt.setInt(2, inventoryItem.getItemID());

            ResultSet resultSet = selectStmt.executeQuery();

            if (resultSet.next()) {
                int inventoryItemID = resultSet.getInt("inventoryItemID");

                // Set parameters for the UPDATE query
                updateStmt.setInt(1, inventoryItem.getStockLevel());
                updateStmt.setInt(2, inventoryItemID);

                int affectedRows = updateStmt.executeUpdate();

                // Check if the update was successful
                if (affectedRows > 0) {
                    System.out.println("Stock level updated successfully.");
                } else {
                    System.out.println("Failed to update stock level.");
                }
            } else {
                System.out.println("InventoryItem not found for the given inventoryID and itemID.");
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Handle SQL exceptions
        }
    }

    public static void deleteItemFromDB(int inventoryID, int itemID) {

        String selectQuery = "SELECT InventoryItem.inventoryItemID " +
                "FROM InventoryItem " +
                "JOIN Inventory_InventoryItem ON InventoryItem.inventoryItemID = Inventory_InventoryItem.inventoryItemID " +
                "WHERE Inventory_InventoryItem.inventoryID = ? AND InventoryItem.itemID = ? " +
                "LIMIT 1";

        String deleteQuery = "DELETE FROM Inventory_InventoryItem WHERE inventoryID = ? AND inventoryItemID = ?";


        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement selectStmt = connection.prepareStatement(selectQuery);
             PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {

            selectStmt.setInt(1, inventoryID);
            selectStmt.setInt(2, itemID);

            ResultSet resultSet = selectStmt.executeQuery();

            if (resultSet.next()) {
                int inventoryItemID = resultSet.getInt("inventoryItemID");

                deleteStmt.setInt(1, inventoryID);
                deleteStmt.setInt(2, inventoryItemID);

                int affectedRows = deleteStmt.executeUpdate();

                if (affectedRows > 0) {
                    System.out.println("Row deleted successfully from Inventory_InventoryItem.");
                } else {
                    System.out.println("Failed to delete the row from Inventory_InventoryItem.");
                }
            } else {
                System.out.println("InventoryItemID not found for the given inventoryID and itemID.");
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Handle SQL exceptions
        }
    }




    // Generate Optimal List



    public static int[] getAllShopsInLocation(String location) {

        String query = "SELECT shopID FROM Shop WHERE location = ?";
        ArrayList<Integer> shopIDs = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            // Set the location parameter
            pstmt.setString(1, location);

            ResultSet resultSet = pstmt.executeQuery();

            // Add each shopID to the ArrayList
            while (resultSet.next()) {
                shopIDs.add(resultSet.getInt("shopID"));
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Handle SQL exceptions
        }

        // Convert ArrayList to int[]
        return shopIDs.stream().mapToInt(i -> i).toArray();
    }

    public static void addUserDetailsToDB(User user) {
        // update budget location and family size of the corresponding userID given

        String updateQuery = "UPDATE User SET budget = ?, location = ?, familySize = ? WHERE ID = ?";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {

            // Set the parameters from the User object
            pstmt.setInt(1, user.getBudget());
            pstmt.setString(2, user.getLocation());
            pstmt.setInt(3, user.getFamilySize());
            pstmt.setInt(4, user.getID());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("User updated successfully.");
            } else {
                System.out.println("No user found with the given ID.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int addOptimalList(OptimalList optimalListClass) {
        // add a row in the optimal list class adding userID, currentDate, categories, and percentages
        // return id of the newly created optimal list entry in the optimal list table in database

        String insertOptimalListQuery = "INSERT INTO OptimalList (userID, date, totalCost) VALUES (?, ?, ?)";
        String insertPercentagesQuery = "INSERT INTO OptimalListPercentages (optimalListID, category, percentage) VALUES (?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {

            // Insert into OptimalList and get the generated ID
            try (PreparedStatement pstmt = connection.prepareStatement(insertOptimalListQuery, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, optimalListClass.getUserID());
                pstmt.setString(2, optimalListClass.getDate());
                pstmt.setDouble(3, optimalListClass.getTotalCost());

                int affectedRows = pstmt.executeUpdate();

                if (affectedRows == 0) {
                    throw new SQLException("Creating optimal list failed, no rows affected.");
                }

                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int optimalListID = generatedKeys.getInt(1);

                        // Insert percentages into OptimalListPercentages
                        try (PreparedStatement pstmtPercentages = connection.prepareStatement(insertPercentagesQuery)) {
                            ArrayList<String> categories = optimalListClass.getCategories();
                            int[] percentages = optimalListClass.getPercentages();

                            for (int i = 0; i < categories.size(); i++) {
                                pstmtPercentages.setInt(1, optimalListID);
                                pstmtPercentages.setString(2, categories.get(i));
                                pstmtPercentages.setInt(3, percentages[i]);
                                pstmtPercentages.addBatch(); // Add to batch
                            }

                            pstmtPercentages.executeBatch(); // Execute the batch
                        }

                        return optimalListID;
                    } else {
                        throw new SQLException("Creating optimal list failed, no ID obtained.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;

    }

    public static void addOptimalListItems(ArrayList<SaleLineItem> optimalList, int optimalListClassID) {
        // adds all optimal list items into the database with corresponding id optimalListClassID

        String insertQuery = "INSERT INTO OptimalSaleLineItem (optimalListID, itemID, quantity, shopID) VALUES (?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {

                for (SaleLineItem saleLineItem : optimalList) {

                    int itemID = saleLineItem.getItem().getItemID();
                    int quantity = saleLineItem.getQuantity();
                    int shopID = saleLineItem.getShop();

                    pstmt.setInt(1, optimalListClassID);
                    pstmt.setInt(2, itemID);
                    pstmt.setInt(3, quantity);
                    pstmt.setInt(4, shopID);

                    pstmt.addBatch();
                }

                pstmt.executeBatch();

                System.out.println("Sale line items inserted successfully.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteOptimalListItemFromDB(int optimalListID, int itemID) {
        String sql = "DELETE FROM OptimalSaleLineItem WHERE optimalListID = ? AND itemID = ?";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, optimalListID);
            statement.setInt(2, itemID);

            int rowsAffected = statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
