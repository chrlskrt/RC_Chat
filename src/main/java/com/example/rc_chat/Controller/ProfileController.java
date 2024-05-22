package com.example.rc_chat.Controller;

import javafx.scene.control.Label;

import static com.example.rc_chat.RC_Chat.current_user;

public class ProfileController {
    public Label lbl_username;

    void loadPage(){
        lbl_username.setText(current_user.getUsername());
    }
}
