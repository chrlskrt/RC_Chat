package com.example.rc_chat;

import com.example.rc_chat.Controller.HomeController;
import com.example.rc_chat.Controller.LoginRegisterController;
import com.example.rc_chat.Database.DatabaseManager;
import com.example.rc_chat.Database.User;
import com.example.rc_chat.Database.dbStatus;
import com.example.rc_chat.Server.ChatClient;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class RC_Chat extends Application {
    public AnchorPane splashAnchor;
    public TextField tf_logUsername;
    public PasswordField pf_logPassword;
    public LoginRegisterController logregcon;
    public Alert alert = new Alert(Alert.AlertType.NONE);
    public static DatabaseManager dbManager = DatabaseManager.getInstance();;
    public static User current_user = User.getInstance();;
    public static ChatClient client;

    static {
        try {
            client = ChatClient.getInstance();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(RC_Chat.class.getResource("SplashScreen.fxml"));
        Scene scene = new Scene(fxmlLoader.load(),700,440);
        stage.setTitle("RChat");
        stage.setScene(scene);
        stage.show();
    }

    public void registerOnClick(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Register.fxml"));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 700, 440);
        stage.setScene(scene);
        stage.show();
        stage.setTitle("Log-in Page");
    }

    public void btnLogInUserClick(ActionEvent actionEvent) throws IOException {
        logregcon = new LoginRegisterController();
        String username = tf_logUsername.getText();
        String password = pf_logPassword.getText();

        if (username.isEmpty() || password.isEmpty()){
            showAlert(Alert.AlertType.ERROR, "Username or password is empty.");
            return;
        }

        dbStatus logRes = dbManager.logUser(username, password);
        if (logRes == dbStatus.LOGIN_USER_NOT_FOUND){
            showAlert(Alert.AlertType.ERROR, "User not found.");
            return;
        } else if (logRes == dbStatus.LOGIN_ERROR){
            showAlert(Alert.AlertType.WARNING, "Username or password is incorrect.");
            return;
        }
        goToChatroom(actionEvent);
    }

    private void goToChatroom(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RC_Chat.class.getResource("MainChat.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1300, 800);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
//        root.setId("#chatroom");

        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        // Calculate the center position
        double centerX = bounds.getMinX() + (bounds.getWidth() - scene.getWidth()) / 2;
        double centerY = bounds.getMinY() + (bounds.getHeight() - scene.getHeight()) / 2;

        // Set the stage position
        stage.setX(centerX);
        stage.setY(centerY);

        // loading the mainChat.fxml
        HomeController hc = fxmlLoader.getController();
        hc.loadPage();

        stage.setScene(scene);
        stage.show();
        stage.setTitle("RChat Room");
    }

    void showAlert(Alert.AlertType alertType, String content){
        alert.setAlertType(alertType);
        alert.setContentText(content);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}
