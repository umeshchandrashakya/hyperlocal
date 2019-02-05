package com.hyperlocal.app.ui.profile;


import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.hyperlocal.app.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * @author ${Umesh} on 10-05-2018.
 */

public class OtpDialogFragment extends Dialog {
    @BindView(R.id.btn_verify)
    Button verifyButton;
    @BindView(R.id.tv_resend)
    TextView resendTextView;
    @BindView(R.id.edit_text)
    EditText editText;
    VerifyOtpListener listener;
    private FirebaseAuth auth;


    public OtpDialogFragment(@NonNull Context context, VerifyOtpListener listener) {
        super(context);
        this.listener = listener;
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.otp_dialog);
        auth = FirebaseAuth.getInstance();
        ButterKnife.bind(this);
    }

    @OnClick(R.id.tv_resend)
    public void reSendOtpClick(){
        listener.reSendOtpButtonClick();
    }

    @OnClick(R.id.btn_verify)
    public void setVerificationButtonClick() {
        String otp = editText.getText().toString();
        listener.onVerifyOtpButtonClick(otp);
        //dismiss();
    }

    public interface VerifyOtpListener{
         void onVerifyOtpButtonClick(String otp);
         void reSendOtpButtonClick();
    }


}
