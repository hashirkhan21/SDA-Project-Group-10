package p.project.Classes;

import p.project.DBHandling.DB;
import p.project.DBHandling.DBHandler;

import java.util.ArrayList;

public class Feedback {
    private

    int feedbackID;
    User user;
    String userComments;
    String adminComments;
    String type;
    String status;
    String priority;
    int userID;

    public Feedback() {
    }

    //ABBAS
    public Feedback(int feedbackID, int userID, String userComments, String adminComments, String type, String status, String priority) {
        this.feedbackID = feedbackID;
        this.userID = userID;
        this.userComments = userComments;
        this.adminComments = adminComments;
        this.type = type;
        this.status = status;
        this.priority = priority;
    }


    public static ArrayList<Feedback> getAllFeedbacks() {
        return DBHandler.getAllFeedbacksFromDB();
    }

    public static Feedback getFeedback(int feedbackID) {
        return DBHandler.getFeedbackFromDB(feedbackID);
    }

    public static void updateFeedback(Feedback feedback) {
        DBHandler.updateFeedbackToDB(feedback);
    }

    // Getters
    public int getFeedbackID() {
        return feedbackID;
    }
    public User getUser() {
        return user;
    }
    public int getUserID() { return userID; }
    public String getUserComments() {
        return userComments;
    }
    public String getAdminComments() {
        return adminComments;
    }
    public String getType() {
        return type;
    }
    public String getStatus() {
        return status;
    }
    public String getPriority() {
        return priority;
    }

    // Setters
    public void setFeedbackID(int feedbackID) {
        this.feedbackID = feedbackID;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public void setUserComments(String userComments) {
        this.userComments = userComments;
    }
    public void setAdminComments(String adminComments) {
        this.adminComments = adminComments;
    }
    public void setType(String type) {
        this.type = type;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public void setPriority(String priority) {
        this.priority = priority;
    }
    public void setUserID(int userID) { this.userID = userID; }
    // Functions that DO ACTUAL WORK

    public void submitFeedback(String comments){
        setUserComments(comments);
        if (DB.submitFeedback(this)){
            System.out.println("Feedback submitted by " + user.getAccount().getName());
        }
        else
            System.out.println("Feedback could not be submitted.");
    }
}
