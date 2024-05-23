package com.example.rc_chat.Server;

import java.io.*;
import java.net.*;

import static com.example.rc_chat.RC_Chat.current_user;
public class ChatClient {
    private final String IP_ADDRESS = "localhost";
    private final int PORT = 1803;
    private static ChatClient instance; // Singleton instance sa ChatClient
    private static PrintWriter out; // Prints out to the Main Server
    private static Socket socket = null; // MAIN CONNECTION TO THE SERVER, NEEDS AN IP AND PORT TO KNOW WHERE TO CONNECT

    public static PrintWriter getOut() { //External access of Client's PrintWriter to Print to Main Server
        return out;
    }

    public static Socket getSocket() { //External access of Client's socket, mostly used for BufferedReader for reading messages from Server
        return socket;
    }

    public static ChatClient getInstance() throws IOException {
        if (instance == null) {
            instance = new ChatClient();
        }

        return instance;
    }

    public ChatClient() throws IOException {

        socket = new Socket(IP_ADDRESS, PORT);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        ChatClient.out = out;
        out.println(current_user.getUser_id()); // sends ID to the thing and waits for a code
    }
}
