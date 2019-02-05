package com.hyperlocal.app.ui.home.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hyperlocal.app.MySharedPreferences;
import com.hyperlocal.app.R;
import com.hyperlocal.app.model.IncomingRequestModel;
import com.hyperlocal.app.ui.chats.ChatActivity;
import com.hyperlocal.app.utlity.Constants;
import com.hyperlocal.app.utlity.FunctionUtil;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author ${Umesh} on 18-04-2018.
 */

public class TimerFragment extends Fragment {
    @BindView(R.id.update_time_text_view) TextView upDateTimeTextView;
    @BindView(R.id.tv_item_text) TextView requestTextView;
    @BindView(R.id.btn_cancel)Button btnCancel;
    @BindView(R.id.btn_extend)TextView btnExtends;

    static final String[] values = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
    static final String[] unit = {"Minutes", "Hours", "Days"};
    private TextView mSelectedValue;
    private NumberPicker np;
    private NumberPicker np2;
    private MySharedPreferences preferences;
    private DatabaseReference databaseReference;
    private ChildEventListener childEventListener;
    CountDownTimer timer;
    private boolean isCancelClick;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.timer_fragment, container, false);
        ButterKnife.bind(this, view);
        preferences = MySharedPreferences.getInstance(getActivity());
        boolean isRequestActive = preferences.readBoolean(Constants.ASK_REQUEST_IS_ACTIVE);
        if(isRequestActive){
            btnCancel.setVisibility(View.VISIBLE);
            btnExtends.setVisibility(View.VISIBLE);
        }

        String requestText = preferences.readString(Constants.TIMER_TITLE);
        changeStringColor(requestText);
        startTimerTask();
        requestEnteredInIncoming();
        return view;
    }

    @OnClick(R.id.btn_cancel)
    public void cancelButtonClick() {
        showConfirmationDialog();
    }

    private void changeStringColor(String requestText) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        String red = "Your request for ";
        SpannableString redSpannable = new SpannableString(red);
        redSpannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.text_dark_gray)), 0, red.length(), 0);
        builder.append(redSpannable);
        String white = requestText;
        SpannableString whiteSpannable = new SpannableString(white);
        whiteSpannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)), 0, white.length(), 0);
        builder.append(whiteSpannable);
        String blue = " will be expired in ";
        SpannableString blueSpannable = new SpannableString(blue);
        blueSpannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.text_dark_gray)), 0, blue.length(), 0);
        builder.append(blueSpannable);
        requestTextView.setText(builder, TextView.BufferType.SPANNABLE);

    }

    @OnClick(R.id.btn_extend)
    public void extendButtonClick() {
        preferences.writeBoolean(Constants.TIMER_VISIBLE, false);
        preferences.writeLong(Constants.TIMER_TIME, 0);
        showTimePicker();
    }


    private void showTimePicker() {
        final Dialog dialog = new Dialog(getActivity());
        Window window = dialog.getWindow();
        if (window != null) {
            window.setGravity(Gravity.CENTER);
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            window.setBackgroundDrawableResource(android.R.color.background_light);
        }
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.time_picker);
        dialog.setCancelable(true);
        TextView b1 = dialog.findViewById(R.id.btn_done);
        mSelectedValue = dialog.findViewById(R.id.tv_selector);
        np = dialog.findViewById(R.id.numberPicker1);
        np2 = dialog.findViewById(R.id.numberPicker2);
        mSelectedValue.setText(values[np.getValue()]);
        np.setMaxValue(values.length - 1);
        np.setMinValue(0);
        np.setWrapSelectorWheel(false);
        np.setDisplayedValues(values);
        np.getDisplayedValues();
        np.setOnScrollListener((numberPicker, i) ->
                numberPicker.playSoundEffect(SoundEffectConstants.NAVIGATION_LEFT));
        np.setOnValueChangedListener((numberPicker, i, i1) ->
                mSelectedValue.setText(values[np.getValue()] + " " + unit[np2.getValue()]));

        b1.setOnClickListener(view -> {
            mSelectedValue.setText(values[np.getValue()]);
            dialog.dismiss();
            Toast.makeText(getActivity(), "" + values[np.getValue()], Toast.LENGTH_SHORT).show();
        });

        dialog.show();
        mSelectedValue.setText(values[np.getValue()] + " " + unit[np2.getValue()]);
        np2.setMaxValue(unit.length - 1);
        np2.setMinValue(0);
        np2.setWrapSelectorWheel(false);
        np2.setDisplayedValues(unit);
        np2.getDisplayedValues();
        np2.setOnScrollListener((numberPicker, i) ->
                numberPicker.playSoundEffect(SoundEffectConstants.NAVIGATION_LEFT));

        np2.setOnValueChangedListener((numberPicker, i, i1) ->
                mSelectedValue.setText(values[np.getValue()] + " " + unit[np2.getValue()]));

        b1.setOnClickListener(view -> {
            mSelectedValue.setText(values[np.getValue()]);
            dialog.dismiss();
            saveExtendTimeToPref(Integer.parseInt(values[np.getValue()]), unit[np2.getValue()]);
        });
    }


    private void startTimerTask() {
        MySharedPreferences preferences = MySharedPreferences.getInstance(getActivity());
        long mil = preferences.readLong(Constants.TIMER_TIME);
        long endTime = System.currentTimeMillis();
        long timeRemains = mil - endTime;
        if (timeRemains >= 0) {
            timer = new CountDownTimer(timeRemains, 1000) {
                public void onTick(long millisUntilFinished) {
                    upDateTimeTextView.setText(getFormattedTime(millisUntilFinished));
                }

                public void onFinish() {
                    upDateTimeTextView.setText("done!");
                    preferences.writeBoolean(Constants.TIMER_VISIBLE, false);
                    preferences.writeLong(Constants.TIMER_TIME, 0);
                    requestTimeExpireAlert();
                }
            }.start();
        } else {
            upDateTimeTextView.setText("done!");
            preferences.writeBoolean(Constants.TIMER_VISIBLE, false);
        }
    }



    public void goToAskFragment(){
        Fragment childFragment = new AskFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.left_to_right, R.anim.right_to_left);
        transaction.replace(R.id.linear_layout, childFragment).commit();
    }


    private void goToSparedTheWord() {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TITLE, "");
        Fragment childFragment = new SpreadTheWordFragment();
        childFragment.setArguments(bundle);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        //transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.left_to_right, R.anim.right_to_left);
        transaction.commitAllowingStateLoss();
        transaction.replace(R.id.linear_layout, childFragment);
    }


    public String getFormattedTime(long millis) {
        @SuppressLint("DefaultLocale")
        String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        return hms;
    }


    public void saveExtendTimeToPref(int timeDuration, String unit) {
        if (unit.equalsIgnoreCase("Minutes")) {
            long timeInMaliSecond = timeDuration * 60000;
            updateTimeInRequest(timeInMaliSecond);
            preferences.writeLong(Constants.TIMER_TIME, System.currentTimeMillis() + timeInMaliSecond);
            startTimerForNewTime();

        } else if (unit.equalsIgnoreCase("Hours")) {
            long timeInMaliSecond = timeDuration * 60000 * 60;
            updateTimeInRequest(timeInMaliSecond);
            preferences.writeLong(Constants.TIMER_TIME, System.currentTimeMillis() + timeInMaliSecond);
            startTimerForNewTime();
        } else {
            long timeInMaliSecond = timeDuration * 86400000;
            updateTimeInRequest(timeInMaliSecond);
            preferences.writeLong(Constants.TIMER_TIME, System.currentTimeMillis() + timeInMaliSecond);
            startTimerForNewTime();
        }
    }

    private void updateTimeInRequest(long timeInMaliSecond) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseOutstanding = database.getReference("Outstanding").child(preferences.readString(Constants.USER_ID)).child("0");
        HashMap hashMap = new HashMap();
        hashMap.put("timeout", timeInMaliSecond);
        hashMap.put("timestamp", System.currentTimeMillis());
        databaseOutstanding.updateChildren(hashMap);
        DatabaseReference databaseReference = database.getReference("Incoming").child(preferences.readString(Constants.ASK_REQUEST_KEY));
        databaseReference.updateChildren(hashMap);
    }


    private void startTimerForNewTime() {
        if (timer != null) {
            timer.cancel();
            startTimerTask();
        } else {
            startTimerTask();
        }
    }

    private void showConfirmationDialog() {
        if(getActivity()!=null){
            final Dialog dialog = new Dialog(getActivity());
            dialog.setCancelable(false);
            dialog.setTitle("Hyperlocal");
            dialog.setContentView(R.layout.custom_aleart_dialog);
            Button dialogButtonYes = dialog.findViewById(R.id.btn_yes);
            Button dialogButtonNo = dialog.findViewById(R.id.btn_cancel);
            TextView alertSubTextView = dialog.findViewById(R.id.sub_title);
            alertSubTextView.setText("are you  sure want to cancel the request?");
            dialogButtonYes.setOnClickListener((View v) -> {
                preferences.writeBoolean(Constants.TIMER_VISIBLE, false);
                preferences.writeLong(Constants.TIMER_TIME, 0);
                upDateTimeTextView.setText(getFormattedTime(0));
                dialog.dismiss();
                cancelRequest();
            });
            dialog.show();
            dialogButtonNo.setOnClickListener(view -> dialog.dismiss());
        }
    }

    public void requestEnteredInIncoming() {
        databaseReference = FirebaseDatabase
                .getInstance().getReference().child("Incoming")
                //.getReference("Incoming")
                .child(preferences.readString(Constants.ASK_REQUEST_KEY));

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                preferences.writeBoolean(Constants.ASK_REQUEST_IS_ACTIVE,true);
                btnCancel.setVisibility(View.VISIBLE);
                btnExtends.setVisibility(View.VISIBLE);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String key = dataSnapshot.getKey();
                if(!isCancelClick){
                    if(key!=null&&key.equals("status")){
                        String value = (String) dataSnapshot.getValue();
                        if(value!=null&& value.equals("1")){
                            preferences.writeBoolean(Constants.ASK_REQUEST_IS_ACTIVE,false);
                            btnCancel.setVisibility(View.INVISIBLE);
                            btnExtends.setVisibility(View.INVISIBLE);
                            showAlertForConfirmation();
                        }
                    }
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

       databaseReference.addChildEventListener(childEventListener);

    }

    private void showAlertForConfirmation() {
        if(getActivity()!=null){
            final Dialog dialog = new Dialog(getActivity());
            dialog.setCancelable(false);
            dialog.setTitle("Hyperlocal");
            dialog.setContentView(R.layout.custom_aleart_dialog);
            Button dialogButtonYes = dialog.findViewById(R.id.btn_yes);
            Button dialogButtonNo = dialog.findViewById(R.id.btn_cancel);
            dialogButtonNo.setVisibility(View.GONE);
            TextView alertSubTextView = dialog.findViewById(R.id.sub_title);
            alertSubTextView.setText("your request has been accepted by some one?");
            dialogButtonYes.setOnClickListener((View v) -> {
                preferences.writeBoolean(Constants.TIMER_VISIBLE, false);
                preferences.writeLong(Constants.TIMER_TIME, 0);
                upDateTimeTextView.setText(getFormattedTime(0));
                if(getActivity()!=null){
                    dialog.dismiss();
                    removeFragment();
                }
            });

            dialog.show();
            dialogButtonNo.setOnClickListener(view -> dialog.dismiss());
    }}

    private void removeFragment() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        for (Fragment frag : fm.getFragments()) {
            if (frag.isVisible()) {
                FragmentManager childFm = frag.getChildFragmentManager();
                if (childFm.getBackStackEntryCount() > 0) {
                    childFm.popBackStack();
                    return;
                }
            }
        }
    }

    public void requestTimeExpireAlert(){
        if(getActivity()!=null){
            final Dialog dialog = new Dialog(getActivity());
            dialog.setCancelable(false);
            dialog.setTitle("Hyperlocal");
            dialog.setContentView(R.layout.custom_aleart_dialog);
            Button dialogButtonYes = dialog.findViewById(R.id.btn_yes);
            Button dialogButtonNo = dialog.findViewById(R.id.btn_cancel);
            dialogButtonNo.setVisibility(View.GONE);
            TextView alertSubTextView = dialog.findViewById(R.id.sub_title);
            alertSubTextView.setText("your request has been expired. do you want to send new request?");
            dialogButtonYes.setOnClickListener((View v) -> {
                preferences.writeBoolean(Constants.TIMER_VISIBLE, false);
                preferences.writeLong(Constants.TIMER_TIME, 0);
                upDateTimeTextView.setText(getFormattedTime(0));
                goToSparedTheWord();
                dialog.dismiss();

            });
            dialog.show();
            dialogButtonNo.setOnClickListener(view -> dialog.dismiss());
        }
    }




    private void cancelRequest() {
        isCancelClick = true;
        String str = preferences.readString(Constants.ASK_REQUEST_KEY);
        DatabaseReference firebaseDatabase = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("Incoming")
                .child(str);
        HashMap hashMap = new HashMap();
        hashMap.put(Constants.STATUS, "1");
        hashMap.put(Constants.USERS, "");
        firebaseDatabase.updateChildren(hashMap);

        DatabaseReference firebaseDatabase1 = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("Outstanding").child(preferences.readString(Constants.USER_ID))
                .child("0");
        HashMap hashMap1 = new HashMap();
        hashMap1.put(Constants.STATUS, "1");
        firebaseDatabase1.updateChildren(hashMap1);
        goToAskFragment();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(databaseReference!=null){
            databaseReference.removeEventListener(childEventListener);
        }
    }
}
