package com.example.rc_chat;

import com.example.rc_chat.Controller.HomeController;
import com.example.rc_chat.Controller.LoginRegisterController;
import com.example.rc_chat.Database.DatabaseManager;
import com.example.rc_chat.Database.User;
import com.example.rc_chat.Database.dbStatus;
import com.example.rc_chat.Server.ChatClient;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class RC_Chat extends Application {
    public AnchorPane splashAnchor;
    public TextField tf_logUsername;
    public Button btnLogInUser, btnRegisterAcc;
    public PasswordField pf_logPassword;
    public LoginRegisterController logregcon;
    public Alert alert = new Alert(Alert.AlertType.NONE);
    public static DatabaseManager dbManager;
    public static User current_user = User.getInstance();;
    public static ChatClient client;
    public KeyHandlers kh = new KeyHandlers();

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(RC_Chat.class.getResource("SplashScreen.fxml"));
        Scene scene = new Scene(fxmlLoader.load(),700,440);
        stage.setTitle("RChat");
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                ChatClient.getOut().println("/8130");
                try {
                    ChatClient.getSocket().close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    // TABBING SHIT
    public void initialize() {
        TabHandler();
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
        } else if (logRes == dbStatus.LOGIN_FAILED){
            showAlert(Alert.AlertType.WARNING, "Username or password is incorrect.");
            return;
        }
        goToChatroom(actionEvent);
//        loadScreen();
    }

    public void loadScreen() throws IOException {
        AnchorPane ap = splashAnchor;
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

//        FXMLLoader fxmlLoader = new FXMLLoader(RC_Chat.class.getResource("LoadingScreen.fxml"));
        Parent p = FXMLLoader.load(getClass().getResource("LoadingScreen.fxml"));
        p.setLayoutY(bounds.getMinY());
        p.setLayoutX(bounds.getMinX());

        ap.setBackground(null);
        ap.getChildren().clear();
        ap.getChildren().add(p);
    }

    private void goToChatroom(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RC_Chat.class.getResource("MainChat.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1000,540);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        Screen screen = Screen.getPrimary();

        // loading the mainChat.fxml
        HomeController hc = fxmlLoader.getController();
        hc.loadPage();

        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
        stage.setTitle("RChat Room");
    }

    void showAlert(Alert.AlertType alertType, String content){
        alert.setAlertType(alertType);
        alert.setContentText(content);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    public void TabHandler() {
        tf_logUsername.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if(kh.TabPressed(event)) {
                pf_logPassword.requestFocus();
            }
        });

        pf_logPassword.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if(kh.TabPressed(event)) {
                btnLogInUser.requestFocus();
            } if(kh.EnterPressed(event)) {
                // TODO: HUHUHUHU IG ENTER, LOGIN
//                try {
//                    btnLogInUserClick(new ActionEvent());
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
            }
        });

        btnLogInUser.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if(kh.TabPressed(event)) {
                btnRegisterAcc.requestFocus();
            }
        });

        btnRegisterAcc.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if(kh.TabPressed(event)) {
                tf_logUsername.requestFocus();
            }
        });
    }


    public static void main(String[] args) {
        try {
            dbManager = DatabaseManager.getInstance();
            client = ChatClient.getInstance();
            launch();
        } catch (RuntimeException e) {
            Platform.runLater(() -> {
                Alert x = new Alert(Alert.AlertType.ERROR);
                x.setHeaderText("Database Offline");
                x.setContentText("Currently, our database is offline due to maintenance. \nPlease try again later.");
                x.showAndWait();
            });
        } catch (IOException e) {
            Platform.runLater(() -> {
                System.err.println("SERVER OFFLINE");
                Alert x = new Alert(Alert.AlertType.ERROR);
                x.setHeaderText("Server Offline");
                x.setContentText("Currently, our server is offline due to maintenance. \nPlease try again later.");
                x.showAndWait();
            });
        }

    }
}
