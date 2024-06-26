package com.example.rc_chat.Controller;

import com.example.rc_chat.Database.SQLConnection;
import com.example.rc_chat.RC_Chat;
import com.example.rc_chat.Server.ChatClient;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLData;
import java.sql.SQLException;

import static com.example.rc_chat.RC_Chat.current_user;
import static javafx.application.Application.getUserAgentStylesheet;

public class ProfileController {
    public Label lbl_username , lbl_userID;
    public AnchorPane apChatroom, profButtons;
    public Circle circleImg;
    public HomeController hc;
    public LoginRegisterController lgr;
    public AnchorPane ap_Profile;

    public void setProfilePicture() {
        Image img = new Image("https://i.ibb.co/Ht4pQ6d/profilepic.png", false);
        circleImg.setFill(new ImagePattern(img));
    }

    void loadPage(){
        lbl_username.setText(current_user.getUsername());
        lbl_userID.setText("#000" + String.valueOf(current_user.getUser_id()));
        setProfilePicture();
    }

    public void deleteAccOnClick(ActionEvent actionEvent) {
        try(Connection c = SQLConnection.getConnection();
            PreparedStatement stmt = c.prepareStatement("DELETE FROM tbluser WHERE user_id = ?")) {
            stmt.setInt(1,current_user.getUser_id());
            stmt.executeUpdate();
            goToSplashScreen(actionEvent);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public void goToSplashScreen(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RC_Chat.class.getResource("SplashScreen.fxml"));
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root,700,440);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setTitle("RChat");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();

        current_user.setPassword("");
        current_user.setUsername("");
        current_user.setUser_id(-1);
    }

    public void LogOutOnClick(ActionEvent actionEvent) throws IOException {
        // pop up

        goToSplashScreen(actionEvent);
    }

    public void EditAccOnClick() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RC_Chat.class.getResource("EditAccount.fxml"));
        Parent p = fxmlLoader.load();

        HBox home_box = (HBox) ap_Profile.getParent();
        home_box.getChildren().remove(1);
        home_box.getChildren().add(p);
        HBox.setHgrow(p, Priority.ALWAYS);
    }

}
