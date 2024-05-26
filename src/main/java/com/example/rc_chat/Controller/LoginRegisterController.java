package com.example.rc_chat.Controller;

import com.example.rc_chat.Database.dbStatus;
import com.example.rc_chat.KeyHandlers;
import com.example.rc_chat.RC_Chat;
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
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

import static com.example.rc_chat.RC_Chat.dbManager;

public class LoginRegisterController {
    @FXML
    public AnchorPane regAnchorPane;
    @FXML
    public BorderPane loginBorderPane;
    public TextField tf_regUsername;
    public PasswordField pf_regPassword, pf_regConfirmPassword;
    public Button btnRegister;
    public Alert alert = new Alert(Alert.AlertType.NONE);
    public KeyHandlers kh = new KeyHandlers();
    public void initialize() {
        TabHandler();
    }

    public void backRegOnClick() throws IOException {
        AnchorPane a = regAnchorPane;
        a.getScene().getStylesheets().clear();
        FXMLLoader fxmlLoader = new FXMLLoader(RC_Chat.class.getResource("SplashScreen.fxml"));

        Parent p = fxmlLoader.load();
        a.getChildren().clear();
        a.getChildren().add(p);
    }

    public void btnRegisterUserClick(ActionEvent actionEvent) throws IOException {
        String username = tf_regUsername.getText();
        String password = pf_regPassword.getText();
        String confirmPass = pf_regConfirmPassword.getText();

        if (username.isEmpty() || password.isEmpty()){
            showAlert(Alert.AlertType.ERROR, "Username or password is empty.");
            return;
        } if(!password.equals(confirmPass)) {
            showAlert(Alert.AlertType.ERROR, "Password does not match.");
            return;
        }

        dbStatus regRes = dbManager.createUser (username, password);
        if (regRes == dbStatus.REGISTER_USER_EXISTS){
            showAlert(Alert.AlertType.WARNING, "User exists");
            return;
        } else if (regRes == dbStatus.REGISTER_FAILED){
            showAlert(Alert.AlertType.ERROR, "Account creation unsuccessful");
            return;
        }

        goToHomePage(actionEvent);
    }

    public void goToHomePage(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RC_Chat.class.getResource("SplashScreen.fxml"));
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

    public void TabHandler() {
        tf_regUsername.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if(kh.TabPressed(event)) {
                pf_regPassword.requestFocus();
            }
        });

        pf_regPassword.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if(kh.TabPressed(event)) {
                pf_regConfirmPassword.requestFocus();
            }
        });

        pf_regConfirmPassword.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if(kh.TabPressed(event)) {
                btnRegister.requestFocus();
            }
        });

        btnRegister.addEventHandler(KeyEvent.KEY_PRESSED, event-> {
            if(kh.TabPressed(event)) {
                tf_regUsername.requestFocus();
            }
        });
    }
}