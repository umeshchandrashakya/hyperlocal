package com.hyperlocal.app.ui.home.presenter;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.hyperlocal.app.MySharedPreferences;
import com.hyperlocal.app.R;
import com.hyperlocal.app.model.AbuseWordsData;
import com.hyperlocal.app.ui.home.fragment.AboutFragment;
import com.hyperlocal.app.utlity.Constants;

import java.util.HashMap;

/**
 * @author ${Umesh} on 05-07-2018.
 */

public class AskRequestPresenter {

    private AskView view;
    private MySharedPreferences sharedPreferences;
    private String uid;

    public AskRequestPresenter(AskView view, MySharedPreferences sharedPreferences, String uid) {
        this.view = view;
        this.sharedPreferences = sharedPreferences;
        this.uid = uid;
    }


    public void nextButtonClick(String fcmToken) {
        String strRequestText = view.getAskRequest();
        if (strRequestText != null && strRequestText.equalsIgnoreCase("")) {
            view.showSnackBar("Please request the item first ");
        } else if (AbuseWordsData.isAnyAbuseInText(strRequestText)) {
            view.showSnackBar("There are some vulgar words in your text please remove them");
        } else {
            sharedPreferences.writeBoolean(Constants.ASK_REQUEST_IS_ACTIVE,false);
            sharedPreferences.writeLong(Constants.TIMER_TIME, System.currentTimeMillis() + 300000);
            sharedPreferences.writeBoolean(Constants.TIMER_VISIBLE, true);
            sharedPreferences.writeString(Constants.TIMER_TITLE, strRequestText);
            DatabaseReference databaseReference = FirebaseDatabase
                    .getInstance()
                    .getReference("ask_request")
                    .child(uid)
                    .push();

            String requestKey = databaseReference.getKey();
            sharedPreferences.writeString(Constants.ASK_REQUEST_KEY, requestKey);
            HashMap hashMap = new HashMap<>();
            hashMap.put("item", strRequestText);
            hashMap.put("timestamp", ServerValue.TIMESTAMP);
            hashMap.put("timeout", convertMinutesIntoMilliSecond(5));
            hashMap.put("fromId", sharedPreferences.readString(Constants.USER_ID));
            hashMap.put("requestType", "0");
            hashMap.put("type", "ask");
            hashMap.put("device_token", fcmToken);
            hashMap.put("device_type", "1");
            databaseReference.setValue(hashMap);
            insertAsOutStandingReq(strRequestText);

        }
    }


    private void insertAsOutStandingReq(String strRequestText) {
        HashMap hashMap = new HashMap();
        hashMap.put("item", strRequestText);
        hashMap.put("requestType", "Out Standing Request");
        hashMap.put("status", "0");
        hashMap.put("timestamp", ServerValue.TIMESTAMP);
        hashMap.put("timeout", convertMinutesIntoMilliSecond(5));
        hashMap.put("requestType", "0");
        hashMap.put("type", "ask");
        DatabaseReference databaseOutstanding = FirebaseDatabase
                .getInstance()
                .getReference("Outstanding")
                .child(uid)
                .child("0");
        databaseOutstanding.setValue(hashMap);
        view.replaceFragment(strRequestText);
    }


    public long convertMinutesIntoMilliSecond(int minutes) {
        return 5 * 60000;
    }

}
