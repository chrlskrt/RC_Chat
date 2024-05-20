package com.example.rc_chat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class SplashScreen extends Application {
    public AnchorPane splashAnchor;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(SplashScreen.class.getResource("SplashScreen.fxml"));
        Scene scene = new Scene(fxmlLoader.load(),700,440);
        stage.setTitle("RChat");
        stage.setScene(scene);
        stage.show();
    }

    public void loginOnClick() throws IOException {
        AnchorPane a = splashAnchor;
        a.getScene().getStylesheets().clear();

        Parent p = FXMLLoader.load(getClass().getResource("Login.fxml"));
        a.getChildren().clear();
        a.getChildren().add(p);
    }

    public void registerOnClick() throws IOException {
        AnchorPane a = splashAnchor;
        a.getScene().getStylesheets().clear();

        Parent p = FXMLLoader.load(getClass().getResource("Register.fxml"));
        a.getChildren().clear();
        a.getChildren().add(p);
    }

    public static void main(String[] args) {
        launch();
    }
}
