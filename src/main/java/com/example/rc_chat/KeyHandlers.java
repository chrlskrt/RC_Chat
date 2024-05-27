package com.example.rc_chat;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class KeyHandlers {
    public boolean TabPressed(KeyEvent event) {
        if(event.getCode() == KeyCode.TAB) {
            event.consume();
            return true;
        }
        return false;
    }

    public boolean EnterPressed(KeyEvent event) {
        if(event.getCode() == KeyCode.ENTER) {
            event.consume();
            return true;
        }
        return false;
    }
}
