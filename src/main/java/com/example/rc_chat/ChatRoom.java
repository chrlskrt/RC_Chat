package com.example.rc_chat;

public class ChatRoom {
    int room_id;
    int user_1_id;
    int user_2_id;
    String other_user_name;
    public ChatRoom(int room_id, int user_1_id, int user_2_id) {
        this.room_id = room_id;
        this.user_1_id = user_1_id;
        this.user_2_id = user_2_id;
    }

    public int getRoom_id() {
        return room_id;
    }

    public int getUser_1_id() {
        return user_1_id;
    }

    public int getUser_2_id() {
        return user_2_id;
    }

    public ChatRoom setOtherUser(String username){
        other_user_name = username;
        return this;
    }

    public String getOtherUser() {
        return other_user_name;
    }
}