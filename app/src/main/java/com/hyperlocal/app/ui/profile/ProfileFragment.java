package com.hyperlocal.app.ui.profile;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hyperlocal.app.MySharedPreferences;
import com.hyperlocal.app.R;
import com.hyperlocal.app.ui.home.fragment.AskFragment;
import com.hyperlocal.app.ui.registration.CountryPickerActivity;
import com.hyperlocal.app.ui.registration.RegistrationActivity;
import com.hyperlocal.app.ui.registration.UserDataModel;
import com.hyperlocal.app.utlity.Constants;
import com.hyperlocal.app.utlity.FunctionUtil;
import com.hyperlocal.app.utlity.Validator;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * @author ${Umesh} on 04-05-2018.
 */

public class ProfileFragment extends Fragment implements OtpDialogFragment.VerifyOtpListener {

    @BindView(R.id.user_name) EditText userNameEditText;
    @BindView(R.id.email) EditText emailEditText;
    @BindView(R.id.tv_mobile) EditText mobileEditText;
    @BindView(R.id.address) EditText addressEditText;
    @BindView(R.id.linear_layout) FrameLayout linearLayout;
    @BindView(R.id.progressBar) FrameLayout progressBar;

    @BindView(R.id.tv_code) EditText countryCodeEditText;

    private MySharedPreferences preferences;
    private UserProfileDataModel userProfileDataModel;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private OtpDialogFragment otpDialogFragment;
    private UserProfileDataModel dataModel;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    DatabaseReference logoutReference;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        auth = FirebaseAuth.getInstance();
        preferences = MySharedPreferences.getInstance(getActivity());
        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("users").child(preferences.readString(Constants.USER_ID));
        getProfileDataFromFireBase();
        return view;
    }

    private void getProfileDataFromFireBase() {
        userProfileDataModel = preferences.readObject(Constants.USER_PROFILE);
        userNameEditText.setText(userProfileDataModel.getName());
        emailEditText.setText(userProfileDataModel.getEmail());
        mobileEditText.setText(userProfileDataModel.getMobile());
        addressEditText.setText(userProfileDataModel.getAddress());
    }

    @OnClick(R.id.tv_code)
    public void onCountryPickerClick() {
        Activity activity = getActivity();
        if (activity != null) {
            Intent intent = new Intent(activity, CountryPickerActivity.class);
            startActivityForResult(intent, 300);
        }
    }

    @OnClick(R.id.address)
    public void onAddressEditTextClick() {
        if (FunctionUtil.isLocationEnabled(getActivity())) {
            if (checkSelfPermission()) {
                try {
                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(getActivity());
                    startActivityForResult(intent, 1);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @OnClick(R.id.btn_logout)
    public void logoutUser(){
        showAlertForConfirmation();
        }


    private void showAlertForConfirmation() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(false);
        dialog.setTitle("Hyperlocal");
        dialog.setContentView(R.layout.custom_aleart_dialog);
        Button dialogButtonYes = dialog.findViewById(R.id.btn_yes);
        Button dialogButtonNo = dialog.findViewById(R.id.btn_cancel);
        dialogButtonNo.setVisibility(View.VISIBLE);
        TextView alertSubTextView = dialog.findViewById(R.id.sub_title);
        alertSubTextView.setText("Are you sure want to logout?");
        dialogButtonYes.setOnClickListener((View v) -> {
            updateLogout();
            dialog.dismiss();

        });

        dialog.show();
        dialogButtonNo.setOnClickListener(view -> dialog.dismiss());
    }

    private void updateLogout() {
        HashMap hashMap = new HashMap();
        hashMap.put("loginStatus","0");
        logoutReference = FirebaseDatabase
                .getInstance()
                .getReference("users")
                .child(preferences.readString(Constants.USER_ID));
        logoutReference.updateChildren(hashMap);
        FirebaseAuth.getInstance().signOut();
        MySharedPreferences.getInstance(getActivity()).clearPref();
        startActivity(new Intent(getActivity(), RegistrationActivity.class));
    }


    @OnClick(R.id.btn_next)
    public void onButtonClick() {
        userProfileDataModel = preferences.readObject(Constants.USER_PROFILE);
        String userName = userNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phoneNumber = mobileEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();
        String countryCode = countryCodeEditText.getText().toString();
        dataModel = new UserProfileDataModel();
        dataModel.setMobile(phoneNumber);
        dataModel.setEmail(email);
        dataModel.setName(userName);
        dataModel.setAddress(address);
        dataModel.setTimeStamp(userProfileDataModel.getTimeStamp());
        dataModel.setCountryCode(userProfileDataModel.getCountryCode());

        if (dataModel.equals(userProfileDataModel)) {
            Snackbar.make(linearLayout, "Nothing changed for update!", Snackbar.LENGTH_SHORT).show();
        } else {
            if (!Validator.isValidName(userName)) {
                Snackbar.make(linearLayout, "Please enter valid name", Snackbar.LENGTH_SHORT).show();
            } else if (!Validator.isValidPhoneNumber(phoneNumber)) {
                Snackbar.make(linearLayout, "Please enter valid mobile number", Snackbar.LENGTH_SHORT).show();
            } else {
                if(phoneNumber.equalsIgnoreCase(userProfileDataModel.getMobile())&& countryCode.equalsIgnoreCase(userProfileDataModel.getCountryCode())){
                    UpdateProfile(dataModel);
                }else {
                    verifyMobileNumber(phoneNumber,countryCode);
                }
            }
        }
    }

    private void UpdateProfile(UserProfileDataModel dataModel) {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
                Map<String, Object> postValues = new HashMap<String, Object>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    postValues.put(snapshot.getKey(), snapshot.getValue());
                }
                postValues.put("emailId", dataModel.getEmail());
                postValues.put("address", dataModel.getAddress());
                postValues.put("mobileNumber", dataModel.getMobile());
                postValues.put("userName", dataModel.getName());
                postValues.put("device_type","1");
                reference.updateChildren(postValues);
                reference.removeEventListener(this);
                saveUpdatedValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }



    private void verifyMobileNumber(String mobileNumber, String countryCode) {
        if (countryCode != null) {
            if (Validator.isValidPhoneNumber(mobileNumber)) {
                fireBaseAuthentication(mobileNumber, countryCode, this);
            } else {
                showErrorMessage(R.string.enter_valid_phone_number);
            }
        } else {
            showErrorMessage(R.string.country_code_empty);
        }
    }


    private void fireBaseAuthentication(String mobileNumber, String countryCode, ProfileFragment profileFragment) {
       progressBar.setVisibility(View.VISIBLE);
       mCallbacks  = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                progressBar.setVisibility(View.GONE);
                auth.getCurrentUser().updatePhoneNumber(phoneAuthCredential);
                Toast.makeText(getActivity(), "Verification completed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // hideProgressBar();
                progressBar.setVisibility(View.GONE);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(getActivity(), "verification failed", Toast.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Toast.makeText(getActivity(), "verification failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken);
                progressBar.setVisibility(View.GONE);
                mVerificationId = verificationId;
                mResendToken = forceResendingToken;
                otpDialogFragment = new OtpDialogFragment(getActivity(), ProfileFragment.this);
                //otpDialogFragment.setCancelable(false);
                otpDialogFragment.show();
            }
        };

        String mobile = countryCode + mobileNumber;
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mobile,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                getActivity(), mCallbacks);
    }

    private void showErrorMessage(int enter_valid_phone_number) {
        Snackbar.make(linearLayout, getString(enter_valid_phone_number), Snackbar.LENGTH_SHORT).show();
    }

    private void saveUpdatedValue() {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
                UserDataModel model = dataSnapshot.getValue(UserDataModel.class);
                if (model != null) {
                    saveDataInPref(model);
                    reference.removeEventListener(this);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);

            }
        });

    }

    private void saveDataInPref(UserDataModel model) {
        Snackbar.make(linearLayout, "Profile Updated Successfully!", Snackbar.LENGTH_SHORT).show();
        UserProfileDataModel dataModel = new UserProfileDataModel();
        dataModel.setAddress(model.getAddress());
        dataModel.setName(model.getUserName());
        dataModel.setEmail(model.getEmailId());
        dataModel.setMobile(model.getMobileNumber());
        dataModel.setCountryCode(model.getCountryCode());
        dataModel.setTimeStamp(model.getTimestamp());
        preferences.writeBoolean(Constants.IS_USER_REGISTERED, true);
        preferences.writeObject(Constants.USER_PROFILE, dataModel);
        preferences.writeString(Constants.MOBILE_NUMBER, model.getMobileNumber());
        preferences.writeString(Constants.PASSWORD, model.getPassword());
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.left_to_right, R.anim.right_to_left);
        transaction.replace(R.id.container, new AskFragment()).commit();
    }


    public boolean checkSelfPermission() {
        if (ActivityCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            return false;
        } else {
            Log.e("DB", "PERMISSION GRANTED");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (permissions[0].equals(android.Manifest.permission.ACCESS_COARSE_LOCATION) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), "Permission Granted", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 300 && data != null) {
            String countryCode = data.getStringExtra("dialCode");
            String strCountryName = data.getStringExtra("countryName");
            //countryName.setText(strCountryName);
            countryCodeEditText.setText(countryCode);
            System.out.println(countryCode);
        }
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                addressEditText.setText(place.getAddress());
                addressEditText.setSelection(place.getAddress().length());

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
            }
        }
    }

    @Override
    public void onVerifyOtpButtonClick(String otp) {
        if(TextUtils.isEmpty(otp)){
            Toast.makeText(getActivity(), "Please enter otp", Toast.LENGTH_SHORT).show();
        }else {
            try {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp);
                if (auth != null) {
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null) {
                        user.updatePhoneNumber(credential);
                        String strMobileNumber = user.getPhoneNumber();
                        userProfileDataModel.setMobile(strMobileNumber);
                        UpdateProfile(dataModel);
                        otpDialogFragment.dismiss();
                    }
                }
            } catch (IllegalArgumentException exception) {
                exception.printStackTrace();
            }
        }

    }

    @Override
    public void reSendOtpButtonClick() {
        String mobile = countryCodeEditText.getText().toString() + mobileEditText.getText().toString();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mobile,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                getActivity(),               // Activity (for callback binding)
                mCallbacks,mResendToken);        // OnVerificationStateChangedCallbacks
        // mVerificationInProgress = true;
    }


}
