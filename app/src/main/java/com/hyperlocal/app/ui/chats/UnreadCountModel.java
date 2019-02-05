package com.hyperlocal.app.ui.chats;

/**
 * @author ${Umesh} on 01-06-2018.
 */

public class UnreadCountModel {
    private int unreadMessageCount;
    private int isBlocked;
    private int hasBlocked;

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

    public int getUnreadMessageCount() {
        return unreadMessageCount;
    }

    public void setUnreadMessageCount(int unreadMessageCount) {
        this.unreadMessageCount = unreadMessageCount;
    }
}
