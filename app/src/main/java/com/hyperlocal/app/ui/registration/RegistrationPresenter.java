package com.hyperlocal.app.ui.registration;

import com.google.firebase.auth.PhoneAuthProvider;


/**
 * @Author ${Umesh} on 20-03-2018.
 */

public class RegistrationPresenter implements RegistrationMVP.LoginPresenter {

    private RegistrationMVP.LoginView loginView;
    private RegistrationMVP.LoginModel loginModel;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    public RegistrationPresenter(RegistrationMVP.LoginView loginView) {
        this.loginView = loginView;

    }

    @Override
    public void setView(RegistrationMVP.LoginView loginView) {
        this.loginView = loginView;

    }

    @Override
    public void fireBaseAuthentication(String mobileNumber, String countryCode, RegistrationActivity activity) {
        throw new UnsupportedOperationException();
    }

    /*@Override
    public void fireBaseAuthentication(String mobileNumber,String countryCode,RegistrationActivity activity) {
        loginView.showProgressBar();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            loginView.hideProgressBar();
                loginView.signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                loginView.hideProgressBar();
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    loginView.showErrorMessage((R.string.invalid_phone_number));
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    loginView.showErrorMessage((R.string.sms_quata_full));


                }
            }
        };

        //loginView.goNext();;
        String  mobile = countryCode +mobileNumber;
        mobile = "+91" +mobile;
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mobile,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                activity, mCallbacks);
    }
*/


    @Override
    public void verifyOTP(String string) {
        loginView.goNext();
    }

    @Override
    public void doRegistration(String userName, String emailAddress, String mAddress, String mPassword) {

    }



    /*@Override
    public void onLoginButtonClick() {
        String phoneNumber = loginView.getPhoneNumber();
        String countryCode = loginView.getCountryCode();
        if (phoneNumber.isEmpty()) {
            loginView.showErrorMessage(R.string.phone_empty_error_msg);
            return;
        }
        if (countryCode.isEmpty()) {
            loginView.showErrorMessage(R.string.country_code_empty);
            return;
        }
        if (!isPhoneNumberValid(phoneNumber)) {
            loginView.showErrorMessage(R.string.enter_valid_phone_number);
            return;
        }
        if (countryCode.equalsIgnoreCase("91") &&
                phoneNumber.equalsIgnoreCase("1234567890")) {
            loginView.startNewActivity();
            return;
        }
    }*/



    public boolean isPhoneNumberValid(String phoneNumber) {
        //validate phone numbers of format "1234567890"
        if (phoneNumber.length() < 8 || phoneNumber.length() > 15) {
            return false;
        } else
            return !phoneNumber.matches("^[0]+$") && phoneNumber.matches("[0-9]+") && phoneNumber.length() > 2;

    }
}
