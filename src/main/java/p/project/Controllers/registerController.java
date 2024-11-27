package p.project.Controllers;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.layout.GridPane;
import java.io.IOException;
import javafx.collections.FXCollections;

public class registerController {
    //private Zaviyah zaviyah;

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField phoneNumberField;

    @FXML
    private Hyperlink loginLink;

    @FXML
    private ComboBox<String> accountTypeComboBox;

//    public void setZaviyah(Zaviyah zaviyah) {
//        this.zaviyah = zaviyah;
//    }

    private void showLocationDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Shop Location");
        dialog.setHeaderText("Enter your shop's location");

        TextField locationField = new TextField();
        locationField.setPromptText("Enter location");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Location:"), 0, 0);
        grid.add(locationField, 1, 0);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDefaultButton(true);

        okButton.setDisable(true);
        locationField.textProperty().addListener((observable, oldValue, newValue) -> {
            okButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String location = locationField.getText().trim();
                Zaviyah.getInstance().getShop().setLocation(location);
            }
        });
    }


    @FXML
    protected void onRegisterButtonClick() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String phoneNumber = phoneNumberField.getText().trim();
        String accountType = accountTypeComboBox.getValue().toUpperCase();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || phoneNumber.isEmpty() || accountType == null) {
            showAlert("Error", "Please fill out all fields.");
            return;
        }


        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showAlert("Error", "Please enter a valid email address.");
            return;
        }

        if (password.length() < 6) {
            showAlert("Error", "Password must be at least 6 characters long.");
            return;
        }

        try {
            boolean success = false;
            switch (accountType) {
                case "USER":
                    success = Zaviyah.getInstance().createAccount(name, email, password, phoneNumber, "USER");
                    break;
                case "RIDER":
                    success = Zaviyah.getInstance().createAccount(name, email, password, phoneNumber, "RIDER");
                    break;
                case "SHOP":
                    success = Zaviyah.getInstance().createAccount(name, email, password, phoneNumber, "SHOP");
                    if (success) {
                        showLocationDialog();
                    }
                    break;
            }
            if (success) {
                showAlert("Success", accountType + " account created successfully!");
                switchToLogin();
            } else {
                showAlert("Error", "Failed to create account. Email might already be registered.");
            }
        } catch (Exception e) {
            showAlert("Error", "An error occurred during registration: " + e.getMessage());
        }
    }

    @FXML
    protected void onLoginLinkClick() {
        switchToLogin();
    }

    private void switchToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/p/project/logIn-view.fxml"));
            Parent root = loader.load();

            logINController loginController = loader.getController();
            //loginController.setZaviyah(this.zaviyah);

            Stage stage = (Stage) loginLink.getScene().getWindow();
            stage.setScene(new Scene(root, 400, 300));
            stage.setTitle("Log In");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load login page.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void initialize() {
        accountTypeComboBox.setItems(FXCollections.observableArrayList(
                "User", "Rider", "Shop"
        ));
        accountTypeComboBox.setValue("User");
    }
}