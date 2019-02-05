package com.hyperlocal.app.ui.registration.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.hyperlocal.app.R;
import com.hyperlocal.app.utlity.Validator;
import com.jakewharton.rxbinding2.widget.RxTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * @Author ${Umesh} on 02-04-2018.
 */

public class RegEmailFragment extends Fragment {

    private Button btnNext;
    @BindView(R.id.edit_email)EditText emailEditText;
    @BindView(R.id.address_input_text)TextInputLayout inputType;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reg_email_frgament,container,false);
        ButterKnife.bind(this,view);
        showValidationMessage();
        return view;
    }

    private void showValidationMessage() {
        RxTextView.afterTextChangeEvents(emailEditText)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(eventChange->{
                    String s = eventChange.view().getText().toString();
                    if(TextUtils.isEmpty(s)){
                        inputType.setErrorEnabled(false);
                    } else if(Validator.emailValidator(s)){
                        inputType.setErrorEnabled(false);
                    }else {
                        inputType.setError(getString(R.string.enter_email));
                        requestFocus(inputType);
                    }
                });
    }


    public String getEmail() {
        return emailEditText.getText().toString().trim();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
