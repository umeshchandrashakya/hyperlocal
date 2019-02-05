package com.hyperlocal.app.ui.home;


import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hyperlocal.app.MySharedPreferences;
import com.hyperlocal.app.R;
import com.hyperlocal.app.model.OutStandingRequestModel;
import com.hyperlocal.app.ui.BaseActivity;
import com.hyperlocal.app.ui.chats.ChatActivity;
import com.hyperlocal.app.ui.customview.MessageAlertDialog;
import com.hyperlocal.app.ui.home.adapter.SideMenuAdapter;
import com.hyperlocal.app.ui.home.fragment.AboutFragment;
import com.hyperlocal.app.ui.home.fragment.AbuseReportFragment;
import com.hyperlocal.app.ui.home.fragment.ActivityFragment;
import com.hyperlocal.app.ui.home.fragment.AskFragment;
import com.hyperlocal.app.ui.home.fragment.GiveAwayTimerFragment;
import com.hyperlocal.app.ui.home.fragment.GiveFragment;
import com.hyperlocal.app.ui.home.fragment.InviteFragment;
import com.hyperlocal.app.ui.home.fragment.SpreadTheWordFragment;
import com.hyperlocal.app.ui.home.fragment.TimerFragment;
import com.hyperlocal.app.ui.notification.NotificationAcceptRequestModel;
import com.hyperlocal.app.ui.notification.NotificationFireBaseServcie;
import com.hyperlocal.app.ui.notification.NotificationPresenter;
import com.hyperlocal.app.ui.notification.NotificationView;
import com.hyperlocal.app.ui.profile.ProfileFragment;
import com.hyperlocal.app.ui.registration.RegistrationActivity;
import com.hyperlocal.app.ui.registration.UserDataModel;
import com.hyperlocal.app.utlity.Constants;
import com.hyperlocal.app.utlity.FunctionUtil;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @Author ${Umesh} on 04-04-2018.
 */

public class HomeActivity extends BaseActivity implements SideMenuAdapter.SideMenuItemClickListener,NotificationView {

    private static final int PERMISSION_REQUEST_CODE = 100;

