package com.hyperlocal.app.ui.registration;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.location.LocationRequest;
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
import com.hyperlocal.app.adapter.NonSwipeAbleViewPager;
import com.hyperlocal.app.adapter.NonSwipeViewPagerAdapter;
import com.hyperlocal.app.locationhelper.CurrentLocationListener;
import com.hyperlocal.app.ui.BaseActivity;
import com.hyperlocal.app.ui.home.HomeActivity;
import com.hyperlocal.app.ui.profile.UserProfileDataModel;
import com.hyperlocal.app.ui.registration.fragment.RegAddressFragment;
import com.hyperlocal.app.ui.registration.fragment.RegEmailFragment;
import com.hyperlocal.app.ui.registration.fragment.RegNameFragment;
import com.hyperlocal.app.ui.registration.fragment.RegOtpFragment;
import com.hyperlocal.app.ui.registration.fragment.RegPasswordFragment;
import com.hyperlocal.app.ui.registration.fragment.RegPhoneNumberFragment;
import com.hyperlocal.app.utlity.Constants;
import com.hyperlocal.app.utlity.FunctionUtil;
import com.hyperlocal.app.utlity.Validator;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * @Author ${Umesh} on 20-03-2018.
 */

public class RegistrationActivity extends BaseActivity  {

    private static final String TAG = RegistrationActivity.class.getSimpleName();

    public static final int PHONE_NUMBER = 0;
    public static final int OTP_NUMBER = 1;
    public static final int NAME = 2;
    public static final int EMAIL = 3;
    public static final int ADDRESS = 4;
    public static final int PASSWORD = 5;

    @BindView(R.id.view_pager) NonSwipeAbleViewPager viewPager;
    @BindView(R.id.btn_next) Button btnNext;
    @BindView(R.id.progressBar) FrameLayout progressBarFrameLayout;
    @BindView(R.id.frame_layout) FrameLayout frameLayout;
    @BindView(R.id.text_registration) TextView textRegistration;


    private String emailAddress;
    private String userName;
    private String mAddress;
    private ArrayList<Fragment> fragmentsArrayList = new ArrayList<>();
    private FirebaseAuth mAuth;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private String mobileNumber;
    private MySharedPreferences sharedPreferences;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    private CurrentLocationListener locationListener;

