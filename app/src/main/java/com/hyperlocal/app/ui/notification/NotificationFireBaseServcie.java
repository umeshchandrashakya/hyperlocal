package com.hyperlocal.app.ui.notification;
import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import java.util.Arrays;
import java.util.HashMap;

/**
 * @author ${Umesh} on 04-07-2018.
 */

public class NotificationFireBaseServcie {
    private String userId;
    private NotificationAcceptRequestModel model;
    private CallBack callBack;


    public NotificationFireBaseServcie(Context context){

    }

    public void acceptButtonClick(String userId, NotificationAcceptRequestModel model, CallBack callBack, Context context) {
        this.userId = userId;
        this.model = model;
        this.callBack = callBack;
        if (model.getRequestType().equalsIgnoreCase("invite")) {
            updateInviteOnly(model);
        } else {
            updateNodeWithAcceptedRequest(model);
        }
    }

    private void updateNodeWithAcceptedRequest(NotificationAcceptRequestModel model) {
        DatabaseReference  mDatabaseReference = FirebaseDatabase.getInstance().getReference()
                        .child("accepted_request")
                        .child(model.getRequestId());
      mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {
              Object key = dataSnapshot.getValue();
              if (key == null) {
                  saveAcceptedRequest(model);
              } else {
                    callBack.onSuccess("0");
                    callBack.showMessage("Request already accepted");
              }
          }

          @Override
          public void onCancelled(DatabaseError databaseError) {
                callBack.onSuccess("0");
          }
      });
    }

    private void saveAcceptedRequest(NotificationAcceptRequestModel model) {
       DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference()
                .child("accepted_request")
                .child(model.getRequestId());
        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis()));
        hashMap.put("userId", userId);
        hashMap.put("device_token", model.getRequestDeviceToken());
        hashMap.put("device_type", model.getDevcieType());
        hashMap.put("type", model.getRequestType());
        hashMap.put("acceptedId", model.getRequestedUserId());
        mDatabaseReference.push().setValue(hashMap);
        updateOutStanding(model.getRequestedUserId(),model);
    }

    private void updateOutStanding(String requestedUserId,NotificationAcceptRequestModel model) {
        HashMap hashMap = new HashMap();
        hashMap.put("status", "1");
        if (model.getRequestType() != null && model.getRequestType().equalsIgnoreCase("ask")) {
            DatabaseReference outStandingRef = FirebaseDatabase
                    .getInstance()
                    .getReference("Outstanding")
                    .child(requestedUserId)
                    .child("0");
            outStandingRef.updateChildren(hashMap);
            updateIncomingNode();
            createConversationNodeWithFriendId(model);
        } else if (model.getRequestType().equalsIgnoreCase("give")) {
            DatabaseReference outStandingRef = FirebaseDatabase
                    .getInstance()
                    .getReference("Outstanding")
                    .child(requestedUserId)
                    .child("1");
            outStandingRef.updateChildren(hashMap);
            updateIncomingNode();
            createConversationNodeWithFriendId(model);
        }
    }


    private void updateIncomingNode() {
        HashMap hashMap2 = new HashMap();
        hashMap2.put("status", "1");
        DatabaseReference databaseReference = FirebaseDatabase
                .getInstance()
                .getReference("Incoming")
                .child(model.getRequestId());
        databaseReference.updateChildren(hashMap2);
    }


    private void updateInviteOnly(NotificationAcceptRequestModel model) {
        String users = model.getUsers();
        if (users != null) {
            users = removeMyUserId(users, userId);
        }
        HashMap hashMap = new HashMap();
        hashMap.put("users", users);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("Incoming")
                .child(model.getRequestId());
        databaseReference.updateChildren(hashMap);
        //createConversationNodeWithMyId(model);
        createConversationNodeWithFriendId(model);

    }



    private void createChatNode(NotificationAcceptRequestModel model) {
         String arr[] = {userId,model.getRequestedUserId()};
        Arrays.sort(arr);

        HashMap hashMap = new HashMap();
        hashMap.put("message","Hello any one have "+ model.getMessage()+" please provide me");
        hashMap.put("timestamp", ServerValue.TIMESTAMP);
        hashMap.put("fromId", model.getRequestedUserId());
        hashMap.put("toId", userId);
        hashMap.put("isBlocked", 0);

        DatabaseReference databaseReference = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("Chats")
                .child(arr[0] + "-" + arr[1])
                .child(model.getRequestId());

        databaseReference.child("Messages").push().setValue(hashMap);

        HashMap hashMap1 = new HashMap();
        hashMap1.put("message", "your request accepted for "+model.getMessage());
        hashMap1.put("timestamp", ServerValue.TIMESTAMP);
        hashMap1.put("fromId", userId);
        hashMap1.put("toId", model.getRequestedUserId());
        hashMap1.put("isBlocked", 0);

        databaseReference.child("Messages").push().setValue(hashMap1);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                callBack.onSuccess("1");
                databaseReference.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
              callBack.onSuccess("0");
              callBack.OnError("Something went wrong");
            }
        });

    }

    private void createConversationNodeWithFriendId(NotificationAcceptRequestModel model) {
        DatabaseReference conversation = FirebaseDatabase.getInstance()
                .getReference("Conversation")
                .child(model.getRequestedUserId()).push();
        String pushKey = conversation.getKey();


        HashMap hashMap = new HashMap();
        hashMap.put("status", "0");
        hashMap.put("item", model.getMessage());
        hashMap.put("toId", userId);
        hashMap.put("timestamp", ServerValue.TIMESTAMP);
        hashMap.put("fromId", model.getRequestedUserId());
        hashMap.put("requestType", "3");
        hashMap.put("itemKey", model.getRequestId());
        conversation.setValue(hashMap);
        createConversationNodeWithMyId(model,pushKey);
    }

    private void createConversationNodeWithMyId(NotificationAcceptRequestModel model, String pushKey) {
        DatabaseReference conversation = FirebaseDatabase.getInstance()
                .getReference("Conversation")
                .child(userId)
                .child(pushKey);
        HashMap hashMap = new HashMap();
        hashMap.put("status", "0");
        hashMap.put("item", model.getMessage());
        hashMap.put("toId", model.getRequestedUserId());
        hashMap.put("timestamp", ServerValue.TIMESTAMP);
        hashMap.put("fromId", userId);
        hashMap.put("requestType", "3");
        hashMap.put("itemKey", model.getRequestId());
        conversation.setValue(hashMap);
        createChatNode(model);
    }


    private String removeMyUserId(String users, String userId) {
        String arr[] = users.split(",");
        StringBuilder user2 = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            if (!this.userId.equalsIgnoreCase(arr[i])) {
                user2.append(arr[i]).append(",");
            }
        }
        return user2.toString();
    }


    public interface CallBack {
        void onSuccess(String result);
        void showMessage(String message);
        void OnError(String error);
    }
}
