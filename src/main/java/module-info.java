module com.example.rc_chat {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;


    opens com.example.rc_chat to javafx.fxml;
    exports com.example.rc_chat;
    exports com.example.rc_chat.Controller;
    opens com.example.rc_chat.Controller to javafx.fxml;
}