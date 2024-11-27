package p.project.Classes;

import p.project.DBHandling.DB;

public class Account {
    private
    int accountID;
    String name;
    String email;
    String password;
    String phoneNumber;
    String type;

    public
    Account() {}

    // updateProfile(profileInfo)
    public void updatePassword(String password) {}
    // updatePreferences(preferences)
    public boolean confirmDeletion() {return false;}

    // Getters
    public  String getName() { return name; }
    public String getType() { return type; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getPhoneNumber() { return phoneNumber; }

    // Setters
    public  void setName(String name) { this.name = name; }
    public void setType(String type) { this.type = type; }
    public void setEmail(String email) { this.email = email; }
    public  void setPassword(String password) { this.password = password; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    //Function that ACTUALLY DO WORK
    public  void createAccount(String name, String email, String password, String number, String accountType) {
        setName(name);
        setEmail(email);
        setPassword(password);
        setPhoneNumber(number);
        setType(accountType);

        if(DB.checkDetails(password, email)) {
            System.out.println("Account already exists with this email");
            return;
        }
        if (accountType.equals("USER")) {
            DB.insertUserAccount(this);
        } else if (accountType.equals("SHOP")) {
            DB.insertShopAccount(this);
        }else if (accountType.equals("RIDER")) {
            DB.insertRiderAccount(this);
        }
    }

    public  boolean logIN(String email, String password) {
        Account accountDetails = DB.getAccountDetails(email, password);
        if (accountDetails != null) {
            this.name = accountDetails.name;
            this.email = accountDetails.email;
            this.password = accountDetails.password;
            this.phoneNumber = accountDetails.phoneNumber;
            this.type = accountDetails.type;
            return true;
        }
        return false;
    }

}
