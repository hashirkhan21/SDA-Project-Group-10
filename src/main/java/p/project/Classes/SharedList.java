package p.project.Classes;

public class SharedList {

    private int senderID;
    private int receiverID;

    public  SharedList() {}

    // Getters
    public int getSenderID() { return senderID; }
    public void setReceiverID(int receiverID) {this.receiverID = receiverID;}

    // Setters
    public void setSenderID(int senderID) {this.senderID = senderID;}
    public int getReceiverID() {return receiverID;}

}