    private Location mLocation;
    private Context context;
    private  String uid;
    private String countryCode;
    private UserDataModel userDataModel;
    NonSwipeViewPagerAdapter adapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_registration);
        ButterKnife.bind(this);
        context = this;
        sharedPreferences = MySharedPreferences.getInstance(this);
        userDataModel = sharedPreferences.readUserDataModel(Constants.USER_DATA_MODEL);
        if(userDataModel == null){
            userDataModel = new UserDataModel();
            setupViewPager(0);
            viewPager.setVisibility(View.VISIBLE);
        }else {
            userDataModel = sharedPreferences.readUserDataModel(Constants.USER_DATA_MODEL);
            uid = sharedPreferences.readString(Constants.USER_ID);
            int i = userDataModel.getCurrentPage();
           // setViewPagerHeight();
            setupViewPager(i);
            viewPager.setVisibility(View.VISIBLE);
        }
        locationListener = new CurrentLocationListener(context,this);
        subscribeToLocationUpdate();
        FunctionUtil.hideKeyboardOnOutSideTouch(frameLayout, RegistrationActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void subscribeToLocationUpdate() {
        if (FunctionUtil.isLocationEnabled(RegistrationActivity.this)) {
            locationListener.observe(this, location -> {
                mLocation = location;
            });
        } else {
            locationListener.enableLocationDialog();
        }
    }


    @OnClick(R.id.btn_next)
    public void nextButtonClick() {
      //  Crashlytics.getInstance().crash();
        int i = viewPager.getCurrentItem();
        callFromFragment(i);
        if(i==3){
            fragmentsArrayList.add(new RegAddressFragment());
            fragmentsArrayList.add(new RegPasswordFragment());
            adapter.setFragments(fragmentsArrayList);
            adapter.notifyDataSetChanged();
            callFromFragment(i);
        }
    }

    private void setupViewPager(int i) {
        fragmentsArrayList = new ArrayList<>();
        fragmentsArrayList.add(new RegPhoneNumberFragment());
        fragmentsArrayList.add(new RegOtpFragment());
        fragmentsArrayList.add(new RegNameFragment());
        fragmentsArrayList.add(new RegEmailFragment());
        adapter = new NonSwipeViewPagerAdapter(getSupportFragmentManager());
        adapter.setFragments(fragmentsArrayList);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(i);
    }


    public void callFromFragment(int currentPage) {
        switch (currentPage) {
            case PHONE_NUMBER:
                RegPhoneNumberFragment
                        phoneNumberFragment = (RegPhoneNumberFragment) fragmentsArrayList.get(currentPage);
                mobileNumber = phoneNumberFragment.getPhoneNumber();
                countryCode = phoneNumberFragment.getCountryCode();
                userDataModel.setMobileNumber(mobileNumber);
                userDataModel.setCountryCode(countryCode);
                saveUserModelInPref();
                verifyMobileNumber(phoneNumberFragment.getPhoneNumber(), phoneNumberFragment.getCountryCode());
                break;
            case OTP_NUMBER:
                RegOtpFragment otpFragment
                        = (RegOtpFragment) fragmentsArrayList.get(currentPage);
                verifyOTP(mVerificationId, otpFragment.getOtp());
                break;
            case NAME:
                RegNameFragment nameFragment
                        = (RegNameFragment) fragmentsArrayList.get(currentPage);
                userName = nameFragment.getName();
                if (TextUtils.isEmpty(userName)) {
                    showErrorMessage(R.string.enter_name);
                } else {
                    userDataModel.setUserName(userName);
                    saveUserModelInPref();
                    goNext();
                }
                break;
            case EMAIL:
                RegEmailFragment emailFragment
                        = (RegEmailFragment) fragmentsArrayList.get(currentPage);
                emailAddress = emailFragment.getEmail();
                if (Validator.emailValidator(emailAddress)) {
                    userDataModel.setEmailId(emailAddress);
                    saveUserModelInPref();
                    goNext();
                } else {
                    showErrorMessage(R.string.enter_email);
                }
                break;
            case ADDRESS:
              //  setViewPagerHeight();
                RegAddressFragment addressFragment
                        = (RegAddressFragment) fragmentsArrayList.get(currentPage);
                mAddress = addressFragment.getAddress();
                if (TextUtils.isEmpty(mAddress)) {
                    showErrorMessage(R.string.please_enter_address);
                } else {
                    userDataModel.setAddress(mAddress);
                    saveUserModelInPref();
                    goNext();
                    btnNext.setText("Done");
                }
                break;
            case PASSWORD:
                RegPasswordFragment passwordFragment
                        = (RegPasswordFragment) fragmentsArrayList.get(currentPage);
                String mPassword = passwordFragment.getPassword();
                if (!TextUtils.isEmpty(mPassword) && Validator.isValidPassword(mPassword)) {
                    userDataModel.setPassword(mPassword);
                    saveUserModelInPref();
                    doRegistration(userDataModel.getMobileNumber(),userDataModel.getCountryCode(),userDataModel.getUserName(), userDataModel.getEmailId(), userDataModel.getAddress(), userDataModel.getPassword());
                } else {
                    showErrorMessage(R.string.please_enter_password);
                }
                break;
        }
    }



    private void saveUserModelInPref() {
        sharedPreferences.writeUserDataModel(Constants.USER_DATA_MODEL,userDataModel);
    }


    private void verifyMobileNumber(String mobileNumber, String countryCode) {
        if (countryCode != null) {
            if (Validator.isValidPhoneNumber(mobileNumber)) {
                fireBaseAuthentication(mobileNumber, countryCode, RegistrationActivity.this);
            } else {
                showErrorMessage(R.string.enter_valid_phone_number);
            }
        } else {
            showErrorMessage(R.string.country_code_empty);
        }
    }


    public void fireBaseAuthentication(String mobileNumber, String countryCode, RegistrationActivity activity) {
        showProgressBar();
        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
               // hideProgressBar();
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                hideProgressBar();
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    showErrorMessage((R.string.invalid_phone_number));
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    showErrorMessage((R.string.sms_quota_full));
                }
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken);
                mVerificationId = verificationId;
                mResendToken = forceResendingToken;
                goNext();
                hideProgressBar();
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String s) {
                super.onCodeAutoRetrievalTimeOut(s);

            }
        };

        String mobile = countryCode + mobileNumber;
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mobile,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                activity, mCallbacks);

    }


    private void verifyOTP(String mVerificationId, String otp) {
        if(TextUtils.isEmpty(otp)){
           showErrorMessage((R.string.please_enter_otp));
        }else {
            showProgressBar();
            try {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp);
                signInWithPhoneAuthCredential(credential);
            } catch (IllegalArgumentException exception) {
                exception.printStackTrace();
            }
        }
    }



    private void doRegistration(String mobileNumber,String countryCode,String userName, String emailAddress, String mAddress, String mPassword) {
        showProgressBar();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference().child("users");
        UserDataModel model = new UserDataModel();
        model.setUserName(userName);
        model.setEmailId(emailAddress);
        model.setAddress(mAddress);
        model.setMobileNumber(mobileNumber);
        model.setPassword(mPassword);
        model.setCountryCode(countryCode);
        model.setDevice_type("1");
        model.setTimestamp(System.currentTimeMillis());
        model.setLoginStatus("1");

        if(mLocation!=null){
            model.setLatitude(mLocation.getLatitude());
            model.setLongitude(mLocation.getLongitude());
        }

        SharedPreferences preferences = getSharedPreferences("fcm_token_pref",MODE_PRIVATE);
        String fcmToken = preferences.getString(Constants.FCM_TOKEN,null);
        model.setDevice_token(fcmToken);
        reference.child(uid).setValue(model);
        reference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hideProgressBar();
                UserDataModel model = dataSnapshot.getValue(UserDataModel.class);
                if(model!=null){
                    saveDataInPref(model);
                    reference.removeEventListener(this);
                    sharedPreferences.writeString(Constants.USER_NAME,model.getUserName());
                    Intent intent = new Intent(RegistrationActivity.this, HomeActivity.class);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                    startActivity(intent);
                    finish();
                }else {
                    goNext();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressBar();
                reference.removeEventListener(this);
            }
        });
    }


    public void goNext() {
        viewPager.setCurrentItem(getItemOfViewPager(1));
        userDataModel.setCurrentPage(getItemOfViewPager(1)-1);
        saveUserModelInPref();
    }

    public int getItemOfViewPager(int i) {
        return viewPager.getCurrentItem() + i;
    }


    public void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        mAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = task.getResult().getUser();
                        uid = user.getUid();
                        sharedPreferences.writeString(Constants.USER_ID,uid);
                        System.out.println(uid);
                        hideProgressBar();
                        if (task.isSuccessful()) {
                            showProgressBar();
                            isUserRegistered();
                            //goNext();
                            }
                    } else {
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            showErrorMessage(R.string.enter_correct_opt);
                            hideProgressBar();
                        }
                    }
                });
    }


    private void isUserRegistered() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("users").child(uid);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserDataModel model = dataSnapshot.getValue(UserDataModel.class);
                System.out.println(model);
                if(model!=null){
                    hideProgressBar();
                    saveDataInPref(model);
                    databaseReference.removeEventListener(this);
                    Intent intent = new Intent(RegistrationActivity.this, PasswordActivity.class);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                    startActivity(intent);
                    //viewPager.setCurrentItem(0);
                }else {
                    hideProgressBar();
                    goNext();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressBar();
                databaseReference.removeEventListener(this);
            }
        });
    }

    private void saveDataInPref(UserDataModel model) {
        UserProfileDataModel dataModel = new UserProfileDataModel();
        dataModel.setAddress(model.getAddress());
        dataModel.setName(model.getUserName());
        dataModel.setEmail(model.getEmailId());
        dataModel.setMobile(model.getMobileNumber());
        dataModel.setCountryCode(model.getCountryCode());
        dataModel.setTimeStamp(model.getTimestamp());
        sharedPreferences.writeBoolean(Constants.IS_USER_REGISTERED, true);
        sharedPreferences.writeObject(Constants.USER_PROFILE,dataModel);
        sharedPreferences.writeString(Constants.MOBILE_NUMBER, model.getMobileNumber());
        sharedPreferences.writeString(Constants.PASSWORD, model.getPassword());
        sharedPreferences.writeString(Constants.USER_NAME,model.getUserName());
        sharedPreferences.writeString(Constants.EMAIL_ID,model.getEmailId());
        sharedPreferences.writeLong(Constants.TIME_STAMP,model.getTimestamp());
    }

    @Override
    protected void onStart() {
        super.onStart();
        // presenter.setView(this);
    }




    public void showProgressBar() {
        progressBarFrameLayout.setVisibility(View.VISIBLE);
    }


    public void hideProgressBar() {
        progressBarFrameLayout.setVisibility(View.GONE);
    }


    public void showErrorMessage(int messageId) {
        Snackbar.make(frameLayout, getString(messageId), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        //btnNext.setText("Next");
        if(viewPager.getCurrentItem()==1){
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        if(locationListener!=null){
                            mLocation = locationListener.getLocation();
                        }
                        //Toast.makeText(context, ""+locationListener.getLocation(), Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    @Override
    public void locationUpdated(Location location) {
        super.locationUpdated(location);
        this.mLocation = location;
    }
}
