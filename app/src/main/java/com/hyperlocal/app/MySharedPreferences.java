package com.hyperlocal.app;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyperlocal.app.model.ActivityItemDataModel;
import com.hyperlocal.app.ui.profile.UserProfileDataModel;
import com.hyperlocal.app.ui.registration.UserDataModel;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * @author ${Umesh} on 17-04-2018.
 */

public class MySharedPreferences {

    public static final String PREFERENCES = "hyper_local";

    private static Context mContext;
    private static SharedPreferences sharedPreference;
    private static MySharedPreferences mySharedpreferences;
    private static SharedPreferences.Editor editor;

    private MySharedPreferences() {

    }

    public static MySharedPreferences getInstance(Context context) {
        mContext = context;
        if (mySharedpreferences == null) {
            mySharedpreferences = new MySharedPreferences();
            initializePref();
        }
        return mySharedpreferences;
    }

    private static void initializePref() {
        sharedPreference = mContext.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreference.edit();
    }

    public void writeString(String key, String value) {
        editor.putString(key, value).commit();
    }

    public String readString(String key) {
        return sharedPreference.getString(key, null);
    }

    public void writeIteger(String key, int value) {
        editor.putInt(key, value);
    }

    public int readInteger(String key) {
        return sharedPreference.getInt(key, 0);
    }

    public void writeLong(String key,long value){
        editor.putLong(key,value).apply();
    }


    public long readLong(String key){
        return sharedPreference.getLong(key, 0L);
    }

    public void writeBoolean(String key, boolean value) {
        editor.putBoolean(key, value).commit();

    }

    public void writeObject(String key,UserProfileDataModel userProfileDataModel){
        Gson gson = new Gson();
        String obj = gson.toJson(userProfileDataModel);
        editor.putString(key,obj).apply();
    }

    public UserProfileDataModel readObject(String key){
        Gson gson = new Gson();
        String json = sharedPreference.getString(key, "");
        return gson.fromJson(json, UserProfileDataModel.class);
    }


    public void writeUserDataModel(String key, UserDataModel userDataModel){
        Gson gson = new Gson();
        String obj = gson.toJson(userDataModel);
        editor.putString(key,obj).apply();
    }

    public UserDataModel readUserDataModel(String key){
        Gson gson = new Gson();
        String json = sharedPreference.getString(key, "");
        return gson.fromJson(json, UserDataModel.class);
    }


    public boolean readBoolean(String key) {
        return sharedPreference.getBoolean(key, false);
    }

    public void setArrayList(String key, ArrayList<ActivityItemDataModel> obj) {
        Gson gson = new Gson();
        String json = gson.toJson(obj);
        editor.putString(key, json);
        editor.commit();
    }

    public ArrayList<ActivityItemDataModel> getArrayList(String key) {
        Gson gson = new Gson();
        String json = sharedPreference.getString(key, "");
        Type type = new TypeToken<ArrayList<ActivityItemDataModel>>() {}.getType();
        return gson.fromJson(json, type);
    }
    public void clearPref(){
        sharedPreference.edit().clear().commit();
    }

}
