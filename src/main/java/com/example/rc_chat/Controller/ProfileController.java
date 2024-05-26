package com.example.rc_chat.Controller;

import com.example.rc_chat.Database.SQLConnection;
import com.example.rc_chat.RC_Chat;
import javafx.event.ActionEvent;
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
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLData;
import java.sql.SQLException;

import static com.example.rc_chat.RC_Chat.current_user;

public class ProfileController {
    public Label lbl_username , lbl_userID;
    public AnchorPane apChatroom, profButtons;
    public Circle circleImg;
    public HomeController hc;
    public LoginRegisterController lgr;

    public void setProfilePicture() {
        Image img = new Image("https://i.pinimg.com/736x/a9/e5/79/a9e57939084a578206e77566b685c47b.jpg", false);
        circleImg.setFill(new ImagePattern(img));
    }

    void loadPage(){
        lbl_username.setText(current_user.getUsername());
        lbl_userID.setText("#000" + String.valueOf(current_user.getUser_id()));
        setProfilePicture();
    }

    public void deleteAccOnClick() {
        try(Connection c = SQLConnection.getConnection();
            PreparedStatement stmt = c.prepareStatement("DELETE FROM tbluser WHERE user_id = ?")) {
            stmt.setInt(1,current_user.getUser_id());
//            stmt.executeUpdate();
            goToSplashScreen();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public void goToSplashScreen() throws IOException {
        // TODO: but how :(
        FXMLLoader fxmlLoader = new FXMLLoader(RC_Chat.class.getResource("SplashScreen.fxml"));
        Parent root = fxmlLoader.load();

        hc = fxmlLoader.getController();
        hc.clearMainChat(); //hopefully removes everything sa mainchat.fxml ???

    }

    public void LogOutOnClick(ActionEvent actionEvent) {
    }

    public void EditAccOnClick() throws IOException {
        AnchorPane a = profButtons;
        a.getScene().getStylesheets().clear();

        FXMLLoader fxmlLoader = new FXMLLoader(RC_Chat.class.getResource("EditAccount.fxml"));
        Parent p = fxmlLoader.load();

        a.getChildren().clear();
        a.getChildren().add(p);
    }

    public void exitEdit() {

    }

}
