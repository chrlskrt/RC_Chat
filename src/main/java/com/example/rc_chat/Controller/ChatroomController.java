package com.example.rc_chat.Controller;

import com.example.rc_chat.ChatMessage;
import com.example.rc_chat.Database.DatabaseManager;
import com.example.rc_chat.KeyHandlers;
import com.example.rc_chat.RC_Chat;
import com.example.rc_chat.Server.ChatClient;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.util.ArrayList;

import static com.example.rc_chat.RC_Chat.current_user;
import static com.example.rc_chat.RC_Chat.dbManager;

public class ChatroomController {
    public VBox vbox_chat_container;
    public Button btnSendChat;
    public TextArea txtareaMsg;
    public Alert alert = new Alert(Alert.AlertType.NONE);
    public AnchorPane ap_chatroom;
    int room_id; //Current chat room ID, WILL CHANGE DEPENDING ON WHICH CHAT IT'S STILL ON ATM
    private HomeController parentController;
    public KeyHandlers kh = new KeyHandlers();

    public void initialize() {
        txtareaMsg.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if(kh.EnterPressed(event)) {
                btnSendChatClick();
            }
        });
    }


    public void loadChatroom() throws IOException {
        Thread t = new Thread(new IncomingReader()); //STARTS MESSAGE READER, SEE LINE 91 FOR MORE DETAILS
        t.start();

        txtareaMsg.setEditable(false);
    }

    public void loadPrevChatroom() throws IOException {
        Thread t = new Thread(new IncomingReaderforPrevious()); //STARTS MESSAGE READER, SEE LINE 91 FOR MORE DETAILS
        t.start();
    }

    public void setParentController(HomeController parentController){
        this.parentController = parentController;
    }

    public void setRoom_id(int room_id){
        this.room_id = room_id;
        loadChats();
    }
    public void btnSendChatClick() {
        if (room_id == 0){
            showAlert(Alert.AlertType.INFORMATION, "You're not connected with anyone,yet.");
            return;
        }

        String message = txtareaMsg.getText();

        if (message.isEmpty()){
            showAlert(Alert.AlertType.WARNING, "You're trying to send an empty message.");
            return; // Should return so that it doesn't send an empty message to the Server
        }

        int user_id = current_user.getUser_id();

        // TODO : add necessary client server chuchuness ; room_id = 1 hahah
        ChatClient.getOut().println(room_id + "|" + user_id + "|" + message); //MAIN MESSAGE TO SEND, Formatted as ROOM_ID|USER_ID|MESSAGE

        // if successfully sent? to server, add message to database
        DatabaseManager.getInstance().saveMessage(room_id, user_id, message);

        txtareaMsg.clear();
    }
    private void loadChats(){
        ArrayList<ChatMessage> messages = dbManager.getMessages(room_id);

        vbox_chat_container.getChildren().clear();
        for (ChatMessage message : messages){
            addChatMessage(message.getSender_id(), message.getMessage());
        }
    }

    public void addChatMessage(int user_id, String message){
        if (user_id != current_user.getUser_id()){
            addChatMessage(message); // normal template
            return;
        }

        FXMLLoader loader = new FXMLLoader(RC_Chat.class.getResource("Chat-template-2.fxml")); // TODO: change template
        AnchorPane message_component = null;

        try {
            message_component = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Label lbl_message = (Label) message_component.lookup("#lbl_chatOwnMessage");

            lbl_message.setText(message);
        } catch (Exception e){
            e.printStackTrace();
        }

        vbox_chat_container.getChildren().add(message_component);
    }

    public void addChatMessage(String message)  {
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
    *               code.
    *
    * Additional: Platform.runLater works concurrently with the JavaFX Thread, so the Runnable it takes in runs on the JavaFX Thread, necessary for manipulating the Chats.*/
    public class IncomingReader extends Task<Void> {

        @Override
        protected Void call() throws Exception {
            try{
                BufferedReader in = new BufferedReader(new InputStreamReader(ChatClient.getSocket().getInputStream())); //Takes in messages from the server
                Platform.runLater(()->{
                    showStatus("Connecting you with someone...");
                });
                room_id = Integer.parseInt(in.readLine()); // scans the room ID first
                Platform.runLater(()-> {
                    try {
                        setNewChatButton();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

                String fromServer;
                while ((fromServer = in.readLine()) != null) { // constantly asks for messages
                    System.out.println(fromServer);
                    if (fromServer.startsWith("/8130")) {
                        System.out.println("Closing Reader due to transfer...");
                        break;
                    }

                    String [] message = fromServer.split("\\|", 2);
                    Platform.runLater(()->addChatMessage(Integer.parseInt(message[0]), message[1])); // See Line 101
                }
            } catch (IOException e) {
                System.err.println("Disconnected");
            }

            System.out.println("Ending Incoming Reader 1");
            return null;
        }


    }

    public class IncomingReaderforPrevious extends Task<Void> {

        @Override
        protected Void call() throws Exception {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(ChatClient.getSocket().getInputStream())); //Takes in messages from the server
                String fromServer;

                while ((fromServer = in.readLine()) != null) { // constantly asks for messages
                    System.out.println(fromServer);

                    if (fromServer.startsWith("/8130")) {
                        System.out.println("Closing Reader due to transfer...");
                        break;
                    }

                    String [] message = fromServer.split("\\|", 2);
                    Platform.runLater(()->addChatMessage(Integer.parseInt(message[0]), message[1])); // See Line 101
                }
            } catch (IOException e) {
                System.err.println("Disconnected");
            }

            System.out.println("Ending Incoming Reader 2");
            return null;
        }


    }
    public void setNewChatButton() throws IOException {
        parentController.addNewChatButton(room_id);

        showStatus("You're now connected with someone!");
    }

    private void showStatus(String message){
        FXMLLoader loader = new FXMLLoader(RC_Chat.class.getResource("Status-Template.fxml"));
        AnchorPane status_component = null;

        try {
            status_component = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Label lbl_message = (Label) status_component.lookup("#lbl_status");

            lbl_message.setText(message);
        } catch (Exception e){
            e.printStackTrace();
        }

        vbox_chat_container.getChildren().add(status_component);

        txtareaMsg.setEditable(true);
    }
}
