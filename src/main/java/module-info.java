module com.example.rc_chat {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.rc_chat to javafx.fxml;
    exports com.example.rc_chat;
}