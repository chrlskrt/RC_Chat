package com.example.rc_chat.Database;

import com.example.rc_chat.ChatMessage;
import com.example.rc_chat.ChatRoom;
import com.example.rc_chat.Server.ChatClient;

import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;

import static com.example.rc_chat.RC_Chat.current_user;

public class DatabaseManager {
    private static DatabaseManager instance;
    private DatabaseManager(){
        initializeDB();
    }
    public static DatabaseManager getInstance(){
        if (instance == null){
            instance = new DatabaseManager();
        }

        return instance;
    }

    private void initializeDB() throws RuntimeException{
        Statement stmt;
        try (Connection c = SQLConnection.getConnection()){
            stmt = c.createStatement();

            String createDBQuery = "CREATE DATABASE IF NOT EXISTS dbRChat;";
            stmt.execute(createDBQuery);

            SQLConnection.DBName = "dbRChat";
            c.setCatalog("dbRChat");
            c.setAutoCommit(false);

            stmt = c.createStatement();

            String createTblUsersQuery = "CREATE TABLE IF NOT EXISTS tblUser (" +
                    "user_id INT AUTO_INCREMENT PRIMARY KEY," +
                    "username VARCHAR (50) NOT NULL," +
                    "password VARCHAR (100) NOT NULL)";
            stmt.execute(createTblUsersQuery);

            String createTblChatRoomQuery = "CREATE TABLE IF NOT EXISTS tblChatroom (" +
                    "room_id INT AUTO_INCREMENT PRIMARY KEY," +
                    "user_1 INT NOT NULL," +
                    "user_2 INT NOT NULL," +
                    "FOREIGN KEY (user_1) REFERENCES tblUser(user_id) ON DELETE CASCADE," +
                    "FOREIGN KEY (user_2) REFERENCES tblUser(user_id) ON DELETE CASCADE )";
            stmt.execute(createTblChatRoomQuery);

            String createTblChatMessage = "CREATE TABLE IF NOT EXISTS tblChatMessage (" +
                    "chat_id INT AUTO_INCREMENT PRIMARY KEY," +
                    "room_id INT NOT NULL," +
                    "sender_id INT NOT NULL," +
                    "message VARCHAR (250) NOT NULL," +
                    "FOREIGN KEY (room_id) REFERENCES tblChatroom(room_id) ON DELETE CASCADE," +
                    "FOREIGN KEY (sender_id) REFERENCES tblUser(user_id) ON DELETE CASCADE )";
            stmt.execute(createTblChatMessage);

            c.commit();
            System.out.println("Database with TABLES created successfully.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (NullPointerException n) {
            System.err.println("No SQL Connection detected");
        }
    }

    public dbStatus createUser(String username, String password) {
        try (Connection conn = SQLConnection.getConnection();
             PreparedStatement searchUser = conn.prepareStatement(
                     "SELECT user_id FROM tblUser WHERE username = ? AND password = ?"
             );
            PreparedStatement insertUser = conn.prepareStatement(
                    "INSERT INTO tblUser (username, password) VALUES (?, ?)"
            )) {

            String hash_pass = SHA256.toHexString(SHA256.getSHA(MD5_Hash.getMd5(password)));
            searchUser.setString(1, username);
            searchUser.setString(2, hash_pass);

            ResultSet resSearch = searchUser.executeQuery();

            if (resSearch.next()){
                return dbStatus.REGISTER_USER_EXISTS;
            }

            insertUser.setString(1, username);
            insertUser.setString(2, hash_pass);

            int regRes = insertUser.executeUpdate();

            if (regRes == 1){
                return dbStatus.REGISTER_SUCCESS;
            }
        } catch (SQLException e) {
            throw new RuntimeException();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        return dbStatus.REGISTER_FAILED;
    }

    public dbStatus logUser(String username, String password) {
        try (Connection c = SQLConnection.getConnection();
             PreparedStatement verifyUser = c.prepareStatement("SELECT username FROM tblUser WHERE username=?");
             PreparedStatement getUser = c.prepareStatement(
                     "SELECT user_id FROM tblUser WHERE username=? AND password=?"
             )) {

            verifyUser.setString(1, username);
            ResultSet vres = verifyUser.executeQuery();

            if (!vres.next()){
                return dbStatus.LOGIN_USER_NOT_FOUND;
            }

            String hash_pass = hashPassword(password);
            getUser.setString(1, username);
            getUser.setString(2, hash_pass);
            ResultSet res = getUser.executeQuery();

            if (!res.next()){
                return dbStatus.LOGIN_FAILED;
            }

            current_user.setUser_id(res.getInt("user_id"));
            current_user.setUsername(username);
            current_user.setPassword(hash_pass);
            ChatClient.getOut().println(current_user.getUser_id()); // sends ID to the thing and waits for a code

        } catch (SQLException e) {
            return dbStatus.LOGIN_ERROR;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        return dbStatus.LOGIN_SUCCESS;
    }

    private String hashPassword(String password) throws NoSuchAlgorithmException {
        return SHA256.toHexString(SHA256.getSHA(MD5_Hash.getMd5(password)));
    }

    public ArrayList<ChatMessage> getMessages (int room_id){
        ArrayList<ChatMessage> messages = new ArrayList<>();

        try (Connection conn = SQLConnection.getConnection();
            PreparedStatement getMessagesStmt = conn.prepareStatement(
                    "SELECT * FROM tblChatMessage WHERE room_id = ?"
            )) {

            getMessagesStmt.setInt(1, room_id);
            ResultSet res = getMessagesStmt.executeQuery();

            while (res.next()){
                messages.add(
                        new ChatMessage(
                                res.getInt("room_id"),
                                res.getInt("sender_id"),
                                res.getInt("chat_id"),
                                res.getString("message")
                        )
                );
            }
        } catch (SQLException e) {
            System.out.println("Error fetching messages");
            e.printStackTrace();
        }

        return messages;
    }

    public int createChatRoom(int clientId, int other_id) {
        try(Connection c = SQLConnection.getConnection();
            PreparedStatement p = c.prepareStatement("INSERT INTO tblChatroom(user_1, user_2) values(?, ?)", Statement.RETURN_GENERATED_KEYS);) {

            p.setInt(1, clientId);
            p.setInt(2, other_id);
            p.executeUpdate();
            ResultSet r = p.getGeneratedKeys();

            if (r.next()) {
                return r.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return 0;
    }

    public void saveMessage(int roomId, int userId, String message) {
        try(Connection c = SQLConnection.getConnection();
        PreparedStatement p = c.prepareStatement("INSERT INTO tblChatMessage(room_id, sender_id, message) values(?, ?, ?)", Statement.RETURN_GENERATED_KEYS);) {

            p.setInt(1, roomId);
            p.setInt(2, userId);
            p.setString(3, message);

            p.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<ChatRoom> getChatRooms(){
        ArrayList<ChatRoom> rooms = new ArrayList<>();

        try (Connection conn = SQLConnection.getConnection();
             Statement stmt = conn.createStatement();
            PreparedStatement pStmt = conn.prepareStatement(
                    "SELECT room_id, user_1, user_2 FROM tblChatroom " +
                            "WHERE user_1 = ? OR user_2 = ?"
            )) {
            pStmt.setInt(1, current_user.getUser_id());
            pStmt.setInt(2, current_user.getUser_id());

            ResultSet res = pStmt.executeQuery();
            while (res.next()){
                int room_id = res.getInt("room_id");
                int user1 = res.getInt("user_1");
                int user2 = res.getInt("user_2");

                int other_user = (user1 != current_user.getUser_id()) ? user1 : user2;
                String getOtherUser = "SELECT username FROM tblUser WHERE user_id = " + other_user;
                ResultSet res1 = stmt.executeQuery(getOtherUser);
                res1.next();
                String username = res1.getString("username");

                rooms.add(new ChatRoom(room_id, user1, user2).setOtherUser(username));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return rooms;
    }

    public ChatRoom getRoomInfo(int room_id){
        ChatRoom room = null;
        try (Connection conn = SQLConnection.getConnection();
             PreparedStatement getUser = conn.prepareStatement("SELECT username FROM tblUser WHERE user_id = ?");
             
             PreparedStatement pStmt = conn.prepareStatement("SELECT user_1, user_2 FROM tblChatRoom WHERE room_id = ?")
            ){

            pStmt.setInt(1, room_id);
            
            ResultSet res = pStmt.executeQuery();
            
            while (res.next()){
                int user_1 = res.getInt("user_1");
                int user_2 = res.getInt("user_2");
                
                assert user_1 != 0 && user_2 != 0;
                
                int other_user = (user_1 != current_user.getUser_id()) ? user_1 : user_2;
                getUser.setInt(1, other_user);
                
                ResultSet res2 = getUser.executeQuery();
                res2.next();
                
                String other_username = res2.getString("username");
                
                room = new ChatRoom(room_id, user_1, user_2).setOtherUser(other_username);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        
        return room;
    }

    public void updateUserDetails(String newusername, String newpassword) {
        try(Connection c = SQLConnection.getConnection();
            PreparedStatement stmt = c.prepareStatement("UPDATE tbluser SET username = ?, password = ? WHERE user_id = ?")) {

            if(newpassword.isEmpty()) {
                newpassword = current_user.getPassword();
            } else {
                newpassword = DatabaseManager.getInstance().hashPassword(newpassword);
            }

            stmt.setString(1,newusername);
            stmt.setString(2,newpassword);
            stmt.setInt(3,current_user.getUser_id());

            current_user.setUsername(newusername);
            current_user.setPassword(newpassword);

            stmt.executeUpdate();
            System.out.println("Edited real");
        }catch (SQLException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }
}
