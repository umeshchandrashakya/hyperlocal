package com.hyperlocal.app.ui.home.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hyperlocal.app.MySharedPreferences;
import com.hyperlocal.app.R;
import com.hyperlocal.app.model.OutStandingRequestModel;
import com.hyperlocal.app.ui.home.presenter.AskRequestPresenter;
import com.hyperlocal.app.ui.home.presenter.AskView;
import com.hyperlocal.app.utlity.Constants;
import com.hyperlocal.app.utlity.FunctionUtil;
import com.jakewharton.rxbinding2.widget.RxTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static android.content.Context.MODE_PRIVATE;

/**
 * @author ${Umesh} on 18-04-2018.
 */

public class AskFragment extends Fragment implements AskView {
    @BindView(R.id.edit_invite_neighbors) EditText editText;
    @BindView(R.id.linear_layout) FrameLayout linearLayout;
    @BindView(R.id.text_length) TextView countCharacterTextView;

    MySharedPreferences sharedPreferences;
    FirebaseDatabase database;
    String uid;
    AskRequestPresenter presenter;
    private String fcmToken;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_ask, container, false);
        ButterKnife.bind(this, view);
        sharedPreferences = MySharedPreferences.getInstance(getActivity());
        uid = sharedPreferences.readString(Constants.USER_ID);
        if(getActivity()!=null){
            SharedPreferences fcmPref = getActivity().getSharedPreferences("fcm_token_pref", MODE_PRIVATE);
            fcmToken = fcmPref.getString(Constants.FCM_TOKEN, null);
        }

        database = FirebaseDatabase.getInstance();
        presenter = new AskRequestPresenter(AskFragment.this, sharedPreferences,uid);
        isTimerVisible();
        countCharacterTextView.setText("0/120");
        RxTextView.afterTextChangeEvents(editText);
        FunctionUtil.hideKeyboardOnOutSideTouch(linearLayout, getActivity());
        showEnterCharacterCount();
        addOutStandingEventListener();
        return view;
    }



    private void addOutStandingEventListener() {
        DatabaseReference databaseOutstanding = database.getReference("Outstanding").child(uid);
        databaseOutstanding.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                OutStandingRequestModel outStandingRequestModel = dataSnapshot.getValue(OutStandingRequestModel.class);
                if (outStandingRequestModel != null && outStandingRequestModel.getStatus().equalsIgnoreCase("1")) {
                    sharedPreferences.writeBoolean(Constants.TIMER_VISIBLE, false);
                    sharedPreferences.writeLong(Constants.TIMER_TIME, 0);
                } else if (outStandingRequestModel != null && outStandingRequestModel.getType().equalsIgnoreCase("ask")) {
                    long time = outStandingRequestModel.getTimeout() + outStandingRequestModel.getTimestamp();
                    if (time > 0) {
                        sharedPreferences.writeBoolean(Constants.TIMER_VISIBLE, true);
                    } else {
                        sharedPreferences.writeBoolean(Constants.TIMER_VISIBLE, false);
                    }
                    sharedPreferences.writeLong(Constants.TIMER_TIME, time);
                } else {
                    long time = outStandingRequestModel.getTimeout() + outStandingRequestModel.getTimestamp();
                    if (time > 0) {
                        sharedPreferences.writeBoolean(Constants.GIVE_AWAY_TIMER_VISIBLE, true);
                    } else {
                        sharedPreferences.writeBoolean(Constants.GIVE_AWAY_TIMER_VISIBLE, false);
                    }
                    sharedPreferences.writeLong(Constants.GIVE_AWAY_TIMER_TIME, time);
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
        });
    }

    private void isTimerVisible() {
        boolean checkTimerStatus = sharedPreferences.readBoolean(Constants.TIMER_VISIBLE);
        if (checkTimerStatus) {
            replaceFragment("");
        }
    }


    @OnClick(R.id.btn_next)
    public void onNextButtonClick() {
        presenter.nextButtonClick(fcmToken);
    }


    @Override
    public void showProgressBar() {

    }

    @Override
    public void hideProgressBar() {

    }

    @Override
    public void showSnackBar(String message) {
        Snackbar.make(linearLayout, message, Snackbar.LENGTH_SHORT).show();

    }

    @Override
    public String getAskRequest() {
        return editText.getText().toString();
    }

    @Override
    public void replaceFragment(String message) {
        editText.setText("");
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TITLE, message);
        Fragment childFragment = new TimerFragment();
        childFragment.setArguments(bundle);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        //transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.left_to_right, R.anim.right_to_left);
        transaction.replace(R.id.container, childFragment).commit();
    }

    public void showEnterCharacterCount() {
        RxTextView.beforeTextChangeEvents(editText)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(changeEvent -> {
                    if (changeEvent.text().length() <= 0) {
                        countCharacterTextView.setText("0/120");
                    } else {
                        countCharacterTextView.setText(changeEvent.text().length() + "/120");
                    }

                });
    }
}
