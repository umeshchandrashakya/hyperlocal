package com.hyperlocal.app.ui.notification;

/**
 * @author ${Umesh} on 04-07-2018.
 */

public interface NotificationView {
    void showProgressBar();
    void hideProgressBar();
    void showSuccesMessage(String message);
    void showErrorMessage(String message);
    void startChatActivity();
}
