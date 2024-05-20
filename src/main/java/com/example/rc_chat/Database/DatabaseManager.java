package com.example.rc_chat.Database;

import java.sql.*;

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

    User currentUser = User.getInstance();
    private void initializeDB(){
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
                    "password VARCHAR (50) NOT NULL)";
            stmt.execute(createTblUsersQuery);

            String createTblChatRoomQuery = "CREATE TABLE IF NOT EXISTS tblChatroom (" +
                    "room_id INT AUTO_INCREMENT PRIMARY KEY)";
            stmt.execute(createTblChatRoomQuery);

            String createTblChatMessage = "CREATE TABLE IF NOT EXISTS tblChatMessage (" +
                    "chat_id INT AUTO_INCREMENT PRIMARY KEY," +
                    "room_id INT NOT NULL," +
                    "sender_id INT NOT NULL," +
                    "message VARCHAR (250) NOT NULL," +
                    "FOREIGN KEY (room_id) REFERENCES tblChatRoom (room_id) ON DELETE CASCADE," +
                    "FOREIGN KEY (sender_id) REFERENCES tblUser (user_id) ON DELETE CASCADE )";
            stmt.execute(createTblChatMessage);
            c.commit();
            System.out.println("Database with TABLES created successfully.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
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

            searchUser.setString(1, username);
            searchUser.setString(2, password);

            ResultSet resSearch = searchUser.executeQuery();

            if (resSearch.next()){
                return dbStatus.REGISTER_USER_EXISTS;
            }

            insertUser.setString(1, username);
            insertUser.setString(2, password);

            int regRes = insertUser.executeUpdate();

            if (regRes == 1){
                return dbStatus.REGISTER_SUCCESS;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dbStatus.REGISTER_ERROR;
    }

    public dbStatus logUser(String username, String password) {
        try (Connection c = SQLConnection.getConnection();
             PreparedStatement stmt = c.prepareStatement(
                     "SELECT * FROM tblUser WHERE username=? AND password=?"
             )) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet res = stmt.executeQuery();

            if (res.next()){
                currentUser.setUser_id(res.getInt("user_id"));
                currentUser.setUsername(res.getString("username"));
                currentUser.setPassword(res.getString("password"));

                return dbStatus.LOGIN_SUCCESS;
            }
        } catch (SQLException e) {
            return dbStatus.LOGIN_ERROR;
        }

        return dbStatus.LOGIN_USER_NOT_FOUND;
    }
}
