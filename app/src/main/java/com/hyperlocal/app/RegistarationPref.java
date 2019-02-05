package com.hyperlocal.app;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.hyperlocal.app.ui.registration.UserDataModel;

/**
 * @author ${Umesh} on 08-06-2018.
 */

public class RegistarationPref  {

    public static final String PREFERENCES = "registration";

    private static Context mContext;
    private static SharedPreferences sharedPreference;
    private static RegistarationPref mySharedpreferences;
    private static SharedPreferences.Editor editor;

    private RegistarationPref() {

    }

    public static RegistarationPref getInstance(Context context) {
        mContext = context;
        if (mySharedpreferences == null) {
            mySharedpreferences = new RegistarationPref();
            initializePref();
        }
        return mySharedpreferences;
    }

    private static void initializePref() {
        sharedPreference = mContext.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreference.edit();
    }

    public void writeObject(String key,UserDataModel userProfileDataModel){
        Gson gson = new Gson();
        String obj = gson.toJson(userProfileDataModel);
        editor.putString(key,obj).apply();
    }

    public UserDataModel readObject(String key){
        Gson gson = new Gson();
        String json = sharedPreference.getString(key, "");
        return gson.fromJson(json, UserDataModel.class);
    }
}