    @BindView(R.id.drawerLayout) DrawerLayout mDrawerLayout;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.my_recycler_view) RecyclerView sideMenuRecyclerView;
    @BindView(R.id.linear_layout) LinearLayout linearLayout;
    @BindView(R.id.tv_user_name)TextView textView;
    @BindView(R.id.tv_user_email)TextView tvEmailView;

    private EndDrawerToggle mEndDrawerToggle;
    private FragmentManager fragmentManager;
    private MessageAlertDialog dialog;
    private boolean isDialogVisible;
    private MySharedPreferences sharedPreferences;
    private FirebaseDatabase mFireBaseDataBase;
    private DatabaseReference mDatabaseReference;
    private String requestId;
    private String requestedDeviceToken;
    private String deviceType;
    private String requestedUserId;
    private String message;
    private boolean isHomeActivityVisible;
    private String requestType;
    private String users;
    DatabaseReference logoutRefernace;
    private DatabaseReference autoLogOutRef;
    private ValueEventListener autoLogoutValueEventListener;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
       // ScreenUtil.changeStatusBarColor(HomeActivity.this);

        sharedPreferences = MySharedPreferences.getInstance(this);
        textView.setText(sharedPreferences.readString(Constants.USER_NAME));
        tvEmailView.setText(sharedPreferences.readString(Constants.EMAIL_ID));

        FunctionUtil.hideKeyboardOnOutSideTouch(linearLayout, HomeActivity.this);
        setupDrawerToggle();
        setupRecyclerView();
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            String fromChat = intent.getExtras().getString(Constants.FROM_CHAT_ACTIVITY);
            if (fromChat.equalsIgnoreCase(Constants.FROM_CHAT_ACTIVITY)) {
                transaction.replace(R.id.container, new ActivityFragment()).commit();
                getUserCurrentLocation();
            } else {
                transaction.replace(R.id.container, new AskFragment()).commit();
                getUserCurrentLocation();
            }
        } else {
            transaction.replace(R.id.container, new AskFragment()).commit();
            getUserCurrentLocation();
        }
        logoutUserLoginInOtherDevice();
    }


    private void getUserCurrentLocation() {
        if (FunctionUtil.isLocationEnabled(HomeActivity.this)) {
            if (checkSelfPermission()) {

            }
        }
    }


    private void setupRecyclerView() {
        RecyclerView.LayoutManager manager = new LinearLayoutManager(HomeActivity.this);
        sideMenuRecyclerView.setLayoutManager(manager);
        SideMenuDataModel dataModel = new SideMenuDataModel();
        List<SideMenuItemModel> arrayList = dataModel.getArrayList();
        SideMenuAdapter adapter = new SideMenuAdapter(HomeActivity.this, arrayList, this);
        sideMenuRecyclerView.setAdapter(adapter);
    }


    public void closeDrawers() {
        mDrawerLayout.closeDrawers();
    }


    private void setupDrawerToggle() {
        mEndDrawerToggle = new EndDrawerToggle(this,
                mDrawerLayout,
                mToolbar,
                R.string.open,
                R.string.close);
        mDrawerLayout.addDrawerListener(mEndDrawerToggle);
        mDrawerLayout.setStatusBarBackground(R.color.bg_gray);
        mDrawerLayout.setScrimColor(getResources().getColor(android.R.color.transparent));
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mEndDrawerToggle.syncState();
    }



    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        for (Fragment frag : fm.getFragments()) {
            if (frag.isVisible()) {
                FragmentManager childFm = frag.getChildFragmentManager();
                if (childFm.getBackStackEntryCount() > 0) {
                    if (frag instanceof AskFragment) {
                        if (isDialogVisible) {
                            dialog.dismiss();
                        }
                       // finish();
                    } else {
                        for (Fragment ch : childFm.getFragments()) {
                            if (frag.isVisible()) {
                                FragmentManager ch1 = frag.getChildFragmentManager();
                                if (ch1.getBackStackEntryCount() > 0) {
                                    if( ch instanceof TimerFragment || ch instanceof GiveAwayTimerFragment){
                                        return;
                                    }else {
                                        ch1.popBackStack();
                                    }
                                    return;
                                }
                            }
                        }

                    }
                    return;
                }
            }
        }
    }


    @OnClick(R.id.btn_small)
    public void openProfilePage() {
        closeDrawers();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, new ProfileFragment()).commit();
    }



    @Override
    public void onSideMenuItemClicked(int position) {
        switch (position) {
            case 0:
                closeDrawers();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                //transaction.addToBackStack(null);
                transaction.replace(R.id.container, new AskFragment()).commit();
                break;
            case 1:
                closeDrawers();
                transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.container, new ActivityFragment()).commit();
                break;
            case 2:
                closeDrawers();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        transaction.replace(R.id.container, new InviteFragment(),"invite").commit();
                    }
                }, 300);

                break;
            case 3:
                closeDrawers();
                transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.container, new GiveFragment()).commit();
                break;
            case 4:
                closeDrawers();
                transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.container, new SpreadTheWordFragment()).commit();
                break;
            case 5:
                closeDrawers();
                transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.container, new AboutFragment()).commit();
                break;
            case 6:
                closeDrawers();
                transaction = fragmentManager.beginTransaction();
                // transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
                transaction.replace(R.id.container, new AbuseReportFragment()).commit();
                break;
            /*case 7:
                closeDrawers();
                logout();*/
        }
    }

    private void logout() {
        showAlertForConfirmation();

    }

    private void updateLogout() {
        HashMap hashMap = new HashMap();
        hashMap.put("loginStatus","0");
      //  hashMap.put("timestamp",0);
        logoutRefernace = FirebaseDatabase
                .getInstance()
                .getReference("users")
                .child(sharedPreferences.readString(Constants.USER_ID));
        logoutRefernace.updateChildren(hashMap);
        FirebaseAuth.getInstance().signOut();
        MySharedPreferences.getInstance(HomeActivity.this).clearPref();
      startActivity(new Intent(HomeActivity.this, RegistrationActivity.class));
    }


    @Override
    protected void onResume() {
        super.onResume();
        isHomeActivityVisible = true;
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("com.hyperlocal_FCM-Messages"));
    }


    @Override
    protected void onPause() {
        super.onPause();
        isHomeActivityVisible = false;
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }



    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int action_id = intent.getExtras().getInt("action_id");
            message = intent.getExtras().getString("message");
            requestId = intent.getExtras().getString("request_id");
            requestedUserId = intent.getExtras().getString("requestedUserId");
            requestType = intent.getExtras().getString(Constants.REQUEST_TYPE);
            String to_id = intent.getExtras().getString(Constants.TO_ID);
            sharedPreferences = MySharedPreferences.getInstance(HomeActivity.this);
            requestedDeviceToken = intent.getExtras().getString("requested_device_token");
            users = intent.getExtras().getString(Constants.USERS);
            deviceType = intent.getExtras().getString(Constants.DEVIcE_TYPE);

            NotificationAcceptRequestModel model = new NotificationAcceptRequestModel();
            model.setMessage(message);
            model.setRequestId(requestId);
            model.setRequestedUserId(requestedUserId);
            model.setRequestType(requestType);
            model.setRequestDeviceToken(requestedDeviceToken);
            model.setDevcieType(deviceType);
            model.setUsers(users);

            if (action_id == 2) {
                if (isHomeActivityVisible) {
                    showAlertDialogToChatScreen(message, requestId, to_id);
                } else {
                    createLocalNotification(message, requestId, to_id);
                }
            } else {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(isDialogVisible){
                            createLocalNotification(message, requestId, to_id);
                        }else {
                            showAlertDialog(message, action_id, requestId, to_id,model);
                        }
                    }
                }, 400);
            }
        }
    };


    private void createLocalNotification(String message, String requestId, String to_id) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("request_id", this.requestId);
        intent.putExtra(Constants.TO_ID, to_id);
        intent.putExtra(Constants.FROM_ID, sharedPreferences.readString(Constants.USER_ID));
        intent.putExtra(Constants.IS_USER_BLOCK,0);
        intent.putExtra(Constants.HAS_USER_BLOCK,0);
        intent.putExtra(Constants.ITEM_KEY,requestId);
        intent.putExtra(Constants.REQUEST_TYPE,requestType);
        intent.putExtra("requestedUserId", requestedUserId);
        intent.putExtra(Constants.USERS,users);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(intent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id));
        notificationBuilder.setSmallIcon(R.drawable.ic_launcher_app);
        notificationBuilder.setContentTitle("Congratulation");
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
        notificationManager.notify(0, notificationBuilder.build());
    }


    private void showAlertDialogToChatScreen(String message, String requestId, String to_id) {
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            Toast.makeText(this, "Your request has been accepted.", Toast.LENGTH_SHORT).show();
            hideProgressBar();
          /*  AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
            alertDialog.setTitle("Hyperlocal App");
            alertDialog.setMessage("Your request has been accepted.do you want to chat?");
            alertDialog.setPositiveButton("YES", (dialog, which) -> {
                dialog.cancel();
                sharedPreferences.writeBoolean(Constants.TIMER_VISIBLE, false);
                sharedPreferences.writeLong(Constants.TIMER_TIME, 0);
                //startChatActivity(to_id);

            });

            alertDialog.setNegativeButton("NO", (dialog, which) -> dialog.cancel());
            alertDialog.show();*/
        }, 400);
    }


    private void startChatActivity(String to_id) {
        Intent intent = new Intent(HomeActivity.this, ChatActivity.class);
        intent.putExtra(Constants.TO_ID, to_id);
        intent.putExtra(Constants.HAS_USER_BLOCK,0);
        intent.putExtra(Constants.IS_USER_BLOCK,0);
        intent.putExtra(Constants.ITEM_KEY,requestId);
        intent.putExtra(Constants.FROM_ID, sharedPreferences.readString(Constants.USER_ID));
        startActivity(intent);
    }


    private void showAlertDialog(String message, int action_id, String requestId, String requestID, NotificationAcceptRequestModel model) {
        if (isDialogVisible) {
            if (action_id == 1 && dialog != null) {
                isDialogVisible = false;
                dialog.dismiss();
            } else {
                isDialogVisible = true;
                dialog.setTitle(message);
                dialog.setYesButtonListener(listener);
                dialog.setNoButtonListener(listener);
                dialog.show();
            }

        } else {
            isDialogVisible = true;
            dialog = new MessageAlertDialog(this,model);
            dialog.setTitle(message);
            dialog.setYesButtonListener(listener);
            dialog.setNoButtonListener(listener);
            dialog.show();
        }
    }


    public MessageAlertDialog.DialogButtonClickListener listener = new MessageAlertDialog.DialogButtonClickListener() {

        @Override
        public void acceptRequestButtonClick(NotificationAcceptRequestModel model) {
            showProgressBar();
            isDialogVisible = true;
            FirebaseApp.initializeApp(HomeActivity.this);
            checkRequestStatus(model);

        }

        @Override
        public void cancelButtonClick() {
            dialog.dismiss();
            isDialogVisible = false;
            Toast.makeText(HomeActivity.this, "Request Rejected", Toast.LENGTH_SHORT).show();
        }
    };

    private void checkRequestStatus(NotificationAcceptRequestModel model) {
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
                    Toast.makeText(HomeActivity.this, "Request has been expired", Toast.LENGTH_SHORT).show();
                    hideProgressBar();
                    if(isDialogVisible){
                        dialog.dismiss();
                    }
                }else {
                    if(isDialogVisible){
                        dialog.dismiss();
                    }
                    NotificationPresenter presenter = new NotificationPresenter(HomeActivity.this,new NotificationFireBaseServcie(HomeActivity.this));
                    presenter.acceptRequestButtonClick(sharedPreferences.readString(Constants.USER_ID),model);
                    hideProgressBar();

                }
                databaseReference.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                databaseReference.removeEventListener(this);
                hideProgressBar();
            }
        });
    }


    public boolean checkSelfPermission() {
        if (ActivityCompat.checkSelfPermission(HomeActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(HomeActivity.this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            return false;
        } else {
            Log.e("DB", "PERMISSION GRANTED");
            return true;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
              /*  Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                addressEditText.setText(place.getAddress());
                addressEditText.setSelection(place.getAddress().length());*/
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {

            }
        }

        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if(fragment instanceof InviteFragment) {
                    fragment.onActivityResult(requestCode, resultCode, data);
                }
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        isHomeActivityVisible = true;
    }


    @Override
    public void showProgressBar() {
       if(dialog!=null){
          dialog.showProgressBar();
       }
    }

    @Override
    public void hideProgressBar() {
        if(dialog!=null){
            dialog.hideProgressBar();
        }
    }

    @Override
    public void showSuccesMessage(String message) {
        Toast.makeText(this, ""+message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showErrorMessage(String message) {
        Toast.makeText(this, ""+message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void startChatActivity() {
        Intent intent = new Intent(HomeActivity.this, ChatActivity.class);
        intent.putExtra(Constants.TO_ID, requestedUserId);
        intent.putExtra(Constants.HAS_USER_BLOCK, 0);
        intent.putExtra(Constants.IS_USER_BLOCK, 0);
        intent.putExtra(Constants.ITEM_KEY, requestId);
        intent.putExtra(Constants.STATUS,"0");
        intent.putExtra(Constants.FROM_ID, sharedPreferences.readString(Constants.USER_ID));
        startActivity(intent);
    }

    private void showAlertForConfirmation() {
        final Dialog dialog = new Dialog(HomeActivity.this);
            dialog.setCancelable(false);
            dialog.setTitle("Hyperlocal");
            dialog.setContentView(R.layout.custom_aleart_dialog);
            Button dialogButtonYes = dialog.findViewById(R.id.btn_yes);
            Button dialogButtonNo = dialog.findViewById(R.id.btn_cancel);
            dialogButtonNo.setVisibility(View.VISIBLE);
            TextView alertSubTextView = dialog.findViewById(R.id.sub_title);
            alertSubTextView.setText("Are you sure want to logout?");
            dialogButtonYes.setOnClickListener((View v) -> {
                updateLogout();
                dialog.dismiss();

            });

            dialog.show();
            dialogButtonNo.setOnClickListener(view -> dialog.dismiss());
        }

    @Override
    public void locationUpdated(Location location) {
        super.locationUpdated(location);
    }


    public void logoutUserLoginInOtherDevice(){
        autoLogOutRef = FirebaseDatabase
                .getInstance()
                .getReference("users")
                .child(sharedPreferences.readString(Constants.USER_ID))
                .child("timestamp");

        autoLogoutValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long timeStamp = sharedPreferences.readLong(Constants.TIME_STAMP);
                if(dataSnapshot!=null){
                    long timeStamp1 = (long) dataSnapshot.getValue(Long.class);

                    if (timeStamp1 == timeStamp) {
                    } else {
                        sharedPreferences.writeUserDataModel(Constants.USER_DATA_MODEL,new UserDataModel());
                        startActivity(new Intent(HomeActivity.this, RegistrationActivity.class));
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        autoLogOutRef.addValueEventListener(autoLogoutValueEventListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(autoLogOutRef!=null){
            autoLogOutRef.removeEventListener(autoLogoutValueEventListener);
        }
    }
}


