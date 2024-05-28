package com.example.rc_chat;

import com.example.rc_chat.Controller.HomeController;
import com.example.rc_chat.Controller.LoginRegisterController;
import com.example.rc_chat.Database.DatabaseManager;
import com.example.rc_chat.Database.User;
import com.example.rc_chat.Database.dbStatus;
import com.example.rc_chat.Server.ChatClient;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.io.IOException;

public class RC_Chat extends Application {
    public HBox splashMainBox;
    @FXML
    private AnchorPane splashAnchor;
    @FXML
    private TextField tf_logUsername;
    @FXML
    private Button btnLogInUser, btnRegisterAcc;
    @FXML
    private PasswordField pf_logPassword;
    private LoginRegisterController logregcon;
    private Alert alert = new Alert(Alert.AlertType.NONE);
    public static DatabaseManager dbManager;
    public static User current_user = User.getInstance();
    private static ChatClient client;
    private KeyHandlers kh = new KeyHandlers();

    // Reference to the login stage
    private Stage loginStage;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(RC_Chat.class.getResource("SplashScreen.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 700, 440);
        stage.setTitle("RChat");
        stage.setScene(scene);
        stage.show();

        // Retrieve the splashAnchor from the FXML loader's controller
        RC_Chat controller = fxmlLoader.getController();
        this.splashAnchor = controller.splashAnchor;

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

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Username or password is empty.");
            return;
        }

        dbStatus logRes = dbManager.logUser(username, password);
        if (logRes == dbStatus.LOGIN_USER_NOT_FOUND) {
            showAlert(Alert.AlertType.ERROR, "User not found.");
            return;
        } else if (logRes == dbStatus.LOGIN_FAILED) {
            showAlert(Alert.AlertType.WARNING, "Username or password is incorrect.");
            return;
        }

        // Load the screen and then go to the chatroom after the loading screen is done
        loadScreen(actionEvent);
    }

    public void loadScreen(ActionEvent actionEvent) throws IOException {
        if (splashAnchor == null) {
            System.err.println("splashAnchor is not initialized");
            return;
        }

        Parent p = FXMLLoader.load(getClass().getResource("LoadingScreen.fxml"));
//        splashAnchor.getChildren().clear();
//        splashAnchor.getChildren().add(p);
        splashMainBox.getChildren().clear();
        splashMainBox.getChildren().add(p);
        HBox.setHgrow(p, Priority.ALWAYS);

        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(event -> {
            try {
                goToChatroom(actionEvent);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            splashAnchor.getScene().getWindow().hide();
        });
        pause.play();
    }

    private void goToChatroom(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RC_Chat.class.getResource("MainChat.fxml"));
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root, 1000, 540);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        HomeController hc = fxmlLoader.getController();
        hc.loadPage();

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.setTitle("RChat Room");
        stage.show();
    }

    private void showAlert(Alert.AlertType alertType, String content) {
        alert.setAlertType(alertType);
        alert.setContentText(content);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void TabHandler() {
        tf_logUsername.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (kh.TabPressed(event)) {
                pf_logPassword.requestFocus();
            }
        });

        pf_logPassword.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (kh.TabPressed(event)) {
                btnLogInUser.requestFocus();
            }
            if (kh.EnterPressed(event)) {
                // Implement login on Enter key press if needed
            }
        });

        btnLogInUser.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (kh.TabPressed(event)) {
                btnRegisterAcc.requestFocus();
            }
        });

        btnRegisterAcc.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (kh.TabPressed(event)) {
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
