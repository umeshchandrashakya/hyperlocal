package com.hyperlocal.app.ui.registration.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.hyperlocal.app.R;
import com.hyperlocal.app.ui.registration.CountryPickerActivity;
import com.hyperlocal.app.utlity.Validator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @Author ${Umesh} on 02-04-2018.
 */

public class RegPhoneNumberFragment extends Fragment implements TextWatcher {

    @BindView(R.id.edit_phone_number) EditText phoneNumberEditText;
    @BindView(R.id.address_input_text) TextInputLayout inputType;
    @BindView(R.id.btn_country) TextView btnCountryCode;
    @BindView(R.id.phone_number)TextView phoneNumber;

    private String countryCode;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.phone_number_frgament, container, false);
        ButterKnife.bind(this, view);
        btnCountryCode.setText("+1");
        phoneNumberEditText.addTextChangedListener(this);
        return view;
    }


    @OnClick(R.id.btn_country)
    public void countryCodeButtonClick() {
        Activity activity = getActivity();
        if (activity != null) {
            Intent intent = new Intent(activity, CountryPickerActivity.class);
            startActivityForResult(intent, 300);
        }
    }

    public String getPhoneNumber() {
        return phoneNumberEditText.getText().toString();
    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (Validator.isValidPhoneNumber(charSequence.toString())) {
            inputType.setErrorEnabled(false);
        } else if(charSequence.toString().length()<8){
            inputType.setError(getString(R.string.phone_number_length_sort));
            requestFocus(inputType);
        }else if(charSequence.length()>25){
            inputType.setError(getString(R.string.enter_valid_phone_number));
            requestFocus(inputType);
        }else {
            inputType.setError(getString(R.string.enter_valid_phone_number));
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 300 && data!=null) {
             countryCode = data.getStringExtra("dialCode");
            btnCountryCode.setText(countryCode);
            countryCode = btnCountryCode.getText().toString();
            }
    }

    public String getCountryCode(){
        return btnCountryCode.getText().toString();
    }
}
