package com.hyperlocal.app.ui.registration.fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.hyperlocal.app.R;
import com.hyperlocal.app.ui.registration.fragment.adapter.PlacesAutoCompleteAdapter;
import com.hyperlocal.app.ui.registration.fragment.adapter.RecyclerItemClickListener;
import com.jakewharton.rxbinding2.widget.RxTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static android.app.Activity.RESULT_OK;

/**
 * @Author ${Umesh} on 02-04-2018.
 */

public class RegAddressFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    @BindView(R.id.edit_address)
    EditText addressEditText;
    @BindView(R.id.address_input_text)
    TextInputLayout inputType;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    private static final int PERMISSION_REQUEST_CODE = 100;
    protected GoogleApiClient mGoogleApiClient;

    private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(new LatLng(0, 0), new LatLng(0, 0));
    private EditText mAutocompleteView;
    private LinearLayoutManager mLinearLayoutManager;
    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reg_address_fragment, container, false);
        ButterKnife.bind(this, view);
        buildGoogleApiClient();

        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mAutoCompleteAdapter =
                new PlacesAutoCompleteAdapter(getActivity(), R.layout.address_row, mGoogleApiClient, BOUNDS_INDIA, null);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mAutoCompleteAdapter);

        //delete.setOnClickListener(this);


        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        final PlacesAutoCompleteAdapter.PlaceAutocomplete item = mAutoCompleteAdapter.getItem(position);
                        String string = (String) item.description;
                        final String placeId = String.valueOf(item.placeId);
                        addressEditText.setText(string);
                        mRecyclerView.setVisibility(View.GONE);

                        Log.i("TAG", "Autocomplete item selected: " + item.description);

                        /*

                             Issue a request to the Places Geo Data API to retrieve a Place object with additional details about the place.

                         */


                        PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi

                                .getPlaceById(mGoogleApiClient, placeId);

                        placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {

                            @Override

                            public void onResult(PlaceBuffer places) {

                                if (places.getStatus().isSuccess()) {
                                    final Place myPlace = places.get(0);
                                    LatLng queriedLocation = myPlace.getLatLng();
                                    Log.v("Latitude is", "" + queriedLocation.latitude);
                                    Log.v("Longitude is", "" + queriedLocation.longitude);


                                    //Do the things here on Click.....

                                    //Toast.makeText(getApplicationContext(),String.valueOf(places.get(0).getLatLng()),Toast.LENGTH_SHORT).show();

                                } else {

                                    // Toast.makeText(getApplicationContext(),Constants.SOMETHING_WENT_WRONG,Toast.LENGTH_SHORT).show();

                                }
                                places.release();
                            }

                        });

                        Log.i("TAG", "Clicked: " + item.description);

                        Log.i("TAG", "Called getPlaceById to get Place details for " + item.placeId);

                    }

                })

        );

        addressEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(charSequence)) {
                    mRecyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (!s.toString().equals("") && mGoogleApiClient.isConnected()) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mAutoCompleteAdapter.getFilter().filter(s.toString());
                } else {
                    mRecyclerView.setVisibility(View.GONE);
                }
                if (!mGoogleApiClient.isConnected()) {
                    //Toast.makeText(getActivity(), "skvldv", Toast.LENGTH_SHORT).show();

                    // Log.e(Constants.PlacesTag,Constants.API_NOT_CONNECTED);
                }


            }


            @Override
            public void afterTextChanged(Editable editable) {


            }
        });

        showValidationMessage();
        return view;
    }


    @OnClick(R.id.edit_address)
    public void onEditAddressClick() {




       /* if (FunctionUtil.isLocationEnabled(getActivity())) {
           if(checkSelfPermission()){
               try {
                   Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(getActivity());
                   startActivityForResult(intent, 1);
               } catch (GooglePlayServicesRepairableException e) {
                       e.printStackTrace();
               } catch (GooglePlayServicesNotAvailableException e) {
                   e.printStackTrace();
               }
           }
        }*/
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


    public String getAddress() {
        return addressEditText.getText().toString();
    }


    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


    public void showValidationMessage() {
        RxTextView.afterTextChangeEvents(addressEditText).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(changeEvent -> isInputTextIsValid());
    }


    private boolean isInputTextIsValid() {
        if (addressEditText.getText().toString().trim().isEmpty()) {
            inputType.setError(getString(R.string.please_enter_address));
            requestFocus(inputType);
            inputType.setErrorEnabled(false);
            return false;
        } else {
            inputType.setErrorEnabled(false);
        }
        return true;
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
    public void onClick(View v) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.v("Google API Callback", "Connection Done");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.v("Google API Callback", "Connection Suspended");

        Log.v("Code", String.valueOf(i));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.v("Google API Callback", "Connection Failed");

        Log.v("Error Code", String.valueOf(connectionResult.getErrorCode()));

        //Toast.makeText(this, Constants.API_NOT_CONNECTED,Toast.LENGTH_SHORT).show();

    }


    @Override

    public void onResume() {
        super.onResume();
        if (!mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()) {

            Log.v("Google API", "Connecting");

            mGoogleApiClient.connect();
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {

            Log.v("Google API", "Dis-Connecting");

            mGoogleApiClient.disconnect();

        }

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();
    }
}
