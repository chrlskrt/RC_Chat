package com.example.rc_chat;

public class ChatMessage {
    int room_id;
    int sender_id;
    int chat_id;
    String message;

    public ChatMessage(int room_id, int sender_id, int chat_id, String message) {
        this.room_id = room_id;
        this.sender_id = sender_id;
        this.chat_id = chat_id;
        this.message = message;
    }

    public int getRoom_id() {
        return room_id;
    }

    public int getSender_id() {
        return sender_id;
    }

    public int getChat_id() {
        return chat_id;
    }

    public String getMessage() {
        return message;
    }
}
