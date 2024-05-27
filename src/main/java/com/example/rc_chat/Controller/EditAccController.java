package com.example.rc_chat.Controller;

import com.example.rc_chat.Database.SQLConnection;
import com.example.rc_chat.RC_Chat;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.example.rc_chat.RC_Chat.current_user;

public class EditAccController {
    public AnchorPane apEditProf;
    public PasswordField txt_newPass;
    public TextField txt_newUsern;
    public Circle circleImg;
    public Label lbl_username;
    public Label lbl_userID;

    @FXML
    public void initialize() {
        lbl_username.setText(current_user.getUsername());
        lbl_userID.setText("#000" + String.valueOf(current_user.getUser_id()));
        setProfilePicture();

        txt_newUsern.setText(current_user.getUsername());
        txt_newPass.setText(current_user.getPassword());
    }

    public void setProfilePicture() {
        Image img = new Image("https://i.pinimg.com/736x/a9/e5/79/a9e57939084a578206e77566b685c47b.jpg", false);
        circleImg.setFill(new ImagePattern(img));
    }

    public void SaveEditAccOnClick(ActionEvent actionEvent) {
        String newusername, newpassword;

        newusername = txt_newUsern.getText();
        newpassword = txt_newPass.getText();

        try(Connection c = SQLConnection.getConnection();
            PreparedStatement stmt = c.prepareStatement("UPDATE tbluser SET username = ?, password = ? WHERE user_id = ?")) {

            stmt.setString(1,newusername);
            stmt.setString(2,newpassword);
            stmt.setInt(3,current_user.getUser_id());

            current_user.setUsername(newusername);
            current_user.setPassword(newpassword);

            stmt.executeUpdate();
            System.out.println("Edited real");
        }catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void CancelEditOnClick() throws IOException {
//        AnchorPane a = apEditProf;
//        a.getScene().getStylesheets().clear();
        HBox ap_profile = (HBox) apEditProf.getParent();

        FXMLLoader fxmlLoader = new FXMLLoader(RC_Chat.class.getResource("Profile.fxml"));
        AnchorPane p = fxmlLoader.load();
//
//        a.getChildren().clear();
////        a.getChildren().add(p);
        ProfileController pc = fxmlLoader.getController();
        pc.loadPage();

        ap_profile.getChildren().remove(1);
        ap_profile.getChildren().add(p);
        HBox.setHgrow(p, Priority.ALWAYS);
    }
}
