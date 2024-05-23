package com.example.rc_chat.Controller;

import com.example.rc_chat.ChatMessage;
import com.example.rc_chat.RC_Chat;
import com.example.rc_chat.Server.ChatClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

import static com.example.rc_chat.RC_Chat.current_user;

public class HomeController {
    public AnchorPane apChatroom;
    public HBox cHBox;
    public Label lblUsername;
    public TextArea txtareaMsg;
    public VBox vbox_mainchat_container;
    public Alert alert = new Alert(Alert.AlertType.NONE);

    public void loadPage(){
        lblUsername.setText(current_user.getUsername());
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

    void showAlert(Alert.AlertType alertType, String content){
        alert.setAlertType(alertType);
        alert.setContentText(content);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
