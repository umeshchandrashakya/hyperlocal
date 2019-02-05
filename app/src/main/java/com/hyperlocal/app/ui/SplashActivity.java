package com.hyperlocal.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.location.LocationResult;
import com.hyperlocal.app.MySharedPreferences;
import com.hyperlocal.app.locationhelper.LocationMonitoringService;
import com.hyperlocal.app.ui.boarding.WelcomeActivity;
import com.hyperlocal.app.ui.home.HomeActivity;
import com.hyperlocal.app.utlity.Constants;
import io.fabric.sdk.android.Fabric;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                MySharedPreferences mySharedpreferences = MySharedPreferences.getInstance(SplashActivity.this);
              //  mySharedpreferences.writeBoolean(Constants.IS_USER_REGISTERED,false);
                boolean isRegisteredUser = mySharedpreferences.readBoolean(Constants.IS_USER_REGISTERED);
                if (isRegisteredUser) {
                    startHomeActivity();
                } else {
                    startRegistrationActivity();
                }
            }
        }, 5000);

    }

    private void startRegistrationActivity() {
        startActivity(new Intent(this, WelcomeActivity.class));
        finish();

    }

    private void startHomeActivity() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent  serviceIntent = new Intent(getApplicationContext(),LocationMonitoringService.class);
        startService(serviceIntent);
    }
}
