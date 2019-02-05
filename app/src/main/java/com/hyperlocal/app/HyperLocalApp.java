package com.hyperlocal.app;

import android.app.Application;
import android.content.Context;


import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;


/**
 * @Author ${Umesh} on 20-03-2018.
 */
@ReportsCrashes(
        mailTo = "hyperlocaldev123@gmail.com",
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast_text)

public class HyperLocalApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        MySharedPreferences.getInstance(getApplicationContext());

    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        ACRA.init(this);
    }

}
