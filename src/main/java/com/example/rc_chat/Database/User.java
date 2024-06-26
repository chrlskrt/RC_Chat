package com.example.rc_chat.Database;

public class User {
    private static User instance;
    private int user_id;
    private String username;
    private String password;
    public static User getInstance(){
        if (instance == null){
            instance = new User();
        }

        return instance;
    };

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}