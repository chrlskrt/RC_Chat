package com.example.rc_chat;

import com.example.rc_chat.Database.DatabaseManager;
import com.example.rc_chat.Database.SQLConnection;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class SplashScreen extends Application {
    public AnchorPane splashAnchor;
    DatabaseManager dbManager = DatabaseManager.getInstance();

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(SplashScreen.class.getResource("SplashScreen.fxml"));
        Scene scene = new Scene(fxmlLoader.load(),700,440);
        stage.setTitle("RChat");
        stage.setScene(scene);
        stage.show();
    }

    public void loginOnClick(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 700, 500);
        stage.setScene(scene);
        stage.show();
        stage.setTitle("Log-in Page");
    }

    public void registerOnClick(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Register.fxml"));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 700, 500);
        stage.setScene(scene);
        stage.show();
        stage.setTitle("Log-in Page");
    }

    public static void main(String[] args) {
        DatabaseManager db = DatabaseManager.getInstance();
        launch();
    }
}
