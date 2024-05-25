package com.example.rc_chat.Controller;

import javafx.event.ActionEvent;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.shape.Circle;

public class EditAccController {
    public PasswordField txt_newPass;
    public TextField txt_newUsern;

    public void SaveEditAccOnClick(ActionEvent actionEvent) {
        String newusername, newpassword;

        newusername = txt_newUsern.getText();
        newpassword = txt_newPass.getText();

    }
}
