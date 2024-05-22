package com.example.rc_chat.Controller;

import com.example.rc_chat.ChatMessage;
import com.example.rc_chat.RC_Chat;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;

import static com.example.rc_chat.RC_Chat.current_user;
import static com.example.rc_chat.RC_Chat.dbManager;

public class ChatroomController {
    public VBox vbox_chat_container;
    public Button btnSendChat;
    public TextArea txtareaMsg;
    public Alert alert = new Alert(Alert.AlertType.NONE);
    int room_id;

    public void loadChatroom(){
        loadChats();
    }
    public void btnSendChatClick(ActionEvent actionEvent) {
        String message = txtareaMsg.getText();

        if (message.isEmpty()){
            showAlert(Alert.AlertType.WARNING, "You're trying to send an empty message.");
        }

        int user_id = current_user.getUser_id();

        // TODO : add necessary client server chuchuness ; room_id = 1 hahah

        // if successfully sent? to server, add message to database
        // insert code

        txtareaMsg.clear();
        addChatMessage(message);
    }

    private void loadChats(){
        ArrayList<ChatMessage> messages = dbManager.getMessages(1);

        vbox_chat_container.getChildren().clear();
        for (ChatMessage message : messages){
            addChatMessage(message.getMessage());
        }
    }

    private void addChatMessage(String message)  { // TODO : add user_id to see the diff kinds of people nga present sa chat
        FXMLLoader loader = new FXMLLoader(RC_Chat.class.getResource("Chat-template.fxml"));
        AnchorPane message_component = null;

        try {
            message_component = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Label lbl_message = (Label) message_component.lookup("#lbl_chatMessage");

            lbl_message.setText(message);
        } catch (Exception e){
            e.printStackTrace();
        }

        vbox_chat_container.getChildren().add(message_component);
    }

    void showAlert(Alert.AlertType alertType, String content){
        alert.setAlertType(alertType);
        alert.setContentText(content);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
