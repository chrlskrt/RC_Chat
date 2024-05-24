package com.example.rc_chat.Controller;

import com.example.rc_chat.ChatMessage;
import com.example.rc_chat.RC_Chat;
import com.example.rc_chat.Server.ChatClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.example.rc_chat.RC_Chat.*;

public class HomeController {
    public AnchorPane apChatroom;
    public HBox cHBox;
    public Label lblUsername;
    public TextArea txtareaMsg;
    public VBox vbox_mainchat_container;
    public Alert alert = new Alert(Alert.AlertType.NONE);
    public VBox vbox_chatroom_container;

    public void loadPage(){
        lblUsername.setText(current_user.getUsername());

        /* Buttons for previous chats */
        HashMap<Integer, String> rooms = dbManager.getChatRooms();

        vbox_chatroom_container.getChildren().clear();

        for (Map.Entry<Integer, String> room : rooms.entrySet()){
            FXMLLoader loader = new FXMLLoader(RC_Chat.class.getResource("Chat-Button-Template.fxml"));
            AnchorPane chatroomButton;
            try {
                chatroomButton = loader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try {
                Button btnAccessPreviousChat = (Button) chatroomButton.lookup("#btnAccessPreviousChat");

                btnAccessPreviousChat.setText(room.getValue());

                btnAccessPreviousChat.setOnAction(e -> {
                    try {
                        btnGoToPreviousChat(e, room.getKey());
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });

            } catch (Exception e){
                throw new RuntimeException(e);
            }

            vbox_chatroom_container.getChildren().add(chatroomButton);
        }
    }

    public void btnGoToProfileClick(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RC_Chat.class.getResource("Profile.fxml"));
        AnchorPane profile_component = fxmlLoader.load();
        ProfileController pc = fxmlLoader.getController();
        pc.loadPage();
        cHBox.getChildren().remove(1);
        cHBox.getChildren().add(profile_component);
    }

    public void btnStartChatClick(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RC_Chat.class.getResource("Chatroom-template.fxml"));
        AnchorPane profile_component = fxmlLoader.load();

        ChatClient.getOut().println("4156"); //SENDS A CODE TO THE SERVER THAT IT WANTS TO START A NEW CHAT
        ChatroomController cc = fxmlLoader.getController();
        cc.loadChatroom();

        cHBox.getChildren().remove(1);
        cHBox.getChildren().add(profile_component);
    }

    public void btnGoToPreviousChat(ActionEvent event, int room_id) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RC_Chat.class.getResource("Chatroom-template.fxml"));
        AnchorPane profile_component = fxmlLoader.load();

        ChatClient.getOut().println("9104"); //SENDS A CODE TO THE SERVER THAT IT WANTS TO START A NEW CHAT
        ChatroomController cc = fxmlLoader.getController();
        cc.loadChatroom();
        cc.setRoom_id(room_id);

        cHBox.getChildren().remove(1);
        cHBox.getChildren().add(profile_component);
    }

    void showAlert(Alert.AlertType alertType, String content){
        alert.setAlertType(alertType);
        alert.setContentText(content);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
