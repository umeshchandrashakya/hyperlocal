package com.hyperlocal.app.ui.home.fragment;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.hyperlocal.app.MySharedPreferences;
import com.hyperlocal.app.R;
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
 * @author ${Umesh} on 22-06-2018.
 */

public class InviteFragmentNext extends Fragment implements BillingProcessor.IBillingHandler{

    @BindView(R.id.edit_invite_neighbors) EditText editText;
    @BindView(R.id.linear_layout) FrameLayout linearLayout;
    @BindView(R.id.text_length) TextView countCharacterTextView;

    MySharedPreferences sharedPreferences;
    FirebaseDatabase database;
    String uid;
    private String selectedDate;
    private String hours,minutes,unit;
    private BillingProcessor bp;
    private String message;
    private long timeOut;

    private static final String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnhFM0M+fALVRYzu6ubza5BRKm3//KiZXDX0lTYbEjKOWBBiAtUfwkRCDyHW0ovJybKeDG29ZBRLYB6ICpWOqYC8PrrcgUTxDg6VDldvJtfOEhCtN9WBPmhjovAhvALqV2G68BLHPCShHB70Tnrwa174QXZWl5tDHoY6rPhG2N+dT8snOaEi7Tw2RHQhmerrQXX66sMWaqdcqJHnhVsSocQi5NNZhx/Si0y5XV9SD2zkzWZt2+d9A1L4ghONti+bV/3yvOC70lv+Wnobskkr+DTi9dRjBT1SPpjOXkjgqkn+3ctdGoDwwaHYRdt0nkXCCrSE1GBqo0TZspSCyc1RJYQIDAQAB";
    private static final String ITEM_SKU = "android.test.purchased";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.invite_next_fragment, container, false);
        ButterKnife.bind(this, view);
        bp= new BillingProcessor(getActivity(),base64EncodedPublicKey,InviteFragmentNext.this);
        bp.initialize();
        sharedPreferences = MySharedPreferences.getInstance(getActivity());
        database = FirebaseDatabase.getInstance();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Bundle  intent = getArguments();
        if(intent!=null){
            selectedDate = intent.getString("selected_date");
            hours = intent.getString("hours");
            minutes = intent.getString("minutes");
            unit = intent.getString("timeUnit");
            message = intent.getString("message");
            timeOut = intent.getLong("TimeOut");
        }

        countCharacterTextView.setText("0/120");
        RxTextView.afterTextChangeEvents(editText);
        FunctionUtil.hideKeyboardOnOutSideTouch(linearLayout, getActivity());
        showEnterCharacterCount();
        return view;
    }


    @OnClick(R.id.btn_next)
    public void onNextButtonClick() {
        bp.purchase(getActivity(), ITEM_SKU);
    }

    private void insertAsOutStandingReq(String strRequestText) {
        HashMap hashMap = new HashMap();
        hashMap.put("item", strRequestText);
        hashMap.put("requestType", "Out Standing Request");
        hashMap.put("status", "0");
        hashMap.put("timestamp", System.currentTimeMillis());
        hashMap.put("timeout", timeOut);
        hashMap.put("requestType", "0");
        hashMap.put("type", "invite");

        DatabaseReference databaseOutstanding = FirebaseDatabase
                .getInstance()
                .getReference("Outstanding")
                .child(uid)
                .child("2");
        databaseOutstanding.setValue(hashMap);
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


    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
        bp.consumePurchase(productId);
        enterInviteRequest();
        Toast.makeText(getActivity(), "Request send successfully", Toast.LENGTH_SHORT).show();
        FragmentManager manager = getFragmentManager();
        manager.popBackStackImmediate();
    }

    private void enterInviteRequest() {
        SharedPreferences preferences = getActivity().getSharedPreferences("fcm_token_pref", MODE_PRIVATE);
        String fcmToke = preferences.getString(Constants.FCM_TOKEN, null);
        DatabaseReference databaseReference = database.getReference("invite_request").child(uid).push();
        HashMap hashMap = new HashMap<>();
        hashMap.put("item", message);
        hashMap.put("timestamp", System.currentTimeMillis());
        hashMap.put("timeout", timeOut);
        hashMap.put("fromId", sharedPreferences.readString(Constants.USER_ID));
        hashMap.put("requestType", "0");
        hashMap.put("type", "invite");
        hashMap.put("device_token", fcmToke);
        hashMap.put("device_type", "1");
        hashMap.put("date",selectedDate);
        hashMap.put("invitationDate",selectedDate+":"+hours+":"+minutes+":"+unit);
        databaseReference.setValue(hashMap);
        insertAsOutStandingReq(message);
    }

    @Override
    public void onPurchaseHistoryRestored() {
        //Toast.makeText(getActivity(), "on purchase history Success", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {
        //Toast.makeText(getActivity(), errorCode+"on Billing Error"+error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBillingInitialized() {
        //Toast.makeText(getActivity(), "on Billing initialized ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }



    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }

}
