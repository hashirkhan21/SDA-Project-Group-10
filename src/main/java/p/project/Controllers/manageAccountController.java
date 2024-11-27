package p.project.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import p.project.DBHandling.DB;
import p.project.Classes.User;

import java.io.IOException;

public class manageAccountController {
    private User currentUser;
    //private Zaviyah zaviyah;
    @FXML
    private Label userInfoLabel;
    @FXML
    private Button editProfileButton;
    @FXML
    private Button changePasswordButton;
    @FXML
    private Button updatePreferencesButton;
    @FXML
    private Button deleteAccountButton;
    @FXML
    private Button backButton;
    @FXML
    private Button feedbackButton;

    public
    manageAccountController(){
        currentUser = new User();
    }
//    public void setZaviyah(Zaviyah zaviyah) {
//        this.zaviyah = zaviyah;
//        currentUser = zaviyah.user;
//
//    }

    public void initialize() {
        currentUser = Zaviyah.getInstance().getUser();
        displayUserInfo();
    }

    public void displayUserInfo() {
        String userInfo = String.format("""
            User Information:
            ID: %s
            Name: %s
            Email: %s
            Preferences: %s
            Budget: %d
            Location: %s
            Family Size: %d
            """,
                currentUser.getID(),
                currentUser.getAccount().getName(),
                currentUser.getAccount().getEmail(),
                currentUser.getPreferences(),
                currentUser.getBudget(),
                currentUser.getLocation(),
                currentUser.getFamilySize()
        );
        userInfoLabel.setText(userInfo);
    }

    @FXML
    private void handleEditProfile() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Profile");
        dialog.setHeaderText("Update your profile information");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField locationField = new TextField(currentUser.getLocation());
        TextField budgetField = new TextField(String.valueOf(currentUser.getBudget()));
        TextField familySizeField = new TextField(String.valueOf(currentUser.getFamilySize()));

        grid.add(new Label("Location:"), 0, 0);
        grid.add(locationField, 1, 0);
        grid.add(new Label("Budget:"), 0, 1);
        grid.add(budgetField, 1, 1);
        grid.add(new Label("Family Size:"), 0, 2);
        grid.add(familySizeField, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    String newLocation = locationField.getText();
                    int newBudget = Integer.parseInt(budgetField.getText());
                    int newFamilySize = Integer.parseInt(familySizeField.getText());

                    currentUser.setLocation(newLocation);
                    currentUser.setBudget(newBudget);
                    currentUser.setFamilySize(newFamilySize);

                    DB.updateUserProfile(currentUser.getAccount().getName(), newLocation, newBudget, newFamilySize);
                    showAlert("Success", "Profile updated successfully.");
                    displayUserInfo();
                } catch (NumberFormatException e) {
                    showAlert("Error", "Please enter valid numbers for budget and family size.");
                }
            }
        });
    }

    @FXML
    private void handleChangePassword() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Change Password");
        dialog.setHeaderText("Enter your new password");

        PasswordField passwordField = new PasswordField();
        dialog.getDialogPane().setContent(new VBox(10, new Label("New Password:"), passwordField));
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK && !passwordField.getText().isEmpty()) {
                String newPassword = passwordField.getText();
                currentUser.getAccount().setPassword(newPassword);
                DB.changeAccountPassword(currentUser.getAccount().getName(), newPassword);
                showAlert("Success", "Password updated successfully.");
            }
        });
    }

    @FXML
    private void handleUpdatePreferences() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Update Preferences");
        dialog.setHeaderText("Enter your new preferences");

        TextField preferencesField = new TextField(currentUser.getPreferences());
        dialog.getDialogPane().setContent(new VBox(10, new Label("New Preferences:"), preferencesField));
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String newPreferences = preferencesField.getText();
                currentUser.setPreferences(newPreferences);
                DB.updateUserPreferences(currentUser.getAccount().getName(), newPreferences);
                showAlert("Success", "Preferences updated successfully.");
                displayUserInfo();
            }
        });
    }

    @FXML
    private void handleDeleteAccount() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Account");
        alert.setHeaderText("Are you sure you want to delete your account?");
        alert.setContentText("This action cannot be undone.");

        if (alert.showAndWait().get() == ButtonType.OK) {
            DB.deleteAccount(currentUser.getAccount().getName());
            currentUser.setAccount(null);
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/p/project/login-view.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) deleteAccountButton.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error", "Could not return to login screen.");
            }
        }
    }

    @FXML
    private void handleFeedback() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Submit Feedback");
        dialog.setHeaderText("Submit your feedback");


        TextArea feedbackArea = new TextArea();
        feedbackArea.setPromptText("Enter your feedback here");
        feedbackArea.setPrefRowCount(5);
        feedbackArea.setPrefColumnCount(30);
        feedbackArea.setWrapText(true);


        VBox content = new VBox(10);
        content.getChildren().addAll(new Label("Your Feedback:"), feedbackArea);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setStyle("-fx-background-color: lightblue;");

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK && !feedbackArea.getText().trim().isEmpty()) {
                String feedbackText = feedbackArea.getText().trim();

                Zaviyah.getInstance().giveFeedback(feedbackText);

                showAlert("Success", "Thank you for your feedback!");
            }
        });
    }


    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/p/project/main_menu.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 500, 500);
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Main Menu");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not return to Main Menu.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}