package com.example.rc_chat.Controller;

import com.example.rc_chat.Database.SQLConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.example.rc_chat.RC_Chat.current_user;

public class EditAccController {
    public AnchorPane apEditProf;
    public PasswordField txt_newPass;
    public TextField txt_newUsern;

    @FXML
    public void initialize() {
        txt_newUsern.setText(current_user.getUsername());
        txt_newPass.setText(current_user.getPassword());
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

    public void CancelEditOnClick(ActionEvent actionEvent) {

    }
}
