package com.hyperlocal.app.ui.notification;


import android.content.Context;

/**
 * @author ${Umesh} on 04-07-2018.
 */

public class NotificationPresenter {

    private NotificationView notificationView;
    private NotificationFireBaseServcie servcie;
    private String userId;
    private Context context;


    public NotificationPresenter(NotificationView notificationView,NotificationFireBaseServcie servcie){
        this.notificationView = notificationView;
        this.servcie = servcie;
       // FirebaseAuth.getInstance();

    }

    public void acceptRequestButtonClick(String userId, NotificationAcceptRequestModel model) {
        this.userId = userId;
        servcie.acceptButtonClick(userId,model,callBack,context);
    }


    public NotificationFireBaseServcie.CallBack callBack = new NotificationFireBaseServcie.CallBack(){
        @Override
        public void onSuccess(String string) {
          if(string.equalsIgnoreCase("1")){
            notificationView.startChatActivity();
            notificationView.hideProgressBar();
            //return true;
          }else {
            //  return false;
          }

        }

        @Override
        public void showMessage(String message) {
            notificationView.showSuccesMessage(message);
            //return true;
        }

        @Override
        public void OnError(String error) {
          notificationView.showErrorMessage(error);
          //return true;
        }
    };
}
