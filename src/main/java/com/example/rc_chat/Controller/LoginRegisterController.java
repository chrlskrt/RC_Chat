package com.example.rc_chat.Controller;

import com.example.rc_chat.Database.DatabaseManager;
import com.example.rc_chat.Database.User;
import com.example.rc_chat.Database.dbStatus;
import com.example.rc_chat.SplashScreen;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class LoginRegisterController {
    @FXML
    public AnchorPane regAnchorPane;
    @FXML
    public BorderPane loginBorderPane;
    public TextField tf_regUsername;
    public PasswordField pf_regPassword;
    public Button btnRegisterUser;
    public TextField tf_logUsername;
    public PasswordField pf_logPassword;
    DatabaseManager dbManager = DatabaseManager.getInstance();
    User current_user = User.getInstance();
    public Alert alert = new Alert(Alert.AlertType.NONE);

    public void backRegOnClick() throws IOException {
        AnchorPane a = regAnchorPane;
        a.getScene().getStylesheets().clear();
        FXMLLoader fxmlLoader = new FXMLLoader(SplashScreen.class.getResource("SplashScreen.fxml"));

        Parent p = fxmlLoader.load();
        a.getChildren().clear();
        a.getChildren().add(p);
    }

    public void backLoginOnClick() throws IOException {
        BorderPane a = loginBorderPane;
        a.getScene().getStylesheets().clear();
        FXMLLoader fxmlLoader = new FXMLLoader(SplashScreen.class.getResource("SplashScreen.fxml"));

        Parent p = fxmlLoader.load();
        a.getChildren().clear();
        a.getChildren().add(p);
    }

    public void btnRegisterUserClick(ActionEvent actionEvent) throws IOException {
        String username = tf_regUsername.getText();
        String password = pf_regPassword.getText();

        if (username.isEmpty() || password.isEmpty()){
            showAlert(Alert.AlertType.ERROR, "Username or password is empty.");
            return;
        }

        dbStatus regRes = dbManager.createUser (username, password);
        if (regRes == dbStatus.REGISTER_USER_EXISTS){
            showAlert(Alert.AlertType.WARNING, "User exists");
            return;
        } else if (regRes == dbStatus.REGISTER_ERROR){
            showAlert(Alert.AlertType.ERROR, "Account creation unsuccessful");
            return;
        }

        goToHomePage(actionEvent);
    }

    public void btnLogInUserClick(ActionEvent actionEvent) throws IOException {
        String username = tf_logUsername.getText();
        String password = pf_logPassword.getText();

        if (username.isEmpty() || password.isEmpty()){
            showAlert(Alert.AlertType.ERROR, "Username or password is empty.");
            return;
        }

        dbStatus logRes = dbManager.logUser(username, password);
        if (logRes == dbStatus.LOGIN_USER_NOT_FOUND){
            showAlert(Alert.AlertType.ERROR, "User not found.");
            return;
        } else if (logRes == dbStatus.LOGIN_ERROR){
            showAlert(Alert.AlertType.WARNING, "Username or password is incorrect.");
            return;
        }

        goToHomePage(actionEvent);
    }

    private void goToHomePage(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SplashScreen.class.getResource("SplashScreen.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 700, 440);
        stage.setScene(scene);
        stage.show();
        stage.setTitle("Home Page");
    }

    void showAlert(Alert.AlertType alertType, String content){
        alert.setAlertType(alertType);
        alert.setContentText(content);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}