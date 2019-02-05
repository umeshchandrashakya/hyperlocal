package com.hyperlocal.app.locationhelper;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.Task;

import static android.content.ContentValues.TAG;

/**
 * @author ${Umesh} on 07-05-2018.
 */

public class CurrentLocationListener extends LiveData<Location> implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    public static CurrentLocationListener instance;
    public GoogleApiClient googleApiClient;
    private  Context context;
    private PendingResult<LocationSettingsResult> result;
    private  Activity activity1;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final int CHECK_SELF_PERMISSION = 200;
    private Location location;


    public CurrentLocationListener(Context context ,Activity activity){
        this.context = context;
        this.activity1 = activity;
        buildGoogleApiClient(context);
    }

    private synchronized void buildGoogleApiClient(Context appContext) {
        Log.d(TAG, "Build google api client");
        googleApiClient = new GoogleApiClient.Builder(appContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        }

    @Override
    protected void onActive() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity1, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        } else {
         return;
        }
        googleApiClient.connect();
    }

    @Override
    protected void onInactive() {
        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Location changed received: " + location);
        this.location = location;
       // Toast.makeText(context, ""+location, Toast.LENGTH_SHORT).show();
        Log.d("Location Changed",""+location);
     //   Toast.makeText(context, ""+location, Toast.LENGTH_SHORT).show();
        setValue(location);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "connected to google api client");
        if (ActivityCompat.checkSelfPermission(context,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (lastLocation != null) {
            setValue(lastLocation);
        } else {
            Log.e(TAG, "onConnected: last location value is NULL");
        }
        if (hasActiveObservers() && googleApiClient.isConnected()) {
            LocationRequest locationRequest = LocationRequest.create();
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.w(TAG, "On Connection suspended " + i);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "GoogleApiClient connection has failed " + connectionResult);
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CHECK_SELF_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                  //  Toast.makeText(activity1, "", Toast.LENGTH_SHORT).show();
                }
            }
            //return;
        }
    }


    public void enableLocationDialog() {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(5 * 1000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(activity1).checkLocationSettings(builder.build());
        result.addOnCompleteListener(task -> {
            try {
                LocationSettingsResponse response = task.getResult(ApiException.class);
            } catch (ApiException exception) {
                switch (exception.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            ResolvableApiException resolvable = (ResolvableApiException) exception;
                            resolvable.startResolutionForResult(activity1, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        } catch (ClassCastException e) {
                            e.printStackTrace();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    public Location getLocation(){
        return location;
    }



}