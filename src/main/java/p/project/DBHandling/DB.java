package p.project.DBHandling;
import p.project.Classes.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class DB {
    public
    DB(){}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/db1", "root", "123456");
    }

    public static void insertUserAccount(Account account) {
        try (Connection connection = DB.getConnection()) {
            String insertAccountQuery = "INSERT INTO account (name, email, password, phoneNumber, type) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement accountStatement = connection.prepareStatement(insertAccountQuery);
            accountStatement.setString(1, account.getName());
            accountStatement.setString(2, account.getEmail());
            accountStatement.setString(3, account.getPassword());
            accountStatement.setString(4, account.getPhoneNumber());
            accountStatement.setString(5, account.getType());

            int rowsAffected = accountStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Account created successfully!");
                String insertUserQuery = "INSERT INTO user (accountName, preferences, budget, location, familySize) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement userStatement = connection.prepareStatement(insertUserQuery);
                userStatement.setString(1, account.getName());
                userStatement.setString(2, "");
                userStatement.setInt(3, -1);
                userStatement.setString(4, "");
                userStatement.setInt(5, -1);

                int userRowsAffected = userStatement.executeUpdate();
                if (userRowsAffected > 0) {
                    System.out.println("User profile created successfully!");
                } else {
                    System.out.println("Failed to create user profile.");
                }

            } else {
                System.out.println("Failed to create account.");
            }

        } catch (SQLException e) {
            System.out.println("Error inserting data into the database.");
            e.printStackTrace();
        }
    }

    public static void insertShopAccount(Account account) {
        try (Connection connection = DB.getConnection()) {
            connection.setAutoCommit(false);

            try {
                String insertAccountQuery = "INSERT INTO account (name, email, password, phoneNumber, type) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement accountStatement = connection.prepareStatement(insertAccountQuery);
                accountStatement.setString(1, account.getName());
                accountStatement.setString(2, account.getEmail());
                accountStatement.setString(3, account.getPassword());
                accountStatement.setString(4, account.getPhoneNumber());
                accountStatement.setString(5, account.getType());

                int rowsAffected = accountStatement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Account created successfully!");


                    String insertInventoryQuery = "INSERT INTO Inventory () VALUES ()";
                    PreparedStatement inventoryStatement = connection.prepareStatement(insertInventoryQuery, Statement.RETURN_GENERATED_KEYS);
                    inventoryStatement.executeUpdate();

                    ResultSet generatedKeys = inventoryStatement.getGeneratedKeys();
                    int inventoryID;
                    if (generatedKeys.next()) {
                        inventoryID = generatedKeys.getInt(1);

                        String insertShopQuery = "INSERT INTO Shop (name, location, inventoryID, accountName, status) VALUES (?, ?, ?, ?, ?)";
                        PreparedStatement shopStatement = connection.prepareStatement(insertShopQuery);
                        shopStatement.setString(1, account.getName());
                        shopStatement.setString(2, "");
                        shopStatement.setInt(3, inventoryID);
                        shopStatement.setString(4, account.getName());
                        shopStatement.setString(5, "pending");

                        int shopRowsAffected = shopStatement.executeUpdate();

                        if (shopRowsAffected > 0) {
                            System.out.println("Shop profile created successfully!");
                            connection.commit();
                        } else {
                            System.out.println("Failed to create shop profile.");
                            connection.rollback();
                        }
                    } else {
                        System.out.println("Failed to create inventory.");
                        connection.rollback();
                    }
                } else {
                    System.out.println("Failed to create account.");
                    connection.rollback();
                }

            } catch (SQLException e) {
                System.out.println("Error during transaction, rolling back.");
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            System.out.println("Error inserting data into the database.");
            e.printStackTrace();
        }
    }

    public static void insertRiderAccount(Account account) {
        try (Connection connection = DB.getConnection()) {
            String insertAccountQuery = "INSERT INTO account (name, email, password, phoneNumber, type) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement accountStatement = connection.prepareStatement(insertAccountQuery);
            accountStatement.setString(1, account.getName());
            accountStatement.setString(2, account.getEmail());
            accountStatement.setString(3, account.getPassword());
            accountStatement.setString(4, account.getPhoneNumber());
            accountStatement.setString(5, account.getType());

            int rowsAffected = accountStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Account created successfully!");
                String insertRiderQuery = "INSERT INTO Rider (vehicle, accountName) VALUES (?, ?)";
                PreparedStatement riderStatement = connection.prepareStatement(insertRiderQuery,
                        Statement.RETURN_GENERATED_KEYS);
                riderStatement.setString(1, "");
                riderStatement.setString(2, account.getName());

                int riderRowsAffected = riderStatement.executeUpdate();
                if (riderRowsAffected > 0) {
                    ResultSet generatedKeys = riderStatement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        System.out.println("Rider profile created successfully with ID: " + generatedId);
                    }
                } else {
                    System.out.println("Failed to create rider profile.");
                }

            } else {
                System.out.println("Failed to create account.");
            }

        } catch (SQLException e) {
            if (e.getSQLState().equals("23000")) { // SQL state for unique constraint violation
                System.out.println("An account with this name or email already exists.");
            } else {
                System.out.println("Error inserting data into the database.");
                e.printStackTrace();
            }
        }
    }


    public static boolean checkDetails(String password, String email) {
        String query = "SELECT COUNT(*) FROM account WHERE email = ? AND password = ?";
        try (Connection connection = DB.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error while checking details in the database.");
            e.printStackTrace();
        }
        return false;
    }

    public static Account getAccountDetails(String email, String password) {
        String query = "SELECT * FROM account WHERE email = ? AND password = ?";
        try (Connection connection = DB.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Account account = new Account();
                account.setName(resultSet.getString("name"));
                account.setEmail(resultSet.getString("email"));
                account.setPassword(resultSet.getString("password"));
                account.setPhoneNumber(resultSet.getString("phoneNumber"));
                account.setType(resultSet.getString("type"));
                return account;
            }
        } catch (SQLException e) {
            System.out.println("Error while retrieving account details from the database.");
            e.printStackTrace();
        }
        return null;
    }


    public static User getUserDetails(String accountName) {
        String query = "SELECT * FROM user WHERE accountName = ?";
        try (Connection connection = DB.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, accountName);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                User user = new User();
                user.setID(resultSet.getInt("ID"));
                user.setPreferences(resultSet.getString("preferences"));
                user.setBudget(resultSet.getInt("budget"));
                user.setLocation(resultSet.getString("location"));
                user.setFamilySize(resultSet.getInt("familySize"));

                return user;
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving user details from the database.");
            e.printStackTrace();
        }
        return null;
    }

    public static Shop getShopDetails(String accountName) {
        String query = "SELECT * FROM shop WHERE accountName = ?";
        try (Connection connection = DB.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, accountName);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Shop shop = new Shop();
                shop.setShopID(resultSet.getInt("shopID"));
                shop.setName(resultSet.getString("name"));
                shop.setLocation(resultSet.getString("location"));
                shop.setInventory(null);
                shop.setStatus(resultSet.getString("status"));
                return shop;
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving shop details from the database.");
            e.printStackTrace();
        }
        return null;
    }

    public static Rider getRiderDetails(String accountName) {
        String query = "SELECT * FROM rider WHERE accountName = ?";
        try (Connection connection = DB.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, accountName);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Rider rider = new Rider();
                rider.setID(resultSet.getInt("ID"));
                rider.setVehicle(resultSet.getString("vehicle"));
                return rider;
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving rider details from the database.");
            e.printStackTrace();
        }
        return null;
    }

    public static Admin getAdminDetails(String accountName) {
        String query = "SELECT * FROM admin WHERE account_name = ?";
        try (Connection connection = DB.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, accountName);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Admin admin = new Admin();
                admin.setID(resultSet.getInt("id"));
                return admin;
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving rider details from the database.");
            e.printStackTrace();
        }
        return null;
    }


    public static void updateUserProfile(String name, String location, int budget, int familySize) {
        String updateQuery = "UPDATE user SET location = ?, budget = ?, familySize = ? WHERE accountName = ?";
        try (Connection connection = DB.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

            preparedStatement.setString(1, location);
            preparedStatement.setInt(2, budget);
            preparedStatement.setInt(3, familySize);
            preparedStatement.setString(4, name);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User profile updated successfully.");
            } else {
                System.out.println("Failed to update user profile. No user found with the provided name.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating user profile in the database.");
            e.printStackTrace();
        }
    }


    public static void updateUserPreferences(String name, String preferences) {
        String updateQuery = "UPDATE user SET preferences = ? WHERE accountName = ?";
        try (Connection connection = DB.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

            preparedStatement.setString(1, preferences);
            preparedStatement.setString(2, name);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Preferences updated successfully.");
            } else {
                System.out.println("Failed to update preferences. No user found with the provided name.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating preferences in the database.");
            e.printStackTrace();
        }
    }


    public static void changeAccountPassword(String name, String newPassword) {
        String updateQuery = "UPDATE Account SET password = ? WHERE name = ?";
        try (Connection connection = DB.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

            preparedStatement.setString(1, newPassword);
            preparedStatement.setString(2, name);

            System.out.println("Changing password for account: " + name + " to: " + newPassword);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Password changed successfully.");
            } else {
                System.out.println("Failed to change password. No account found with the provided name.");
            }
        } catch (SQLException e) {
            System.out.println("Error changing password in the database.");
            e.printStackTrace();
        }
    }


    public static void deleteAccount(String email) {
        try (Connection connection = DB.getConnection()) {
            String checkAccountQuery = "SELECT name FROM account WHERE email = ?";
            PreparedStatement checkAccountStmt = connection.prepareStatement(checkAccountQuery);
            checkAccountStmt.setString(1, email);

            ResultSet resultSet = checkAccountStmt.executeQuery();
            if (resultSet.next()) {
                String accountName = resultSet.getString("name");
                System.out.println("Found account with name: " + accountName);

                String deleteUserQuery = "DELETE FROM user WHERE accountName = ?";
                PreparedStatement deleteUserStmt = connection.prepareStatement(deleteUserQuery);
                deleteUserStmt.setString(1, accountName);

                int userRowsAffected = deleteUserStmt.executeUpdate();
                if (userRowsAffected > 0) {
                    System.out.println("User profile deleted successfully!");
                } else {
                    System.out.println("No user profile found for the account: " + accountName);
                }

                String deleteAccountQuery = "DELETE FROM account WHERE email = ?";
                PreparedStatement deleteAccountStmt = connection.prepareStatement(deleteAccountQuery);
                deleteAccountStmt.setString(1, email);

                int accountRowsAffected = deleteAccountStmt.executeUpdate();
                if (accountRowsAffected > 0) {
                    System.out.println("Account deleted successfully!");
                } else {
                    System.out.println("No account found with the provided email.");
                }
            } else {
                System.out.println("No account found with the provided email.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting account.");
            e.printStackTrace();
        }
    }

    public static boolean submitFeedback(Feedback feedback) {
        try (Connection connection = DB.getConnection()) {
            int userID = feedback.getUser().getID();
            String insertFeedbackQuery = "INSERT INTO Feedback (userID, userComments, adminComments, type, status, priority) VALUES (?, ?, NULL, NULL, NULL, NULL)";
            PreparedStatement feedbackStatement = connection.prepareStatement(insertFeedbackQuery);
            feedbackStatement.setInt(1, userID);
            feedbackStatement.setString(2, feedback.getUserComments());
            int rowsAffected = feedbackStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Feedback submitted successfully!");
                return true;
            } else {
                System.out.println("Failed to submit feedback.");
                return false;
            }

        } catch (SQLException e) {
            System.out.println("Error submitting feedback.");
            e.printStackTrace();
        }
        return false;
    }

    public static void insertLocation(String location) {
        try (Connection connection = DB.getConnection()) {
            String getMaxIDQuery = "SELECT MAX(shopID) as maxID FROM shop";
            PreparedStatement getMaxStatement = connection.prepareStatement(getMaxIDQuery);
            ResultSet rs = getMaxStatement.executeQuery();

            if (rs.next()) {
                int shopID = rs.getInt("maxID");

                String updateQuery = "UPDATE shop SET location = ? WHERE shopID = ?";
                PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                updateStatement.setString(1, location);
                updateStatement.setInt(2, shopID);

                int rowsAffected = updateStatement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Shop location updated successfully!");
                } else {
                    System.out.println("Failed to update shop location.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error updating shop location.");
            e.printStackTrace();
        }
    }

    public static void updateShopStatus(Account account, String status) {
        Shop shop = getShopDetails(account.getName());

        if (shop == null) {
            System.out.println("Shop not found for the given account.");
            return;
        }

        String shopQuery = "UPDATE shop SET status = ? WHERE shopID = ?";
        String deleteShopQuery = "DELETE FROM shop WHERE shopID = ?";
        String deleteAccountQuery = "DELETE FROM account WHERE name = ?";

        try (Connection connection = DB.getConnection()) {
            if (status.equalsIgnoreCase("approved")) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(shopQuery)) {
                    preparedStatement.setString(1, "approved");
                    preparedStatement.setInt(2, shop.getShopID());
                    int rowsUpdated = preparedStatement.executeUpdate();

                    if (rowsUpdated > 0) {
                        System.out.println("Shop status updated to approved.");
                    } else {
                        System.out.println("Failed to update shop status.");
                    }
                }
            } else if (status.equalsIgnoreCase("denied")) {
                try (PreparedStatement deleteShopStatement = connection.prepareStatement(deleteShopQuery);
                     PreparedStatement deleteAccountStatement = connection.prepareStatement(deleteAccountQuery)) {

                    deleteShopStatement.setInt(1, shop.getShopID());
                    int shopRowsDeleted = deleteShopStatement.executeUpdate();

                    deleteAccountStatement.setString(1, account.getName());
                    int accountRowsDeleted = deleteAccountStatement.executeUpdate();

                    if (shopRowsDeleted > 0 && accountRowsDeleted > 0) {
                        System.out.println("Shop and associated account removed successfully.");
                    } else {
                        System.out.println("Failed to remove shop or account.");
                    }
                }
            } else {
                System.out.println("Invalid status provided. Use 'approved' or 'denied'.");
            }
        } catch (SQLException e) {
            System.out.println("Error while updating shop status.");
            e.printStackTrace();
        }
    }

    public static ArrayList<Shop> getPendingShops() {
        ArrayList<Shop> pendingShops = new ArrayList<>();
        String shopQuery = "SELECT * FROM shop WHERE status = 'pending'";
        String accountQuery = "SELECT * FROM Account WHERE name = ?";

        try (Connection connection = getConnection();
             PreparedStatement shopStatement = connection.prepareStatement(shopQuery)) {

            ResultSet shopResults = shopStatement.executeQuery();

            while (shopResults.next()) {
                Shop shop = new Shop();
                shop.setName(shopResults.getString("name"));
                shop.setShopID(shopResults.getInt("shopID"));
                shop.setLocation(shopResults.getString("location"));
                shop.setStatus(shopResults.getString("status"));

                try (PreparedStatement accountStatement = connection.prepareStatement(accountQuery)) {
                    accountStatement.setString(1, shopResults.getString("accountName"));
                    ResultSet accountResults = accountStatement.executeQuery();

                    if (accountResults.next()) {
                        Account account = new Account();
                        account.setName(accountResults.getString("name"));
                        account.setEmail(accountResults.getString("email"));
                        account.setPassword(accountResults.getString("password"));
                        account.setPhoneNumber(accountResults.getString("phoneNumber"));
                        account.setType(accountResults.getString("type"));
                        shop.setAccount(account);
                    }
                }
                pendingShops.add(shop);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving pending shops: " + e.getMessage());
            e.printStackTrace();
        }
        return pendingShops;
    }

}


