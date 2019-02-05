package com.hyperlocal.app.ui.registration;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hyperlocal.app.MySharedPreferences;
import com.hyperlocal.app.R;
import com.hyperlocal.app.ui.BaseActivity;
import com.hyperlocal.app.ui.home.HomeActivity;
import com.hyperlocal.app.utlity.Constants;
import com.hyperlocal.app.utlity.Validator;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * @author ${Umesh} on 17-04-2018.
 */

public class PasswordActivity extends BaseActivity {
    private String TAG = PasswordActivity.class.getSimpleName();

    @BindView(R.id.linear_layout) LinearLayout linearLayout;
    @BindView(R.id.edit_password) EditText passwordEditText;
    @BindView(R.id.address_input_text)
    TextInputLayout inputType;

    private MySharedPreferences mySharedpreferences;
    private Location location;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        ButterKnife.bind(this);
        mySharedpreferences = MySharedPreferences.getInstance(PasswordActivity.this);

        validateField();
    }


    private void updateFireBaseTokenToServer() {
        long timeStamp = System.currentTimeMillis();
        mySharedpreferences.writeLong(Constants.TIME_STAMP,timeStamp);
        SharedPreferences preferences = getSharedPreferences("fcm_token_pref", MODE_PRIVATE);
        String fcmToke = preferences.getString(Constants.FCM_TOKEN, null);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference().child("users").
                child(mySharedpreferences.readString(Constants.USER_ID));
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //progressBar.setVisibility(View.GONE);
                Map<String, Object> postValues = new HashMap<String, Object>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    postValues.put(snapshot.getKey(), snapshot.getValue());
                }
                if(location!=null){
                  postValues.put("latitude",location.getLatitude());
                  postValues.put("longitude",location.getLongitude());
                }
                postValues.put("device_token", fcmToke);
                postValues.put("loginStatus", "1");
                postValues.put("device_type","1");
                postValues.put("timestamp",timeStamp);
                reference.updateChildren(postValues).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        startActivity(new Intent(PasswordActivity.this, HomeActivity.class));
                        finish();
                    }
                });
                reference.removeEventListener(this);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //progressBar.setVisibility(View.GONE);
            }
        });


    }

    private void validateField() {
        RxTextView.afterTextChangeEvents(passwordEditText)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(changeEvent -> {
                    CharSequence s = changeEvent.view().getText();
                    if (Validator.isValidPassword(s.toString())) {
                        inputType.setErrorEnabled(false);
                    } else {
                        inputType.setError(getString(R.string.please_enter_password));
                        requestFocus(inputType);
                    }
                });
    }

    @OnClick(R.id.btn_next)
    public void nextButtonClick() {
        String password = passwordEditText.getText().toString();
        if (TextUtils.isEmpty(password)) {
            showSnackBar();
        } else if (password.equalsIgnoreCase(mySharedpreferences.readString(Constants.PASSWORD))) {
            updateFireBaseTokenToServer();
            } else {
            showSnackBar();
        }
    }

    private void showSnackBar() {
      Toast  toast =  Toast.makeText(this, "Please enter valid password", Toast.LENGTH_SHORT);
      toast.setGravity(Gravity.TOP | Gravity.CENTER,0,0);
      toast.show();
      Snackbar.make(linearLayout, "Please enter valid password", Snackbar.LENGTH_SHORT).show();
    }


    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void locationUpdated(Location location) {
        super.locationUpdated(location);
        this.location = location;

    }
}
