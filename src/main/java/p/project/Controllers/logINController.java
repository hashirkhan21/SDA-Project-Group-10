package p.project.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import java.io.IOException;

import p.project.Classes.Account;
import p.project.DBHandling.DB;

public class logINController {
    //private Zaviyah zaviyah;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Hyperlink signUpLink;

//    public void setZaviyah(Zaviyah zaviyah) {
//        this.zaviyah = zaviyah;
//    }

    @FXML
    protected void onLogInButtonClick() {
        String email = emailField.getText();
        String password = passwordField.getText();

        System.out.println("Login attempt with email: " + email);

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please fill out both email and password.");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showAlert("Error", "Please enter a valid email address.");
            return;
        }

        System.out.println("Attempting to get account details...");
        Account account = DB.getAccountDetails(email, password);

        if (account != null) {
            System.out.println("Account found with type: " + account.getType());
            if (Zaviyah.getInstance().logIN(account)) {
                showAlert("Success", "Login successful!");
                if (account.getType().equalsIgnoreCase("USER")) {
                    try {
                        Abbas_Controller.userID = Zaviyah.getInstance().getUser().getID1();
                        Zaviyah.getInstance().getFeedback().setUser(Zaviyah.getInstance().getUser());
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/p/project/main_menu.fxml"));
                        Parent root = loader.load();
                        Stage stage = (Stage) emailField.getScene().getWindow();
                        stage.setScene(new Scene(root, 500, 500));
                        stage.setTitle("Main Menu");
                        stage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        showAlert("Error", "Could not load manage account page.");
                    }
                }
                else if (account.getType().equalsIgnoreCase("ADMIN")) {
                    try {
                        FXMLLoader loader;
                        Parent root;
                        Abbas_Controller.adminID = Zaviyah.getInstance().getAdmin().getID();
                        Zaviyah.getInstance().getFeedback().setUser(Zaviyah.getInstance().getUser());
                        Zaviyah.getInstance().getAdmin().setAccount(Zaviyah.getInstance().getAccount());
                        Zaviyah.getInstance().reviewShopApplication();
                        loader = new FXMLLoader(getClass().getResource("/p/project/admin_menu.fxml"));
                        root = loader.load();
                        Stage stage = (Stage) emailField.getScene().getWindow();
                        Scene scene = new Scene(root);
                        scene.getStylesheets().add(getClass().getResource("/p/project/style.css").toExternalForm());
                        stage.setScene(scene);
                        stage.setTitle("Admin Menu");
                        stage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        showAlert("Error", "Could not load shop applications review page.");
                    }
                }
                else if (account.getType().equalsIgnoreCase("RIDER")) {
                    try {
                        MainController.getInstance().riderID =  Zaviyah.getInstance().getRider().getID();
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/p/project/rider_main_menu.fxml"));
                        Parent root = loader.load();
                        Stage stage = (Stage) emailField.getScene().getWindow();
                        Scene scene = new Scene(root, 600, 400); // Set appropriate dimensions
                        scene.getStylesheets().add(getClass().getResource("/p/project/style.css").toExternalForm());
                        stage.setScene(scene);
                        stage.setTitle("Rider Main Menu");
                        stage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        showAlert("Error", "Could not load Rider Main Menu.");
                    }
                }
                else if (account.getType().equalsIgnoreCase("SHOP")) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/p/project/shop_main_menu.fxml"));
                        Parent root = loader.load();
                        Abbas_Controller.shopID = Zaviyah.getInstance().getShop().getShopID();
                        Stage stage = (Stage) emailField.getScene().getWindow();
                        Scene scene = new Scene(root, 600, 400); // Set appropriate dimensions
                        scene.getStylesheets().add(getClass().getResource("/p/project/style.css").toExternalForm());
                        stage.setScene(scene);
                        stage.setTitle("Rider Main Menu");
                        stage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        showAlert("Error", "Could not load Rider Main Menu.");
                    }
                }
                else {
                    Stage stage = (Stage) emailField.getScene().getWindow();
                    stage.close();
                }
            } else {
                System.out.println("zaviyah.logIN(account) returned false");
            }

        }  else {
            System.out.println("No account found");
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Login Failed");
            alert.setHeaderText("Invalid email or password.");
            alert.setContentText("Would you like to create an account?");

            if (alert.showAndWait().get().getButtonData().isDefaultButton()) {
                switchToSignUp();
            }
        }
    }

    private void switchToSignUp() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/p/project/register-view.fxml"));
            Parent root = loader.load();

            registerController controller = loader.getController();
            //controller.setZaviyah(this.zaviyah);

            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root, 500, 500));
            stage.setTitle("Sign Up");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load sign up page.");
        }
    }

    @FXML
    protected void onSignUpLinkClick() {
        switchToSignUp();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void switchToMainView() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/p/project/main-view.fxml"));
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
            stage.setTitle("Blawg Main Page");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load main page.");
        }
    }

    @FXML
    public void initialize() {

    }
}