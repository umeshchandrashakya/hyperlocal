package com.hyperlocal.app.ui.notification;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.hyperlocal.app.MySharedPreferences;
import com.hyperlocal.app.R;
import com.hyperlocal.app.model.OutStandingRequestModel;
import com.hyperlocal.app.ui.chats.ChatActivity;
import com.hyperlocal.app.utlity.Constants;

import java.util.Arrays;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author ${Umesh} on 16-05-2018.
 */

public class NotificationActivity extends AppCompatActivity implements NotificationView {
    @BindView(R.id.tv_ask)
    TextView tvAsk;
    @BindView(R.id.progressBar)
    FrameLayout progressBar;

    private MySharedPreferences sharedPreferences;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private String requestId;
    private String requestDeviceToken;
    private String devcieType;
    private String requestedUserId;
    private String message;
    private String requestType;
    private String users;
    private NotificationPresenter notificationPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        ButterKnife.bind(this);
        notificationPresenter = new NotificationPresenter(this, new NotificationFireBaseServcie(NotificationActivity.this));

        requestId = getIntent().getExtras().getString(Constants.REQUEST_ID);
        requestDeviceToken = getIntent().getExtras().getString(Constants.REQUEST_DEVICE_TOKEN);
        requestedUserId = getIntent().getExtras().getString(Constants.REQUESTED_USER_ID);
        message = getIntent().getExtras().getString(Constants.MESSAGE);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        sharedPreferences = MySharedPreferences.getInstance(this);
        devcieType = getIntent().getExtras().getString(Constants.DEVIcE_TYPE);
        requestType = getIntent().getExtras().getString(Constants.REQUEST_TYPE);
        users = getIntent().getExtras().getString(Constants.USERS);
        if(requestType!=null&&requestType.equals("give")){
            tvAsk.setText("One of your neighbors wants to give " + message);
        }else if(requestType!=null&&requestType.equals("ask")){
            tvAsk.setText("One of your neighbors is asking for " + message);
        }else if(requestType!=null&&requestType.equals("invite")){
            tvAsk.setText("One of your neighbors is invited you for " + message);
        }
        //tvAsk.setText("One of your neighbors is asking if you have " + sharedPreferences.readString(Constants.ASK_TEXT));

    }


    @OnClick(R.id.btn_yes)
    public void yesButtonClick() {
        progressBar.setVisibility(View.VISIBLE);
        NotificationAcceptRequestModel model = new NotificationAcceptRequestModel();
        model.setRequestId(requestId);
        model.setRequestDeviceToken(requestDeviceToken);
        model.setRequestedUserId(requestedUserId);
        model.setDevcieType(devcieType);
        model.setRequestType(requestType);
        model.setMessage(message);
        model.setUsers(users);
        checkRequestIsNotCancelled(model);
       // notificationPresenter.acceptRequestButtonClick(sharedPreferences.readString(Constants.USER_ID), model);


    }

    private void checkRequestIsNotCancelled(NotificationAcceptRequestModel model) {
        String  type = model.getRequestType();
        String nodeType = type.equals("ask")?"0":"1";
        DatabaseReference databaseReference = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("Outstanding")
                .child(model.getRequestedUserId())
                .child(nodeType);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                OutStandingRequestModel outStandingRequestModel = dataSnapshot.getValue(OutStandingRequestModel.class);
                if(outStandingRequestModel!=null&&outStandingRequestModel.getStatus().equals("1")){
                    Toast.makeText(NotificationActivity.this, "Request has been expired", Toast.LENGTH_SHORT).show();
                    finish();
                }else {
                    notificationPresenter.acceptRequestButtonClick(sharedPreferences.readString(Constants.USER_ID), model);

                }
                databaseReference.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                databaseReference.removeEventListener(this);
            }
        });

    }


    private void saveRequest() {
        if (requestType != null && requestType.equalsIgnoreCase("invite")) {
            updateInviteOnly();
        } else {
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mDatabaseReference =
                    mFirebaseDatabase.getReference()
                            .child("accepted_request")
                            .child(requestId);
            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Object key = dataSnapshot.getValue();
                    if (key == null) {
                        progressBar.setVisibility(View.GONE);
                        saveAcceptedRequest();
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(NotificationActivity.this, "Already Accepted by some one ", Toast.LENGTH_SHORT).show();
                    }
                    mDatabaseReference.removeEventListener(this);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    mDatabaseReference.removeEventListener(this);
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }


    private void updateInviteOnly() {
        String mUsers = users;
        if (mUsers != null) {
            String arr[] = mUsers.split(",");
            StringBuilder user2 = new StringBuilder();
            for (int i = 0; i < arr.length; i++) {
                if (!sharedPreferences.readString(Constants.USER_ID).equalsIgnoreCase(arr[i])) {
                    user2.append(arr[i]).append(",");
                }
            }
            HashMap hashMap = new HashMap();
            hashMap.put("users", user2.toString());
            DatabaseReference
                    databaseReference = FirebaseDatabase
                    .getInstance()
                    .getReference("Incoming")
                    .child(requestId);
            databaseReference.updateChildren(hashMap);
            createConversation();
        }
    }

    private void createConversation() {
        DatabaseReference databaseOutstanding = FirebaseDatabase.getInstance()
                .getReference("Conversation")
                .child(sharedPreferences.readString(Constants.USER_ID))
                .push();

        HashMap hashMap = new HashMap();
        hashMap.put("status", "0");
        hashMap.put("item", message);
        hashMap.put("toId", requestedUserId);
        hashMap.put("timestamp", ServerValue.TIMESTAMP);
        hashMap.put("fromId", sharedPreferences.readString(Constants.USER_ID));
        hashMap.put("requestType", "3");
        hashMap.put("itemKey", requestId);
        databaseOutstanding.setValue(hashMap);

        HashMap hashMap1 = new HashMap();
        hashMap1.put("status", "0");
        hashMap1.put("item", message);
        hashMap1.put("fromId", requestedUserId);
        hashMap1.put("timestamp", ServerValue.TIMESTAMP);
        hashMap1.put("toId", sharedPreferences.readString(Constants.USER_ID));
        hashMap1.put("requestType", "3");
        hashMap1.put("itemKey", requestId);

        DatabaseReference databaseOutstanding1 = FirebaseDatabase.getInstance()
                .getReference("Conversation")
                .child(requestedUserId)
                .push();
        databaseOutstanding1.setValue(hashMap1);
        insertDefaultChat(requestedUserId, sharedPreferences.readString(Constants.USER_ID), requestId);

    }

    private void insertDefaultChat(String requestedUserId, String myId, String requestId) {
        HashMap hashMap = new HashMap();
        hashMap.put("message", "Hello any one have " + message + " please provide me");
        hashMap.put("timestamp", ServerValue.TIMESTAMP);
        hashMap.put("fromId", requestedUserId);
        hashMap.put("toId", myId);
        hashMap.put("isBlocked", 0);
        String arr[] = {myId, requestedUserId};
        Arrays.sort(arr);
        DatabaseReference databaseReference = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("Chats")
                .child(arr[0] + "-" + arr[1])
                .child(requestId);
        databaseReference.child("Messages")
                .push().setValue(hashMap);

        HashMap hashMap1 = new HashMap();
        hashMap1.put("message", "your request accepted for " + message);
        hashMap1.put("timestamp", ServerValue.TIMESTAMP);
        hashMap1.put("fromId", myId);
        hashMap1.put("toId", requestedUserId);
        hashMap1.put("isBlocked", 0);
        databaseReference.child("Messages").push().setValue(hashMap1);
        startChatActivity();
    }


    public void saveAcceptedRequest() {
        mDatabaseReference = mFirebaseDatabase.getReference()
                .child("accepted_request")
                .child(requestId);
        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis()));
        hashMap.put("userId", sharedPreferences.readString(Constants.USER_ID));
        hashMap.put("device_token", requestDeviceToken);
        hashMap.put("device_type", devcieType);
        hashMap.put("type", requestType);
        hashMap.put("acceptedId", requestedUserId);
        mDatabaseReference.push().setValue(hashMap);
        updateOutStanding(requestedUserId);
    }

    private void updateOutStanding(String requestedUserId) {
        HashMap hashMap = new HashMap();
        hashMap.put("status", "1");
        createConversationNode();
    }


    private void createConversationNode() {
        DatabaseReference databaseOutstanding = mFirebaseDatabase
                .getReference("Conversation")
                .child(sharedPreferences.readString(Constants.USER_ID)).push();

        HashMap hashMap = new HashMap();
        hashMap.put("status", "0");
        hashMap.put("item", message);
        hashMap.put("toId", requestedUserId);
        hashMap.put("timestamp", ServerValue.TIMESTAMP);
        hashMap.put("fromId", sharedPreferences.readString(Constants.USER_ID));
        hashMap.put("itemKey", requestId);
        hashMap.put("requestType", "3");
        databaseOutstanding.setValue(hashMap);

        HashMap hashMap1 = new HashMap();
        hashMap1.put("status", "0");
        hashMap1.put("item", message);
        hashMap1.put("fromId", requestedUserId);
        hashMap1.put("timestamp", ServerValue.TIMESTAMP);
        hashMap1.put("toId", sharedPreferences.readString(Constants.USER_ID));
        hashMap1.put("requestType", "3");
        hashMap1.put("itemKey", requestId);

        DatabaseReference databaseOutstanding1 = mFirebaseDatabase
                .getReference("Conversation")
                .child(requestedUserId).push();
        databaseOutstanding1.setValue(hashMap1);

        HashMap hashMap2 = new HashMap();
        hashMap2.put("status", "1");


        if (requestType != null && requestType.equalsIgnoreCase("ask")) {
            DatabaseReference outStandingRef = FirebaseDatabase
                    .getInstance()
                    .getReference("Outstanding")
                    .child(requestedUserId)
                    .child("0");

            outStandingRef.updateChildren(hashMap2);
            updateIncomingNode();
        } else if (requestType.equalsIgnoreCase("give")) {
            DatabaseReference outStandingRef = FirebaseDatabase
                    .getInstance()
                    .getReference("Outstanding")
                    .child(requestedUserId)
                    .child("1");
            outStandingRef.updateChildren(hashMap2);
            updateIncomingNode();

        } else {

        }
        insertIntoChatDefoultMessage();
        startChatActivity();
        finish();
    }


    private void updateIncomingNode() {
        HashMap hashMap2 = new HashMap();
        hashMap2.put("status", "1");
        DatabaseReference databaseReference = FirebaseDatabase
                .getInstance()
                .getReference("Incoming")
                .child(requestId);
        databaseReference.updateChildren(hashMap2);
    }

    private void insertIntoChatDefoultMessage() {
        HashMap hashMap = new HashMap();
        hashMap.put("message", "i asked if you have " + message);
        hashMap.put("timestamp", ServerValue.TIMESTAMP);
        hashMap.put("fromId", requestedUserId);
        hashMap.put("toId", sharedPreferences.readString(Constants.USER_ID));
        hashMap.put("isBlocked", 0);
        String arr[] = {requestedUserId, sharedPreferences.readString(Constants.USER_ID)
        };
        Arrays.sort(arr);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference()
                .child("Chats").child(arr[0] + "-" + arr[1])
                .child(requestedUserId);
        databaseReference.child("Messages").push().setValue(hashMap);

        HashMap hashMap1 = new HashMap();
        hashMap1.put("message", "Your request accepted for " + message);
        hashMap1.put("timestamp", ServerValue.TIMESTAMP);
        hashMap1.put("fromId", sharedPreferences.readString(Constants.USER_ID));
        hashMap1.put("toId", requestedUserId);
        hashMap1.put("isBlocked", 0);
        databaseReference.child("Messages").push().setValue(hashMap1);
    }

    @Override
    public void startChatActivity() {
        Intent intent = new Intent(NotificationActivity.this, ChatActivity.class);
        intent.putExtra(Constants.TO_ID, requestedUserId);
        intent.putExtra(Constants.HAS_USER_BLOCK, 0);
        intent.putExtra(Constants.IS_USER_BLOCK, 0);
        intent.putExtra(Constants.ITEM_KEY, requestId);
        intent.putExtra(Constants.STATUS,"0");
        intent.putExtra(Constants.FROM_ID, sharedPreferences.readString(Constants.USER_ID));
        startActivity(intent);
    }

    @OnClick(R.id.btn_no)
    public void noButtonClick() {
        finish();
    }

    @Override
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showSuccesMessage(String message) {
        Toast.makeText(this, "" + message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showErrorMessage(String message) {
        Toast.makeText(this, "" + message, Toast.LENGTH_SHORT).show();
    }


}
