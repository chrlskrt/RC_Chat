package com.example.rc_chat.Controller;

import com.example.rc_chat.ChatMessage;
import com.example.rc_chat.ChatRoom;
import com.example.rc_chat.RC_Chat;
import com.example.rc_chat.Server.ChatClient;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;

import static com.example.rc_chat.RC_Chat.*;

public class HomeController {
    public AnchorPane apChatroom;
    public HBox cHBox;
    public Label lblUsername;
    public TextArea txtareaMsg;
    public VBox vbox_mainchat_container;
    public Alert alert = new Alert(Alert.AlertType.NONE);
    @FXML
    public VBox vbox_chatroom_container;
    public Button btnStartChat;
    public AnchorPane ap_toolbar;

    @FXML
    public void initialize() {
//        vbox_chatroom_container.prefHeightProperty().bind(cHBox.heightProperty().divide(2));
    }

    public void loadPage(){
        lblUsername.setText(current_user.getUsername());
        vbox_chatroom_container.setSpacing(5);
        loadChatRoomButtons();
    }

    public void btnGoToProfileClick(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RC_Chat.class.getResource("Profile.fxml"));
        AnchorPane profile_component = fxmlLoader.load();

        ChatClient.getOut().println("/8130"); //SENDS A CANCELATION CODE BEFORE IT TRANSITIONS
        ProfileController pc = fxmlLoader.getController();
        pc.loadPage();
        cHBox.getChildren().remove(1);
        cHBox.getChildren().add(profile_component);
        HBox.setHgrow(profile_component, Priority.ALWAYS);
    }

    public void btnStartChatClick(ActionEvent event) throws IOException {
//        btnStartChat.setDisable(true);
        ap_toolbar.setDisable(true);
//        vbox_chatroom_container.setDisable(true);
        FXMLLoader fxmlLoader = new FXMLLoader(RC_Chat.class.getResource("Chatroom-template.fxml"));
        AnchorPane chat_component = fxmlLoader.load();

        ChatClient.getOut().println("/8130"); //SENDS A CANCELATION CODE BEFORE IT TRANSITIONS
        ChatClient.getOut().println("/4156"); //SENDS A CODE TO THE SERVER THAT IT WANTS TO START A NEW CHAT
        ChatroomController cc = fxmlLoader.getController();
        cc.loadChatroom();
        cc.setParentController(this);

        cHBox.getChildren().remove(1);
        cHBox.getChildren().add(chat_component);
        HBox.setHgrow(chat_component, Priority.ALWAYS);
    }

    public void btnGoToPreviousChat(ActionEvent event, int room_id) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RC_Chat.class.getResource("Chatroom-template.fxml"));
        AnchorPane chat_component = fxmlLoader.load();

        ChatClient.getOut().println("/8130"); //SENDS A CANCELATION CODE BEFORE IT TRANSITIONS
        ChatClient.getOut().println("/9104"); //SENDS A CODE TO THE SERVER THAT IT WANTS TO CONTINUE PREVIOUS CHAT
        ChatClient.getOut().println(room_id);
        ChatroomController cc = fxmlLoader.getController();
        cc.loadPrevChatroom();
        cc.setRoom_id(room_id);

        cHBox.getChildren().remove(1);
        cHBox.getChildren().add(chat_component);
        HBox.setHgrow(chat_component, Priority.ALWAYS);
    }

    public void clearMainChat() {
        apChatroom.getChildren().clear();
    }

    void showAlert(Alert.AlertType alertType, String content){
        alert.setAlertType(alertType);
        alert.setContentText(content);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void loadChatRoomButtons(){
        /* Buttons for previous chats */
        ArrayList<ChatRoom> rooms = dbManager.getChatRooms();

        vbox_chatroom_container.getChildren().clear();

        for (ChatRoom room : rooms){
            addChatButton(room);
        }
    }

    public void addNewChatButton(int room_id){
        ChatRoom newRoom = dbManager.getRoomInfo(room_id);
        addChatButton(newRoom);
    }

    public void addChatButton(ChatRoom room){
//        btnStartChat.setDisable(false);
        ap_toolbar.setDisable(false);
//        vbox_chatroom_container.setDisable(false);
        if (Platform.isFxApplicationThread()){
            FXMLLoader loader = new FXMLLoader(RC_Chat.class.getResource("Chat-Button-Template.fxml"));
            AnchorPane chatroomButton;
            try {
                chatroomButton = loader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try {
                Button btnAccessPreviousChat = (Button) chatroomButton.lookup("#btnAccessPreviousChat");

                btnAccessPreviousChat.setText(room.getOtherUser());
                btnAccessPreviousChat.setOnAction(e -> {
                    try {
                        btnGoToPreviousChat(e, room.getRoom_id());
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });

            } catch (Exception e){
                throw new RuntimeException(e);
            }

            vbox_chatroom_container.getChildren().add(0,chatroomButton);
        }
    }
}
