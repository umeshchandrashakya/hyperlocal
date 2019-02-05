package com.hyperlocal.app.ui.registration;



/**
 * @Author ${Umesh} on 20-03-2018.
 */

public interface RegistrationMVP {

    interface LoginView {
        String getPhoneNumber();
        String getCountryCode();
        void showProgressBar();
        void hideProgressBar();
        void showErrorMessage(int messageId);
        void goNext();
        int getItemOfViewPager(int i);

        //void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential);
    }

    interface LoginPresenter {
      void setView(RegistrationMVP.LoginView loginView);
      //void onLoginButtonClick();

        void fireBaseAuthentication(String mobileNumber,String countryCode,RegistrationActivity activity);

        void verifyOTP(String string);

        void doRegistration(String userName, String emailAddress, String mAddress, String mPassword);
    }

    interface LoginModel {

    }

}
