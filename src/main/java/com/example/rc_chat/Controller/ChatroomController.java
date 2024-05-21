package com.example.rc_chat.Controller;

import com.example.rc_chat.RC_Chat;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class ChatroomController {
    public AnchorPane apChatroom;
    public HBox cHBox;

    public void btnGoToProfileClick(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RC_Chat.class.getResource("Profile.fxml"));
        AnchorPane profile_component = fxmlLoader.load();

        cHBox.getChildren().remove(1);
        cHBox.getChildren().add(profile_component);
    }

    public void btnStartChatClick(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RC_Chat.class.getResource("Chatroom.fxml"));
        AnchorPane profile_component = fxmlLoader.load();

        cHBox.getChildren().remove(1);
        cHBox.getChildren().add(profile_component);
    }
}
