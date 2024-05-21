package com.example.rc_chat;

import com.example.rc_chat.Controller.LoginRegisterController;
import com.example.rc_chat.Database.DatabaseManager;
import com.example.rc_chat.Database.SQLConnection;
import com.example.rc_chat.Database.dbStatus;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class SplashScreen extends Application {
    public AnchorPane splashAnchor;
    public TextField tf_logUsername;
    public PasswordField pf_logPassword;
    public LoginRegisterController logregcon;
    public Alert alert = new Alert(Alert.AlertType.NONE);
    DatabaseManager dbManager = DatabaseManager.getInstance();

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(SplashScreen.class.getResource("SplashScreen.fxml"));
        Scene scene = new Scene(fxmlLoader.load(),700,440);
        stage.setTitle("RChat");
        stage.setScene(scene);
        stage.show();
    }

//    public void loginOnClick(ActionEvent actionEvent) throws IOException {
//        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
//        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
//        Scene scene = new Scene(root, 700, 500);
//        stage.setScene(scene);
//        stage.show();
//        stage.setTitle("Log-in Page");
//    }

    public void registerOnClick(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Register.fxml"));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 700, 440);
        stage.setScene(scene);
        stage.show();
        stage.setTitle("Log-in Page");
    }

    public void btnLogInUserClick(ActionEvent actionEvent) throws IOException {
        logregcon = new LoginRegisterController();
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
        goToChatroom(actionEvent);
    }

    private void goToChatroom(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SplashScreen.class.getResource("Chatroom.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 700, 440);
        stage.setScene(scene);
        stage.show();
        stage.setTitle("RChat Room");
    }

    void showAlert(Alert.AlertType alertType, String content){
        alert.setAlertType(alertType);
        alert.setContentText(content);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        DatabaseManager db = DatabaseManager.getInstance();
        launch();
    }
}
