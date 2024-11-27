package p.project.Classes;

import p.project.DBHandling.DB;

public class Rider {
    private
    int ID;
    Account account;
    String vehicle;

    public
    Rider(){
        ID = 0;
        account = new Account();
        vehicle = "";
    }

    // Getters
    public int getID() {return ID;}
    public String getVehicle() {return vehicle;}


    // Setters
    public void setID(int ID) {this.ID = ID;}
    public void setVehicle(String vehicle) {this.vehicle = vehicle;}

    public void createRider(Account account){
        this.account = account;
    }

    public boolean logIN(Account account){
        String accountName = account.getName();
        Rider riderDetails = DB.getRiderDetails(accountName);

        if (riderDetails != null) {
            this.ID = riderDetails.getID();
            this.vehicle = riderDetails.getVehicle();
            this.account = account;

            System.out.println("Rider details loaded successfully for account: " + accountName);
            return true;
        } else {
            System.out.println("No rider profile found for the account: " + accountName);
            return false;
        }
    }
}
