package com.hyperlocal.app.locationhelper;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import static android.content.ContentValues.TAG;

/**
 * @author ${Umesh} on 29-05-2018.
 */

public class LocationMonitoringService extends Service implements

        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public GoogleApiClient mLocationClient;
    LocationRequest mLocationRequest;
    private boolean misRandomGenertorOn;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        misRandomGenertorOn = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                mLocationClient = new GoogleApiClient.Builder(LocationMonitoringService.this)
                        .addConnectionCallbacks(LocationMonitoringService.this)
                        .addOnConnectionFailedListener(LocationMonitoringService.this)
                        .addApi(LocationServices.API)
                        .build();

                mLocationRequest.setInterval(10000); //10 secs
                mLocationRequest.setFastestInterval(5000); //5 secs
                int priority = LocationRequest.PRIORITY_HIGH_ACCURACY; //by default
                //PRIORITY_BALANCED_POWER_ACCURACY, PRIORITY_LOW_POWER, PRIORITY_NO_POWER are the other priority modes
                mLocationRequest.setPriority(priority);
                mLocationClient.connect();
                //Make it stick to the notification panel so it is less prone to get cancelled by the Operating System.

            }
        }).start();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*
    * LOCATION CALLBACKS
    */
    @Override
    public void onConnected(Bundle dataBundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            Log.d(TAG, "== Error On onConnected() Permission not granted");
            //Permission not granted by user so cancel the further execution.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, mLocationRequest, this);

        Log.d(TAG, "Connected to Google API");
    }

    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Failed to connect to Google API");
    }

    //to get the location change
    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Location changed");

        if (location != null) {

            //Do something with the location details,
            if (location != null) {

                Toast.makeText(this, ""+location, Toast.LENGTH_SHORT).show();
                //call your API
               // callAPI(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
            }

        }
    }
}