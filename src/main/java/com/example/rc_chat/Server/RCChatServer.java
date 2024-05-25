package com.example.rc_chat.Server;

import com.example.rc_chat.Database.DatabaseManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;

public class RCChatServer {
    private static HashMap<Integer, Set<ClientHandler>> chatRooms = new HashMap<>();
    private static ArrayList<ClientHandler> waitingClients = new ArrayList<>();
    private static final int PORT = 1803;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) { // creates a new serverSocket for any clients to connect to

            System.out.println("SERVER ACTIVE. LISTENING TO PORT " + PORT);

            while (true) {
                Socket socket = serverSocket.accept(); //keeps on running, checking if any users connect
                new Thread(new ClientHandler(socket)).start(); // once connected, it accepts the socket and places it onto a ClientHandler
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static class ClientHandler extends Thread { // DEALS WITH ALL THE SERVER SIDE LOGIC FOR THE CLIENT
        private final Socket socket;
        private PrintWriter out;
        private int clientId;
        private int chatRoomId;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            ) {
                this.out = out;

                // we get this clientID first before checking codes
                clientId = Integer.parseInt(in.readLine());

                //waits for a command
                String code;
                while (true) {
                    code = in.readLine();
                    switch (code) {
                        case "4156": //start chatroom button
                            StartChat(in); // calls a function that deals with new chatrooms
                            break;
                        case "9104": //TODO: previous chat buttons
                            out.println("You selected 9104");
                            break;
                    }
                }
            } catch (SocketException s) {
                sendMessageToChatRoom(chatRoomId, clientId + " has disconnected");
            } catch (IOException | NumberFormatException e) {
                e.printStackTrace();
            } finally {
//              Cleanup when client disconnects
                synchronized (chatRooms) {
                    Set<ClientHandler> clients = chatRooms.get(chatRoomId); //gets clients of chatroom
                    if (clients != null) {
                        clients.remove(this); //removes himself from set of clients
                        if (clients.isEmpty()) {
                            chatRooms.remove(chatRoomId); //if empty, chatroom is removed
                        }
                    }
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void StartChat(BufferedReader in) {
            synchronized (waitingClients) {
                waitingClients.add(this); //adds this client to a waiting list
            }

            while (true) {
                Random r = new Random();
                int other_client = r.nextInt(waitingClients.size()); //finds a random index in waiting list
                synchronized (waitingClients) {
                    if (!waitingClients.contains(this)){ //THIS CHECKS IN CASE ANOTHER THREAD HAS ALREADY REMOVED THIS CLIENT FROM WAITING LIST
                        break;
                    } else if (waitingClients.get(other_client) != this) { //If it successfully finds another client
                        Set<ClientHandler> new_room_clients = new HashSet<>(); //places clients in a new Set of clients
                        new_room_clients.add(this);
                        new_room_clients.add(waitingClients.get(other_client));

                        synchronized (chatRooms) {
                            chatRoomId = DatabaseManager.getInstance().createChatRoom(clientId, waitingClients.get(other_client).clientId); //creates a new chatroom over onto the database and returns the ID
                            chatRooms.put(chatRoomId, new_room_clients);
                            waitingClients.get(other_client).out.println(chatRoomId); //sends message to other client that it has been connected to a chatroom
                            waitingClients.get(other_client).chatRoomId = chatRoomId; //gives the same chatRoomID to the other client
                            waitingClients.remove(other_client); //removes both clients from waiting list so that they don't get referenced again in another Thread (ie. another client)
                            waitingClients.remove(this);
                            System.out.println("new room created");
                            out.println(chatRoomId); //sends message to this client that it has been connected
                        }

                        break;
                    }
                }
            }

            try {
                String inputLine;
                while ((inputLine = in.readLine()) != null) { //waits for messages received from the client
                    if (inputLine.startsWith("8430!")) { //TODO: EXIT CODE THAT DISCONNECTS CLIENT BACK TO WAITING FOR CODE
                        return;
                    }

                    System.out.println(inputLine);
                    String[] message = inputLine.split("\\|", 2);
                    sendMessageToChatRoom(Integer.parseInt(message[0]), message[1] ); //any new messages will be processed in this function
                }
            } catch (SocketException s) {
                sendMessageToChatRoom(chatRoomId, clientId + " has disconnected");
            } catch (IOException | NumberFormatException e) {
                e.printStackTrace();
            } finally { // SEE LINE 67
                //          Cleanup when client disconnects
                synchronized (chatRooms) {
                    Set<ClientHandler> clients = chatRooms.get(chatRoomId);
                    if (clients != null) {
                        clients.remove(this);
                        if (clients.isEmpty()) {
                            chatRooms.remove(chatRoomId);
                        }
                    }
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void sendMessageToChatRoom(int chatRoomId, String message) {
            synchronized (chatRooms) {
                Set<ClientHandler> clients = chatRooms.get(chatRoomId); // gets the users of the current chatroom via chatroomID
                if (clients != null) {
                    for (ClientHandler client : clients) {
                        client.out.println(message); // outputs message to all clients in chatroom
                    }
                }
            }
        }
    }
}
