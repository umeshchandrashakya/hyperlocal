package com.hyperlocal.app.ui.notification;

/**
 * @author ${Umesh} on 04-07-2018.
 */

public class NotificationAcceptRequestModel {
        private String requestId;
    private String requestDeviceToken;
    private String requestedUserId;
    private String message;
    private String devcieType;
    private String requestType;
    private String users;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getRequestDeviceToken() {
        return requestDeviceToken;
    }

    public void setRequestDeviceToken(String requestDeviceToken) {
        this.requestDeviceToken = requestDeviceToken;
    }

    public String getRequestedUserId() {
        return requestedUserId;
    }

    public void setRequestedUserId(String requestedUserId) {
        this.requestedUserId = requestedUserId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDevcieType() {
        return devcieType;
    }

    public void setDevcieType(String devcieType) {
        this.devcieType = devcieType;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getUsers() {
        return users;
    }

    public void setUsers(String users) {
        this.users = users;
    }
}
