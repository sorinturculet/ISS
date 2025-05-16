package ro.iss.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ro.iss.domain.User;
import ro.iss.service.LoginService;

public class LoginViewController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label statusLabel;

    private final LoginService loginService = new LoginService();

    @FXML
    public void initialize() {
        loginButton.setOnAction(this::handleLogin);
    }

    private void handleLogin(ActionEvent event) {

        System.out.println(usernameField.getText());
        System.out.println(passwordField.getText());
        User user = loginService.login(usernameField.getText(), passwordField.getText());
        System.out.println(user);
        if (user != null) {
            loadUserView(user);
            ((Stage) loginButton.getScene().getWindow()).close();
        } else {
            statusLabel.setText("Invalid username or password!");
        }
    }

    private void loadUserView(User user) {
        try {
            FXMLLoader loader;
            Stage stage = new Stage();

            if ("SECTION".equalsIgnoreCase(user.getType())) {
                loader = new FXMLLoader(getClass().getResource("/section_view.fxml"));
            } else if ("PHARMACIST".equalsIgnoreCase(user.getType())) {
                loader = new FXMLLoader(getClass().getResource("/pharmacy_view.fxml"));
            } else {
                statusLabel.setText("Unknown user type: " + user.getType());
                return;
            }

            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle("Welcome " + user.getUsername());
            stage.show();

            if ("SECTION".equalsIgnoreCase(user.getType())) {
                HospitalStaffController controller = loader.getController();
                controller.setCurrentUser(user);
            } else if ("PHARMACIST".equalsIgnoreCase(user.getType())) {
                PharmacyViewController controller = loader.getController();
                controller.setCurrentUser(user);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
