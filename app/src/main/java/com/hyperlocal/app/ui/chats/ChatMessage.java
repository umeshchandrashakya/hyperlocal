package com.hyperlocal.app.ui.chats;

/**
 * @author ${Umesh} on 27-10-2017.
 */

public class ChatMessage {
    public boolean left;
    public String message;
    public String userName;
    public String time;

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public ChatMessage(boolean left, String message, String time, String userName) {
        super();
        this.left = left;
        this.message = message;
        this.time = time;
        this.userName = userName;
    }
}
