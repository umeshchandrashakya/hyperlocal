package com.hyperlocal.app.model;

/**
 * @author ${Umesh} on 18-04-2018.
 */

public class ActivityItemDataModel {
    private String item;
    private String requestType;
    private long timestamp;
    private String status;
    private String toId;
    private String unreadCount;
    private int isBlocked;
    private int hasBlocked;
    private String type;
    private String itemKey;
    private long timeout;
    private String request_id;
    private String device_token;
    private String device_type;
    private String users;
    private String itemPushKey;

    public String getItemPushKey() {
        return itemPushKey;
    }

    public void setItemPushKey(String itemPushKey) {
        this.itemPushKey = itemPushKey;
    }

    public String getUsers() {
        return users;
    }

    public void setUsers(String users) {
        this.users = users;
    }

    public String getDevice_type() {
        return device_type;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getItemKey() {
        return itemKey;
    }

    public void setItemKey(String itemKey) {
        this.itemKey = itemKey;
    }

    public int getIsBlocked() {
        return isBlocked;
    }

    public void setIsBlocked(int isBlocked) {
        this.isBlocked = isBlocked;
    }

    public int getHasBlocked() {
        return hasBlocked;
    }

    public void setHasBlocked(int hasBlocked) {
        this.hasBlocked = hasBlocked;
    }

    public String getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(String unreadCount) {
        this.unreadCount = unreadCount;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    private String fromId;



    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public ActivityItemDataModel(String item, String requestType, long timestamp, String status) {
        this.item = item;
        this.requestType = requestType;
        this.timestamp = timestamp;
        this.status = status;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ActivityItemDataModel(){

    }

    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof ActivityItemDataModel)) return false;
        ActivityItemDataModel o = (ActivityItemDataModel) obj;
        return o == this;
    }
}
