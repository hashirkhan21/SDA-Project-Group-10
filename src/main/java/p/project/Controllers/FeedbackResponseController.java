package p.project.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import p.project.Classes.Feedback;

public class FeedbackResponseController {

    @FXML private TextField adminCommentsField;
    @FXML private TextField statusField;
    @FXML private TextField priorityField;
    @FXML private Button homeButton;
    @FXML private Button submitButton;

    private static Feedback feedback;

    public void initialize(Feedback feedback) {
        this.feedback = feedback;
    }

    // Method to handle the Home button click
    @FXML
    private void handleHomeButtonClick(ActionEvent event) {
        // Logic to navigate to Home page
        System.out.println("Navigating to Home...");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/p/project/FeedbackView.fxml"));
            Parent root = loader.load();

            FeedbackController controller = loader.getController();
            controller.initialize();

            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 800, 600);

            currentStage.setScene(scene);
            currentStage.setTitle("Feedback Management");
            currentStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to handle the Submit button click
    @FXML
    private void handleSubmitButtonClick() {
        String adminComments = adminCommentsField.getText();
        String status = statusField.getText();
        String priority = priorityField.getText();

        // You can now process these inputs and store them in the backend as needed
        System.out.println("Admin Comments: " + adminComments);
        System.out.println("Status: " + status);
        System.out.println("Priority: " + priority);

        if(priority.isEmpty()) {
            System.out.println("Rejected FeedbackID: " + feedback.getFeedbackID());
            // No priority input so feedback rejected
            Abbas_Controller.rejectFeedback(feedback.getFeedbackID(), adminComments, status);
        } else {
            System.out.println("Accepted FeedbackID: " + feedback.getFeedbackID());
            Abbas_Controller.acceptFeedback(feedback.getFeedbackID(), adminComments, status, priority);
        }
    }
}
