package p.project.Controllers;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import p.project.Classes.Feedback;
import p.project.Classes.Main;

import java.io.IOException;
import java.util.ArrayList;

public class FeedbackController {

    @FXML
    private TableView<Feedback> feedbackTable;

    @FXML
    private TableColumn<Feedback, Integer> userIDColumn;

    @FXML
    private TableColumn<Feedback, String> userCommentsColumn;

    @FXML
    private TableColumn<Feedback, Void> actionsColumn;

    @FXML
    private Button homeButton;

    // ObservableList to hold the feedback data
    private ObservableList<Feedback> feedbackItems;

    public void initialize() {
        // Fetch feedback from Abbas_Controller
        ArrayList<Feedback> feedbackList = Abbas_Controller.handleFeedback();

        // Set up the table columns using getter methods
        userIDColumn.setCellValueFactory(cellData -> {
            Feedback feedback = cellData.getValue();
            return feedback != null ? new ReadOnlyObjectWrapper<>(feedback.getUserID()) : null;
        });

        userCommentsColumn.setCellValueFactory(cellData -> {
            Feedback feedback = cellData.getValue();
            return feedback != null ? new ReadOnlyObjectWrapper<>(feedback.getUserComments()) : null;
        });

        // Set up the actions column with only the "Respond" button
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button respondButton = new Button("Respond");

            {
                // Set action for Respond button
                respondButton.setOnAction(event -> {
                    Feedback feedback = getTableRow().getItem();
                    if (feedback != null) {
                        onRespond(event, feedback);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox actions = new HBox(10, respondButton);
                    setGraphic(actions);
                }
            }
        });

        // Load data into the table
        feedbackItems = FXCollections.observableArrayList(feedbackList);
        feedbackTable.setItems(feedbackItems);

        // Add action to the Home button
        homeButton.setOnAction(event -> onHome());
    }

    private void onRespond(ActionEvent event, Feedback feedback) {
        // Logic to respond to the feedback
        System.out.println("Responding to feedback from user " + feedback.getUserID() + ": " + feedback.getUserComments());

        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/p/project/FeedbackResponseView.fxml"));
            Parent root = loader.load();

            FeedbackResponseController controller = loader.getController();
            controller.initialize(feedback);

            // Get the current stage using the button that triggered the event
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the new scene and show it
            Scene scene = new Scene(root, 800, 600);
            currentStage.setScene(scene);
            currentStage.setTitle("Respond To Feedback");
            currentStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onHome() {
        try {
            // Load the admin_menu.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/p/project/admin_menu.fxml"));
            Parent root = loader.load();

            // Get the current stage from the button that triggered the event
            Stage currentStage = (Stage) homeButton.getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/p/project/style.css").toExternalForm());
            currentStage.setScene(scene);
            currentStage.setTitle("Admin Menu");
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
