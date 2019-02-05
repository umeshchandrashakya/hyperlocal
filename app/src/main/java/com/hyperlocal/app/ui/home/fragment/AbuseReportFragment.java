package com.hyperlocal.app.ui.home.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

/**
 * @Author ${Umesh} on 05-04-2018.
 */

public class AbuseReportFragment extends Fragment {

    @BindView(R.id.text_length)TextView textViewLength;
    @BindView(R.id.edit_abuse_report)EditText abuseReportEditText;
    @BindView(R.id.linear_layout)FrameLayout frameLayout;
    private MySharedPreferences mySharedPreferences;

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.report_abuse_fragment,container,false);
        ButterKnife.bind(this,view);
        mySharedPreferences = MySharedPreferences.getInstance(getActivity());
        FunctionUtil.hideKeyboardOnOutSideTouch(frameLayout,getActivity());
        displayValidation();
        return view;
    }

    private void displayValidation() {
        RxTextView.afterTextChangeEvents(abuseReportEditText)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tvChangeEvent-> {
                    if(tvChangeEvent.view().getText().length()<=0){
                        textViewLength.setText("0/120");
                    }else {
                        textViewLength.setText(tvChangeEvent.view().getText().length() + "/120");
                    }
                });
    }

    @OnClick(R.id.btn_next)
    public void onNextButtonClick(){
        String strText = abuseReportEditText.getText().toString();
        if(TextUtils.isEmpty(strText)){
            Snackbar.make(frameLayout, R.string.plz_enter_text,Snackbar.LENGTH_SHORT).show();
        }else {
            fireBaseApiCall(strText);
        }
    }

    private void fireBaseApiCall(String strText) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ReportAbuse").push();
        HashMap hashMap = new HashMap();
        hashMap.put("message",strText);
        hashMap.put("timestamp", System.currentTimeMillis());
        hashMap.put("userId",mySharedPreferences.readString(Constants.USER_ID));
        databaseReference.setValue(hashMap);
        abuseReportEditText.setText("");
        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction taTransaction = manager.beginTransaction();
        taTransaction.addToBackStack(null);
        taTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        taTransaction.replace(R.id.linear_layout,new ThankReportAbuseFragment()).commit();

        // taTransaction.setCustomAnimations(R.anim.enter, R.anim.exit,R.anim.left_to_right, R.anim.right_to_left);
        //taTransaction.replace(R.id.linear_layout,new ThankReportAbuseFragment()).commit();

    }

}
