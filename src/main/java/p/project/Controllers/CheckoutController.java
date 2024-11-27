package p.project.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class CheckoutController {

    @FXML
    private TextField addressField;

    @FXML
    private TextField phoneNumberField; // New TextField for phone number

    @FXML
    private RadioButton codRadioButton;

    @FXML
    private RadioButton onlinePaymentRadioButton;

    @FXML
    private VBox cardDetailsBox;

    @FXML
    private TextField cardNumberField;

    @FXML
    private TextField expiryDateField;

    @FXML
    private PasswordField cvvField;

    private MainController mainController;

    @FXML
    public void initialize() {
        // Create a ToggleGroup for the radio buttons
        ToggleGroup paymentToggleGroup = new ToggleGroup();
        codRadioButton.setToggleGroup(paymentToggleGroup);
        onlinePaymentRadioButton.setToggleGroup(paymentToggleGroup);

        // Add listeners to handle visibility of card details based on the selected payment method
        codRadioButton.setOnAction(event -> cardDetailsBox.setVisible(false));
        onlinePaymentRadioButton.setOnAction(event -> cardDetailsBox.setVisible(true));

        mainController = MainController.getInstance();
    }


    @FXML
    private void confirmOrder() throws IOException {
        String address = addressField.getText();
        String phoneNumber = phoneNumberField.getText(); // Get phone number

        if (address.isEmpty()) {
            showError("Please enter a delivery address.");
            return;
        }

        if (phoneNumber.isEmpty()) {
            showError("Please enter a phone number.");
            return;
        }

        String paymentMethod;
        if (codRadioButton.isSelected()) {
            paymentMethod = "Cash on Delivery";
        } else if (onlinePaymentRadioButton.isSelected()) {
            paymentMethod = "Online Payment";
            if (cardNumberField.getText().isEmpty() || expiryDateField.getText().isEmpty() || cvvField.getText().isEmpty()) {
                showError("Please fill in all card details for online payment.");
                return;
            }
        } else {
            showError("Please select a payment method.");
            return;
        }

        // Set payment method and address in MainController
        mainController.setPaymentMethod(paymentMethod);
        mainController.setOrderDetails("Delivery Address: " + address + ", Phone Number: " + phoneNumber);

        // Insert the order into the DB via MainController
        int userID = mainController.getUserID();
        int customListID = mainController.customListID;
        double totalAmount = mainController.calculateTotalAmount(userID,customListID);

        if (totalAmount == 0) {
            // Generate a random number between 1100 and 9900, in multiples of 100
            totalAmount = (int) (Math.random() * 90 + 11) * 100;
        }

        try {
            mainController.insertOrderIntoDB(userID, customListID, address, phoneNumber, paymentMethod, totalAmount);
            showSuccess("Order confirmed. Status is pending until a rider picks it up.");

            mainController.customListID = 0;
            mainController.clearCart();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to confirm the order. Please try again.");
        }

        goToMenu();
    }

    private void goToMenu() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/p/project/main_menu.fxml")); // Load the main menu FXML
        Parent root = loader.load();
        // Get the stage from the current scene
        Scene scene = new Scene(root,500,500);
        scene.getStylesheets().add(getClass().getResource("/p/project/style.css").toExternalForm());
        Stage stage = (Stage) addressField.getScene().getWindow(); // Get the current window from the addressField
        stage.setScene(scene);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
