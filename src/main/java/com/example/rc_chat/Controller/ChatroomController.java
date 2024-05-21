package com.example.rc_chat.Controller;

import com.example.rc_chat.SplashScreen;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class ChatroomController {
    public AnchorPane apChatroom;
    public HBox cHBox;

    public void btnGoToProfileClick(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SplashScreen.class.getResource("Profile.fxml"));
        AnchorPane profile_component = fxmlLoader.load();

        cHBox.getChildren().remove(1);
        cHBox.getChildren().add(profile_component);
    }

    public void btnStartChatClick(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SplashScreen.class.getResource("Chatroom.fxml"));
        AnchorPane profile_component = fxmlLoader.load();

        cHBox.getChildren().remove(1);
        cHBox.getChildren().add(profile_component);
    }
}
