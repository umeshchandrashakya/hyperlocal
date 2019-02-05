package com.hyperlocal.app.ui.registration.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.hyperlocal.app.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Author ${Umesh} on 02-04-2018.
 */

public class RegOtpFragment extends Fragment {

    private Button btnNext;
    @BindView(R.id.edit_otp) EditText otpEditText;
    @BindView(R.id.address_input_text)TextInputLayout inputType;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.otp_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    public String getOtp() {
        String otp = otpEditText.getText().toString().trim();
        return otp;

    }
}
