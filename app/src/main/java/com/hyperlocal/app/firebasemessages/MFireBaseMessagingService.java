package com.hyperlocal.app.firebasemessages;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.hyperlocal.app.MySharedPreferences;
import com.hyperlocal.app.R;
import com.hyperlocal.app.ui.chats.ChatActivity;
import com.hyperlocal.app.ui.notification.NotificationActivity;
import com.hyperlocal.app.utlity.Constants;
import com.hyperlocal.app.utlity.FunctionUtil;

/**
 * @author ${Umesh} on 08-05-2018.
 */

public class MFireBaseMessagingService extends FirebaseMessagingService {

    private LocalBroadcastManager localBroadcastManager;
    private String title;
    private String message;
    private String key;
    private String deviceToke;
    private int message_id;
    private String requestId;
    private String deviceType;
    private String requestedUserId;
    private String to_id;
    private String requestType;
    private String users;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getData().size() > 0) {
            localBroadcastManager = LocalBroadcastManager.getInstance(this);
            title = remoteMessage.getData().get(Constants.TITLE);
            message = remoteMessage.getData().get(Constants.BODY);
            key = remoteMessage.getData().get(Constants.IS_ACTION);
            deviceToke = remoteMessage.getData().get(Constants.REQUEST_DEVICE_TOKEN);
            message_id = Integer.parseInt(remoteMessage.getData().get(Constants.MESSAGE_ID));
            requestId = remoteMessage.getData().get(Constants.REQUEST_ID);
            deviceType = remoteMessage.getData().get(Constants.DEVIcE_TYPE);
            requestedUserId = remoteMessage.getData().get(Constants.REQUESTED_USER_ID);
            to_id = remoteMessage.getData().get(Constants.ACCEPTED_USER_ID);
            requestType = remoteMessage.getData().get(Constants.REQUEST_TYPE);
            users = remoteMessage.getData().get(Constants.USERS);
            MySharedPreferences preferences = MySharedPreferences.getInstance(this);
            preferences.writeString(Constants.ASK_TEXT, message);
            if (FunctionUtil.isAppIsInBackground(this)) {
                if (key.equalsIgnoreCase("1")) {
                    removeMessages(message_id);
                } else if (key.equalsIgnoreCase("2")) {
                    showRequestAcceptedMessage();
                } else {
                    sendMessages(title, message, message_id, requestId, deviceToke, deviceType, requestedUserId, requestType);
                }
            } else {
                openAlertDialog(message, Integer.parseInt(key), requestId, deviceToke, deviceType, requestedUserId, to_id, requestType);
            }

        }
    }


    private void showRequestAcceptedMessage() {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(Constants.REQUEST_ID, requestId);
        intent.putExtra(Constants.REQUEST_DEVICE_TOKEN, deviceToke);
        intent.putExtra(Constants.DEVIcE_TYPE, deviceType);
        intent.putExtra(Constants.REQUESTED_USER_ID, requestedUserId);
        intent.putExtra(Constants.REQUEST_TYPE, requestType);
        intent.putExtra(Constants.TO_ID, to_id);
        intent.putExtra(Constants.USERS,users);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(intent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id));
        notificationBuilder.setSmallIcon(R.drawable.ic_launcher_app);
        notificationBuilder.setContentTitle(title);
        notificationBuilder.setContentText(message);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setSound(uri);
        notificationBuilder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String CHANNEL_ID = "my_channel_01";// The id of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, "hyperlocal", importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(message_id, notificationBuilder.build());

    }


    public void sendMessages(String title, String messageBody, int message_id, String requestId, String deviceToken, String deviceType, String requestedUserId, String requestType) {
        Intent intent = new Intent(this, NotificationActivity.class);
        intent.putExtra(Constants.REQUEST_ID, requestId);
        intent.putExtra(Constants.REQUEST_DEVICE_TOKEN, deviceToken);
        intent.putExtra(Constants.DEVIcE_TYPE, deviceType);
        intent.putExtra(Constants.REQUESTED_USER_ID, requestedUserId);
        intent.putExtra(Constants.MESSAGE, messageBody);
        intent.putExtra(Constants.REQUEST_TYPE, requestType);
        intent.putExtra(Constants.USERS,users);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(intent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id));
        notificationBuilder.setSmallIcon(R.drawable.ic_launcher_app);
        notificationBuilder.setContentTitle(title);
        notificationBuilder.setContentText(messageBody);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setSound(uri);
        notificationBuilder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String CHANNEL_ID = "my_channel_01";// The id of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, "hyperlocal", importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(message_id, notificationBuilder.build());
    }


    private void removeMessages(int id) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(id);
    }


    private void openAlertDialog(String message, int id, String requestId, String deviceToke, String deviceType, String requestedUserId, String to_id, String requestType) {
        Intent intent = new Intent("com.hyperlocal_FCM-Messages");
        intent.putExtra(Constants.MESSAGE, message);
        intent.putExtra(Constants.ACTION_ID, id);
        intent.putExtra(Constants.REQUEST_ID, requestId);
        intent.putExtra(Constants.REQUEST_DEVICE_TOKEN, deviceToke);
        intent.putExtra(Constants.DEVIcE_TYPE, deviceType);
        intent.putExtra(Constants.REQUESTED_USER_ID, requestedUserId);
        intent.putExtra(Constants.REQUEST_TYPE, requestType);
        intent.putExtra(Constants.TO_ID, to_id);
        intent.putExtra(Constants.USERS, users);
        localBroadcastManager.sendBroadcast(intent);
    }

}
