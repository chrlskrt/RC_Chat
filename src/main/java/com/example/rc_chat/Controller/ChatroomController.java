package com.example.rc_chat.Controller;

import com.example.rc_chat.ChatMessage;
import com.example.rc_chat.RC_Chat;
import com.example.rc_chat.Server.ChatClient;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static com.example.rc_chat.RC_Chat.current_user;
import static com.example.rc_chat.RC_Chat.dbManager;

public class ChatroomController {
    public VBox vbox_chat_container;
    public Button btnSendChat;
    public TextArea txtareaMsg;
    public Alert alert = new Alert(Alert.AlertType.NONE);
    int room_id; //Current chat room ID, WILL CHANGE DEPENDING ON WHICH CHAT IT'S STILL ON ATM

    public void loadChatroom() throws IOException {
        loadChats();
        Thread t = new Thread(new IncomingReader()); //STARTS MESSAGE READER, SEE LINE 91 FOR MORE DETAILS
        t.start();
    }
    public void btnSendChatClick(ActionEvent actionEvent) {
        String message = txtareaMsg.getText();

        if (message.isEmpty()){
            showAlert(Alert.AlertType.WARNING, "You're trying to send an empty message.");
            return; // Should return so that it doesn't send an empty message to the Server
        }

        int user_id = current_user.getUser_id();

        // TODO : add necessary client server chuchuness ; room_id = 1 hahah
        ChatClient.getOut().println(room_id + "|" + user_id + "|" + message); //MAIN MESSAGE TO SEND, Formatted as ROOM_ID|USER_ID|MESSAGE

        // if successfully sent? to server, add message to database

        txtareaMsg.clear();
    }

    private void loadChats(){
        ArrayList<ChatMessage> messages = dbManager.getMessages(1);

        vbox_chat_container.getChildren().clear();
        for (ChatMessage message : messages){
            addChatMessage(message.getMessage());
        }
    }

    public void addChatMessage(String message)  { // TODO : add user_id to see the diff kinds of people nga present sa chat
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

    /*I N C O M I N G   R E A D E R   T A S K
    * PURPOSE: A separate thread that will listen for any new messages from the server.
    *          It is initialized when chats are loaded.
    * HOW IT WORKS: IncomingReader works as a task that is running in the background.
    *               It creates a BufferedReader from the static Client's socket InputStream,
    *               and does its necessary business. First it takes in the Room_ID and will
    *               indefinitely receive messages from other users until it receives a stopping
    *               code (TODO LATER).
    *
    * Additional: Platform.runLater works concurrently with the JavaFX Thread, so the Runnable it takes in runs on the JavaFX Thread, necessary for manipulating the Chats.*/

    public class IncomingReader extends Task<Void> {

        @Override
        protected Void call() throws Exception {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(ChatClient.getSocket().getInputStream())); //Takes in messages from the server
            ) {
                room_id = Integer.parseInt(in.readLine()); // scans the room ID first
                String fromServer;
                while ((fromServer = in.readLine()) != null) { // constantly asks for messages
                    System.out.println(fromServer);
                    String message = fromServer;
                    Platform.runLater(()->addChatMessage(message)); // See Line 101
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
