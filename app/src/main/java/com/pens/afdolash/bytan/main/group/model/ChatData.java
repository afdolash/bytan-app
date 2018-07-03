package com.pens.afdolash.bytan.main.group.model;

/**
 * Created by afdol on 5/24/2018.
 */

public class ChatData {
    public static final int CHAT_OWNER = 0;
    public static final int CHAT_MEMBER = 1;

    public int type;
    public String username;
    public String message;

    public ChatData(int type, String username, String message) {
        this.type = type;
        this.username = username;
        this.message = message;
    }

    public static int getChatOwner() {
        return CHAT_OWNER;
    }

    public static int getChatMember() {
        return CHAT_MEMBER;
    }

    public int getType() {
        return type;
    }

    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return message;
    }
}
