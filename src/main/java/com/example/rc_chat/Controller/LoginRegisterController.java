package com.example.rc_chat.Controller;

import com.example.rc_chat.SplashScreen;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class LoginRegisterController {
    @FXML
    public AnchorPane regAnchorPane;
    @FXML
    public BorderPane loginBorderPane;
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
}