package p.project.Classes;
import p.project.DBHandling.DB;

import java.util.ArrayList;


public class Admin {
    private
    int id;
    Account account;
    public
    Admin() {}

    public static void addEscalationResponse(int feedbackID, String comments, String status, String priority) {
        Feedback feedback = Feedback.getFeedback(feedbackID);

        feedback.setAdminComments(comments);
        feedback.setStatus(status);
        feedback.setPriority(priority);
        Feedback.updateFeedback(feedback);
    }

    public static void addRejectionResponse(int feedbackID, String comments, String status) {
        Feedback feedback = Feedback.getFeedback(feedbackID);

        feedback.setAdminComments(comments);
        feedback.setStatus(status);
        Feedback.updateFeedback(feedback);
    }

    // Getters
    public int getID() { return id; }
    public Account getAccount() { return account; }

    public
    // Setters
    void setID(int id) { this.id = id; }
    public void setAccount(Account account) { this.account = account; }

    public boolean logIn(Account account){
        String accountName = account.getName();
        Admin adminDetails = DB.getAdminDetails(accountName);
        if (adminDetails != null) {
            this.id = adminDetails.getID();
            this.account = account;
            System.out.println("Admin " + accountName + " has logged in!");
            return true;
        } else {
            System.out.println("No admin profile found for the account: " + accountName);
            return false;
        }
    }

    public void reviewShopApplication(){
         ArrayList<Shop> pendingShops;
         pendingShops = DB.getPendingShops();
            if (pendingShops.isEmpty()) {
                System.out.println("No pending shop applications to review.");
                return;
            }
            for (Shop shop : pendingShops) {
                System.out.println("\nShop Application Details:");
                System.out.println("Shop ID: " + shop.getShopID());
                System.out.println("Name: " + shop.getName());
                System.out.println("Location: " + shop.getLocation());
                System.out.println("Status: " + shop.getStatus());
            }

    }
    public boolean approveShopApplication(Shop shop){
        shop.updateStatus("approved");
        return true;
    }

    public boolean denyShopApplication(Shop shop){
        shop.updateStatus("denied");
        return true;
    }

}
