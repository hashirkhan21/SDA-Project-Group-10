package p.project.Controllers;

import p.project.Classes.*;
import p.project.DBHandling.DB;

public class Zaviyah {

    private static Zaviyah instance = null;
    private Account account;
    private User user;
    private Shop shop;
    private Admin admin;
    private Rider rider;
    private Feedback feedback;

    private Zaviyah() {
        account = new Account();
        user = new User();
        shop = new Shop();
        admin = new Admin();
        rider = new Rider();
        feedback = new Feedback();
    }

    public static Zaviyah getInstance() {
        if (instance == null) {
            instance = new Zaviyah();
        }
        return instance;
    }

    public Account getAccount() { return account; }
    public User getUser() { return user; }
    public Shop getShop() { return shop; }
    public Admin getAdmin() { return admin; }
    public Rider getRider() { return rider; }
    public Feedback getFeedback() { return feedback; }

    public boolean logIN(Account A) {
        if (account.logIN(A.getEmail(), A.getPassword())) {
            if (account.getType().equalsIgnoreCase("shop")) {
                return shop.logIN(account);
            }
            else if(account.getType().equalsIgnoreCase("user")) {
                return user.logIN(account);
            }
            else if(account.getType().equalsIgnoreCase("admin")) {
                admin = DB.getAdminDetails(account.getName());
                return admin.logIn(account);
            }
            else if(account.getType().equalsIgnoreCase("rider")) {
                rider = DB.getRiderDetails(account.getName());
                return rider.logIN(account);
            }
        }
        return true;
    }

    public Boolean createAccount(String name, String email, String password, String phoneNumber, String type) {
        account.createAccount(name, email, password, phoneNumber, type);
        return true;
    }

    public void manageAccount() {
        feedback.setUser(user);
        System.out.println("In manage account function");
        System.out.println(feedback.getUser().getAccount().getName());
        user.manageAccount();
    }

    public void giveFeedback(String comments) {
        System.out.println("In give feedback function");
        System.out.println(feedback.getUser().getAccount().getName());
        feedback.submitFeedback(comments);
    }

    public void reviewShopApplication() {
        admin.reviewShopApplication();
    }
}