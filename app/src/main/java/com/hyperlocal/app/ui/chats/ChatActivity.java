package com.hyperlocal.app.ui.chats;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.hyperlocal.app.MySharedPreferences;
import com.hyperlocal.app.R;
import com.hyperlocal.app.ui.home.HomeActivity;
import com.hyperlocal.app.utlity.Constants;
import com.hyperlocal.app.utlity.FunctionUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;



/**
 * @author ${Umesh} on 23-05-2018.
 */

public class ChatActivity extends AppCompatActivity {
    @BindView(R.id.list_view) ListView listView;
    @BindView(R.id.message_edit) EditText messageEditText;
    @BindView(R.id.btn_send)ImageView btnSend;
    @BindView(R.id.relative_layout)RelativeLayout relativeLayoutWithEditText;
    @BindView(R.id.hidden_relative)RelativeLayout relativeLayoutWithTextView;
    @BindView(R.id.progressBar)FrameLayout progressBarLayout;
    @BindView(R.id.toolbar_header)TextView toolBarHeader;

    private ChatArrayAdapter chatArrayAdapter;
    private List<ChatMessage> chatMessageList = new ArrayList<ChatMessage>();
    private String userId;
    private MySharedPreferences sharedPreferences;

    DatabaseReference databaseReference;
    String to_id;
    String fromId;
    private int unReadCount = 0;
    private ValueEventListener valueEventListener;
    private ChildEventListener childEventListener;
    private boolean isOnline;
    private int isBlocked;
    private int hasBlocked;
    private Menu optionMenu;
    String to_id1;
    String fromId1;
    String itemKey;
    private String status;
    private String firendName;
    Toolbar toolbar;
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        sharedPreferences = MySharedPreferences.getInstance(ChatActivity.this);
        userId = sharedPreferences.readString(Constants.USER_ID);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().openOptionsMenu();
        }

        initializeItems();
        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.right_row);
        listView.setAdapter(chatArrayAdapter);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

    }

    private void initializeItems() {
        if (getIntent().getExtras() != null) {
            to_id1 = getIntent().getExtras().getString(Constants.TO_ID);
            fromId1 = sharedPreferences.readString(Constants.USER_ID);
            itemKey = getIntent().getExtras().getString(Constants.ITEM_KEY);

            getPartnerName(to_id1);

            isBlocked = getIntent().getExtras().getInt(Constants.IS_USER_BLOCK);
            hasBlocked = getIntent().getExtras().getInt(Constants.HAS_USER_BLOCK);
            status = getIntent().getExtras().getString(Constants.STATUS);
            if(status.equalsIgnoreCase("2")){
               relativeLayoutWithEditText.setVisibility(View.GONE);
               relativeLayoutWithTextView.setVisibility(View.VISIBLE);
            }else {
                relativeLayoutWithEditText.setVisibility(View.VISIBLE);
                relativeLayoutWithTextView.setVisibility(View.GONE);
            }

            String arr[] = {fromId1, to_id1};
            Arrays.sort(arr);
            fromId = arr[0];
            to_id = arr[1];

            databaseReference = FirebaseDatabase.getInstance().getReference().child("Chats").child(arr[0] + "-" + arr[1]).child(itemKey);
            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance()
                    .getReference().child("Chats")
                    .child(arr[0] + "-" + arr[1]).child(itemKey).child(fromId1 + "-Info");

            databaseReference1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    UnreadCountModel model = dataSnapshot.getValue(UnreadCountModel.class);
                    if (model != null) {
                        isBlocked = model.getIsBlocked();
                        hasBlocked = model.getHasBlocked();
                        updateMenuOptionText();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            loadMessages(arr[0], arr[1]);
        }

    }

    private void getPartnerName(String to_id1) {
        DatabaseReference databaseReference = FirebaseDatabase
                .getInstance()
                .getReference("users").child(to_id1).child("userName");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("User Info",""+dataSnapshot.getValue());
                firendName = (String)dataSnapshot.getValue();
                toolBarHeader.setText(firendName);
                databaseReference.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                databaseReference.removeEventListener(this);
            }
        });


    }


    @OnClick(R.id.btn_send)
    public void sendButtonClick() {
        String message = messageEditText.getText().toString();
        if (TextUtils.isEmpty(message)) {
            Toast.makeText(this, "Please enter message", Toast.LENGTH_SHORT).show();
        } else if (FunctionUtil.isConnectionAvailable(ChatActivity.this)) {
            if (hasBlocked == 1) {
                unBlockTheUser();
            } else {
                    sendMessage(message);
                    messageEditText.setText("");
            }
        } else {
            showToast();
        }
    }

    private void conversationHasRemovedBySomeOne() {
        Toast.makeText(this, "You can not send message because conversation have been blocked forever by that user", Toast.LENGTH_SHORT).show();
    }


    private void unBlockTheUser() {
        final Dialog dialog = new Dialog(ChatActivity.this);
        dialog.setCancelable(false);
        dialog.setTitle("Hyperlocal");
        dialog.setContentView(R.layout.custom_aleart_dialog);
        Button dialogButtonYes = dialog.findViewById(R.id.btn_yes);
        Button dialogButtonNo = dialog.findViewById(R.id.btn_cancel);
        TextView alertSubTextView = dialog.findViewById(R.id.sub_title);
        alertSubTextView.setText("Do you want to unblock the user to send messages?");
        dialogButtonYes.setOnClickListener((View v) -> {
            progressBarLayout.setVisibility(View.VISIBLE);
            String arr[] = {fromId1, to_id1};
            Arrays.sort(arr);
            HashMap hashMap = new HashMap();
            hashMap.put("isBlocked", 0);
            databaseReference.child(to_id1 + "-Info").updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    progressBarLayout.setVisibility(View.GONE);
                }
            });
            HashMap hashMap1 = new HashMap();
            hashMap1.put("hasBlocked", 0);
            databaseReference.child(fromId1 + "-Info").updateChildren(hashMap1);
            hasBlocked = 0;
            isBlocked = 0;
            updateMenuOptionText();
            dialog.dismiss();
        });
        dialog.show();
        dialogButtonNo.setOnClickListener(view -> dialog.dismiss());
    }


    private void sendMessage(String message) {
        HashMap hashMap = new HashMap();
        hashMap.put("message", message);
        hashMap.put("timestamp", ServerValue.TIMESTAMP);
        hashMap.put("fromId", userId);
        hashMap.put("toId", to_id1);
        hashMap.put("isBlocked", isBlocked);
        databaseReference.child("Messages").push().setValue(hashMap);
        if (isBlocked != 1) {
            saveUnreadCount();
        }
    }


    private void saveUnreadCount() {
        HashMap hashMap = new HashMap<>();
        hashMap.put("unreadMessageCount", unReadCount + 1);
        databaseReference.child(to_id1 + "-Info").updateChildren(hashMap);
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    UnreadCountModel unreadCountModel = dataSnapshot.getValue(UnreadCountModel.class);
                    if (unreadCountModel != null) {
                        unReadCount = unreadCountModel.getUnreadMessageCount();
                        databaseReference.removeEventListener(this);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReference.child(to_id1 + "-Info").addValueEventListener(valueEventListener);
    }


    @Override
    protected void onStart() {
        super.onStart();
        isOnline = true;
        updateUnreadCount();
    }

    private void updateUnreadCount() {
        HashMap hashMap = new HashMap<>();
        hashMap.put("unreadMessageCount", 0);
        if(databaseReference!=null){
            databaseReference.child(fromId1 + "-Info").updateChildren(hashMap);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        isOnline = false;
        if (childEventListener != null && databaseReference != null) {
            databaseReference.removeEventListener(childEventListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        isOnline = false;
    }


    private void updateMenuOptionText() {
        if(optionMenu!=null){
            MenuItem item = optionMenu.findItem(R.id.block_user);
            if (hasBlocked == 1&&item!=null) {
                item.setTitle("Unblocked");
            } else if(item!=null){
                item.setTitle("Block");
            }
        }

    }

    private void loadMessages(String s, String s1) {
        progressBarLayout.setVisibility(View.VISIBLE);
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                progressBarLayout.setVisibility(View.GONE);
                if (dataSnapshot != null) {
                    ChatModel map = dataSnapshot.getValue(ChatModel.class);
                    if (map != null) {
                        String message = map.getMessage();
                        String messageUserId = map.getFromId();
                        long time = map.getTimestamp();
                        int isUserBlocked = map.getIsBlocked();
                        if (messageUserId != null && messageUserId.equalsIgnoreCase(fromId1)) {
                            addMessageBox("umesh", "" + time, message, true);
                        } else if (isUserBlocked == 1) {
                            HashMap hashMap = new HashMap<>();
                            hashMap.put("unreadMessageCount", 0);
                            if (isOnline) {
                                databaseReference.child(fromId1 + "-Info").updateChildren(hashMap);
                            }
                        } else {
                            addMessageBox("umesh", "" + time, message, false);
                            HashMap hashMap = new HashMap<>();
                            hashMap.put("unreadMessageCount", 0);
                            if (isOnline) {
                                databaseReference.child(fromId1 + "-Info").updateChildren(hashMap);
                            }
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                progressBarLayout.setVisibility(View.GONE);

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                progressBarLayout.setVisibility(View.GONE);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                progressBarLayout.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBarLayout.setVisibility(View.GONE);
            }
        };

        databaseReference.child("Messages").addChildEventListener(childEventListener);
        databaseReference.child(s1 + "-Info").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    UnreadCountModel model = dataSnapshot.getValue(UnreadCountModel.class);
                    if(model!=null){
                        unReadCount = model.getUnreadMessageCount();
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    public void addMessageBox(String userName, String time, String message, boolean b) {
        ChatMessage message1 = new ChatMessage(b, message, time, userName);
        chatArrayAdapter.add(message1);
        chatMessageList.add(message1);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.optionMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_menu, menu);
        updateMenuOptionText();
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.block_user:
                if (hasBlocked == 1) {
                    unBlockTheUser();
                } else {
                    showConfirmationDialog();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void showConfirmationDialog() {
        final Dialog dialog = new Dialog(ChatActivity.this);
        dialog.setCancelable(false);
        dialog.setTitle("Hyperlocal");
        dialog.setContentView(R.layout.custom_aleart_dialog);
        Button dialogButtonYes = dialog.findViewById(R.id.btn_yes);
        Button dialogButtonNo = dialog.findViewById(R.id.btn_cancel);
        TextView alertSubTextView = dialog.findViewById(R.id.sub_title);
        alertSubTextView.setText("Do you want to block the user?");
        dialogButtonYes.setOnClickListener((View v) -> {
            if (FunctionUtil.isConnectionAvailable(ChatActivity.this)) {
                blockCurrentPartnerFromChat();
                dialog.dismiss();
            } else {
                showToast();
            }

        });
        dialog.show();
        dialogButtonNo.setOnClickListener(view -> dialog.dismiss());
    }

    private void showToast() {
        Toast.makeText(this, "Please check your network connection!", Toast.LENGTH_SHORT).show();
    }

    private void blockCurrentPartnerFromChat() {
        progressBarLayout.setVisibility(View.VISIBLE);
        String arr[] = {fromId1, to_id1};
        Arrays.sort(arr);
        HashMap hashMap = new HashMap();
        hashMap.put("isBlocked", 1);
        databaseReference.child(to_id1 + "-Info").updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                progressBarLayout.setVisibility(View.GONE);
            }
        });
        HashMap hashMap1 = new HashMap();
        hashMap1.put("hasBlocked", 1);
        databaseReference.child(fromId1 + "-Info").updateChildren(hashMap1);
       // progressBarLayout.setVisibility(View.GONE);
        updateMenuOptionText();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(databaseReference!=null&&valueEventListener!=null){
          databaseReference.removeEventListener(valueEventListener);
        }
        Intent intent = new Intent(ChatActivity.this, HomeActivity.class);
        intent.putExtra(Constants.FROM_CHAT_ACTIVITY,"from_chat_activity");
        overridePendingTransition(R.anim.enter, R.anim.exit);
        startActivity(intent);
    }


}
