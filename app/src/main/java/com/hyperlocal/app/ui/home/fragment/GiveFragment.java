package com.hyperlocal.app.ui.home.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.hyperlocal.app.MySharedPreferences;
import com.hyperlocal.app.R;
import com.hyperlocal.app.model.AbuseWordsData;
import com.hyperlocal.app.model.OutStandingRequestModel;
import com.hyperlocal.app.utlity.Constants;
import com.hyperlocal.app.utlity.FunctionUtil;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static android.content.Context.MODE_PRIVATE;

/**
 * @Author ${Umesh} on 05-04-2018.
 */

public class GiveFragment extends Fragment {
    @BindView(R.id.linear_layout) LinearLayout linearLayout;
    @BindView(R.id.text_length) TextView countLengthOfChar;
    @BindView(R.id.edit_invite_neighbors) EditText giveEditText;
    @BindView(R.id.progressBar) FrameLayout progressBarLayout;
    MySharedPreferences sharedPreferences;
    FirebaseDatabase database;
    String uid;

    private DatabaseReference dbOutStandingRef;
    private ChildEventListener outStandingEventListener;

    private DatabaseReference dbInsertOutStanding;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.give_fragment, container, false);
        ButterKnife.bind(this, view);
        FunctionUtil.hideKeyboardOnOutSideTouch(view, getActivity());
        database = FirebaseDatabase.getInstance();
        sharedPreferences = MySharedPreferences.getInstance(getActivity());
        uid = sharedPreferences.readString(Constants.USER_ID);
        isTimerVisible();
        showValidationMessage();
        addOutStandingEventListener();
        return view;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(dbOutStandingRef!=null){
            dbOutStandingRef.removeEventListener(outStandingEventListener);
        }
    }

    private void addOutStandingEventListener() {
        dbOutStandingRef = FirebaseDatabase.getInstance().getReference("Outstanding")
                .child(uid);

        outStandingEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                OutStandingRequestModel outStandingRequestModel = dataSnapshot.getValue(OutStandingRequestModel.class);
                if (outStandingRequestModel != null && outStandingRequestModel.getStatus().equalsIgnoreCase("1")) {
                    sharedPreferences.writeBoolean(Constants.GIVE_AWAY_TIMER_VISIBLE, false);
                    sharedPreferences.writeLong(Constants.GIVE_AWAY_TIMER_TIME, 0);
                } else if (outStandingRequestModel != null && outStandingRequestModel.getType().equalsIgnoreCase("give")) {
                    long time = outStandingRequestModel.getTimeout() + outStandingRequestModel.getTimestamp();
                    if (time > 0) {
                        sharedPreferences.writeLong(Constants.GIVE_AWAY_TIMER_TIME, time);
                        sharedPreferences.writeBoolean(Constants.GIVE_AWAY_TIMER_VISIBLE, true);
                    } else {
                        sharedPreferences.writeBoolean(Constants.GIVE_AWAY_TIMER_VISIBLE, false);
                    }
                    sharedPreferences.writeLong(Constants.GIVE_AWAY_TIMER_TIME, time);
                } else {
                    long time = outStandingRequestModel.getTimeout() + outStandingRequestModel.getTimestamp();
                    if (time > 0) {
                        sharedPreferences.writeBoolean(Constants.TIMER_VISIBLE, true);
                    } else {
                        sharedPreferences.writeBoolean(Constants.TIMER_VISIBLE, false);
                    }
                    sharedPreferences.writeLong(Constants.TIMER_TIME, time);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }


    private void isTimerVisible() {
        boolean checkTimerStatus = sharedPreferences.readBoolean(Constants.GIVE_AWAY_TIMER_VISIBLE);
        if (checkTimerStatus) {
            replaceWithTimerFragment("");
        }

    }

    private void showValidationMessage() {
        RxTextView.afterTextChangeEvents(giveEditText)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(changeEvent -> {
                    if (changeEvent.view().getText().length() <= 0) {
                        countLengthOfChar.setText("0/120");
                    } else {
                        countLengthOfChar.setText(changeEvent.view().getText().length() + "/120");
                    }
                });
    }


    @OnClick(R.id.btn_next)
    public void nextButtonClick() {
        String strRequestText = giveEditText.getText().toString().trim();
        if (TextUtils.isEmpty(strRequestText)) {
            showSnackBar("Please enter the item first");
        } else if (AbuseWordsData.isAnyAbuseInText(strRequestText)) {
            showSnackBar("There are some vulgar words in your text please remove them");
        } else {
            progressBarLayout.setVisibility(View.VISIBLE);
            long totalRequestTime = convertMinutesIntoMilliSecond(1);
            sharedPreferences.writeLong(Constants.GIVE_AWAY_TIMER_TIME, totalRequestTime);
            sharedPreferences.writeBoolean(Constants.GIVE_AWAY_TIMER_VISIBLE, true);
            sharedPreferences.writeString(Constants.GIVE_AWAY_TIMER_TITLE, strRequestText);
            sharedPreferences.writeBoolean(Constants.GIVE_REQUEST_ACTIVE,false);
            SharedPreferences preferences = getActivity().getSharedPreferences("fcm_token_pref", MODE_PRIVATE);
            String fcmToke = preferences.getString(Constants.FCM_TOKEN, null);
            DatabaseReference databaseReference =
                    database.getReference("give_away")
                    .child(uid)
                    .push();

            sharedPreferences.writeString(Constants.Give_AWAY_REQUEST_KEY, databaseReference.getKey());
            HashMap hashMap = new HashMap<>();
            hashMap.put("item", strRequestText);
            hashMap.put("timestamp", ServerValue.TIMESTAMP);
            hashMap.put("timeout", totalRequestTime);
            hashMap.put("fromId", sharedPreferences.readString(Constants.USER_ID));
            hashMap.put("requestType", "0");
            hashMap.put("type", "give");
            hashMap.put("device_token", fcmToke);
            hashMap.put("device_type", "1");

            databaseReference.setValue(hashMap).addOnCompleteListener(task -> {
                progressBarLayout.setVisibility(View.GONE);
                insertAsOutStandingReq(strRequestText,totalRequestTime);
                replaceWithTimerFragment(strRequestText);
            });

        }
    }

    private void insertAsOutStandingReq(String strRequestText,long requestTime) {
        HashMap hashMap = new HashMap();
        hashMap.put("item", strRequestText);
        hashMap.put("requestType", "Out Standing Request");
        hashMap.put("status", "0");
        hashMap.put("timestamp", System.currentTimeMillis());
        hashMap.put("timeout", requestTime);
        hashMap.put("requestType", "0");
        hashMap.put("type", "give");

        DatabaseReference databaseOutstanding = FirebaseDatabase.
                getInstance()
                .getReference("Outstanding")
                .child(uid)
                .child("1");
        databaseOutstanding.setValue(hashMap);
    }

    private void replaceWithTimerFragment(String strRequestText) {
        giveEditText.setText("");
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TITLE, strRequestText);
        Fragment childFragment = new GiveAwayTimerFragment();
        childFragment.setArguments(bundle);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.addToBackStack("giveTimer");
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.left_to_right, R.anim.right_to_left);
        transaction.replace(R.id.container, childFragment).commit();
    }

    public long convertMinutesIntoMilliSecond(int day) {
        long timeInMaliSecond = day*86400000;
        sharedPreferences.writeLong(Constants.GIVE_AWAY_TIMER_TIME,System.currentTimeMillis()+timeInMaliSecond);
        return timeInMaliSecond;
    }

    private void showSnackBar(String message) {
        Snackbar.make(linearLayout, message, Snackbar.LENGTH_SHORT).show();
    }
}

