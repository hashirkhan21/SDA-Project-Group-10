package p.project.Classes;
import p.project.DBHandling.DB;
import p.project.DBHandling.DBHandler;

import java.util.Scanner;
public class User {

    private
    int ID;
    String preferences;
    int budget;
    String location;
    int familySize;
    Account account;

    public User() {
        ID = 0;
        preferences = "";
        budget = -1;
        location = "";
        familySize = -1;
        account = new Account();
    }

    //ABBAS
    public User(int userID, int budget, String location, int familySize) {
        this.ID = userID;
        this.budget = budget;
        this.location = location;
        this.familySize = familySize;
    }
    public static void addUserDetails(User user) {
        DBHandler.addUserDetailsToDB(user);
    }


    // Getters
    public int getID() {return ID;}
    public int getID1() { return ID;}
    public String getPreferences() {return preferences;}
    public int getBudget() {return budget;}
    public String getLocation() {return location;}
    public int getFamilySize() {return familySize;}
    public Account getAccount(){return account;}



    // Setters
    public void setID(int ID) {this.ID = ID;}
    public void setPreferences(String preferences) {this.preferences = preferences;}
    public void setBudget(int budget) {this.budget = budget;}
    public void setLocation(String location) {this.location = location;}
    public void setFamilySize(int familySize) {this.familySize = familySize;}
    public void setAccount(Account account){this.account = account;}
    //Functions that ACTUALLY DO WORK
    public void createUser(Account account) {
        this.account = account;
    }

    public boolean logIN(Account account) {
        String accountName = account.getName();
        User userDetails = DB.getUserDetails(accountName);
        if (userDetails != null) {
            this.ID = userDetails.getID();
            this.preferences = userDetails.getPreferences();
            this.budget = userDetails.getBudget();
            this.location = userDetails.getLocation();
            this.familySize = userDetails.getFamilySize();
            this.account = account;

            System.out.println("User details loaded successfully for account: " + accountName);
            return true;
        } else {
            System.out.println("No user profile found for the account: " + accountName);
            return false;
        }
    }

    public void editProfile(){


    }

    public void manageAccount() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("User Information:");
        System.out.println("ID: " + ID);
        System.out.println("Name: " + account.getName());
        System.out.println("Email: " + account.getEmail());
        System.out.println("Preferences: " + preferences);
        System.out.println("Budget: " + budget);
        System.out.println("Location: " + location);
        System.out.println("Family Size: " + familySize);


        System.out.println("\nChoose an option:");
        System.out.println("1: Edit Profile");
        System.out.println("2: Change Password");
        System.out.println("3: Update Preferences");
        System.out.println("4: Delete Account");

        String choice = scanner.nextLine();

        switch (choice) {
            case "1":
                System.out.println("Enter new location:");
                String newLocation = scanner.nextLine();
                setLocation(newLocation);
                System.out.println("Enter new budget:");
                int newBudget = scanner.nextInt();
                setBudget(newBudget);
                scanner.nextLine();
                System.out.println("Enter new family size:");
                int newFamilySize = scanner.nextInt();
                setFamilySize(newFamilySize);
                scanner.nextLine();
                DB.updateUserProfile(account.getName(), newLocation, newBudget, newFamilySize);
                System.out.println("Profile updated successfully.");
                break;

            case "2":
                System.out.println("Enter new password:");
                String newPassword = scanner.nextLine();
                account.setPassword(newPassword);
                DB.changeAccountPassword(account.getName(), newPassword);
                System.out.println("Password updated successfully.");
                break;

            case "3":
                System.out.println("Enter new preferences:");
                String newPreferences = scanner.nextLine();
                setPreferences(newPreferences);
                DB.updateUserPreferences(account.getName(), newPreferences);
                System.out.println("Preferences updated successfully.");
                break;

            case "4":
                System.out.println("Are you sure you want to delete your account? (yes/no)");
                String confirmation = scanner.nextLine();
                if (confirmation.equalsIgnoreCase("yes")) {
                    System.out.println("Account deleted.");
                    DB.deleteAccount(account.getEmail());
                    account = null;
                } else {
                    System.out.println("Account deletion canceled.");
                }
                break;

            default:
                System.out.println("Invalid option. Please type any of the options listed above.");
                break;
        }
    }

}
