package p.project.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import p.project.Classes.Admin;
import p.project.DBHandling.DB;
import p.project.Classes.Shop;

import java.io.IOException;
import java.util.ArrayList;

public class viewApplicationsController {
    @FXML
    private ComboBox<Shop> applicationsComboBox;

    @FXML
    private Label shopNameLabel;

    @FXML
    private Label shopLocationLabel;

    @FXML
    private Button approveButton;

    @FXML
    private Button denyButton;

    private ArrayList<Shop> pendingShops;
    private Admin admin;
   // private Zaviyah zaviyah;

//    public void setZaviyah(Zaviyah zaviyah) {
//        this.zaviyah = zaviyah;
//    }

    @FXML
    public void initialize() {
        pendingShops = DB.getPendingShops();
        applicationsComboBox.setItems(FXCollections.observableArrayList(pendingShops));
        admin = Zaviyah.getInstance().getAdmin();

        applicationsComboBox.setCellFactory(param -> new ListCell<Shop>() {
            @Override
            protected void updateItem(Shop shop, boolean empty) {
                super.updateItem(shop, empty);
                if (empty || shop == null) {
                    setText(null);
                } else {
                    setText(shop.getName());
                }
            }
        });

        applicationsComboBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        updateShopDetails(newValue);
                    }
                }
        );
        approveButton.setDisable(true);
        denyButton.setDisable(true);
    }

    private void updateShopDetails(Shop shop) {
        shopNameLabel.setText("Shop Name: " + shop.getName());
        shopLocationLabel.setText("Location: " + shop.getLocation());

        approveButton.setDisable(false);
        denyButton.setDisable(false);
    }

    @FXML
    protected void onApproveButtonClick() {
        Shop selectedShop = applicationsComboBox.getValue();
        if (selectedShop != null) {
            admin.approveShopApplication(selectedShop);
            showAlert("Success", "Shop application approved successfully!");
            refreshApplications();
        }
    }

    @FXML
    protected void onDenyButtonClick() {
        Shop selectedShop = applicationsComboBox.getValue();
        if (selectedShop != null) {
            admin.denyShopApplication(selectedShop);
            showAlert("Success", "Shop application denied!");
            refreshApplications();
        }
    }

    @FXML
    protected void onBackButtonClick() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/p/project/admin_menu.fxml"));
            Stage stage = (Stage) approveButton.getScene().getWindow();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/p/project/style.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load admin page.");
        }
    }

    private void refreshApplications() {
        pendingShops = DB.getPendingShops();
        applicationsComboBox.setItems(FXCollections.observableArrayList(pendingShops));
        applicationsComboBox.getSelectionModel().clearSelection();
        approveButton.setDisable(true);
        denyButton.setDisable(true);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }
}