package com.cmput301w20t10.uberapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.MutableLiveData;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.cmput301w20t10.uberapp.Application;
import com.cmput301w20t10.uberapp.R;
import com.cmput301w20t10.uberapp.database.DatabaseManager;
import com.cmput301w20t10.uberapp.models.Driver;
import com.cmput301w20t10.uberapp.models.Rider;
import com.google.firebase.iid.FirebaseInstanceId;

/**
 * @author Joshua Mayer
 * @version 1.0.2
 */
public class LoginActivity extends OptionsMenu {

    private RadioButton radioButtonRider;
    private Button buttonLogIn;
    private EditText usernameField;
    private EditText passwordField;
    private RadioGroup loginTypeField;
    private CheckBox rememberBox;

    SharedPref sharedPref;

    private static final int REQUEST_CODE = 101;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState() == true) {
            setTheme(R.style.DarkTheme);
        } else { setTheme(R.style.AppTheme); }
        setContentView(R.layout.activity_login);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("UBER", task.getException());
                        return;
                    }
                    String token = task.getResult().getToken();

                    Application.getInstance().setMessagingToken(token);
                    Log.d("UBER", token);
                });

        radioButtonRider = findViewById(R.id.rider_radio_button);
        buttonLogIn = findViewById(R.id.button_log_in);
        rememberBox = findViewById(R.id.remember_box);

        buttonLogIn.setOnClickListener(this::onLoginPressed);

        this.usernameField = findViewById(R.id.username_field);
        this.passwordField = findViewById(R.id.password_field);
        this.loginTypeField = findViewById(R.id.rider_driver_toggle);

        if (sharedPref.loadRememberMeState()) {
            usernameField.setText(sharedPref.loadUsername());
            passwordField.setText(sharedPref.loadPassword());
            if (sharedPref.loadUserType().equals("rider")) {
                this.loginTypeField.check(R.id.rider_radio_button);
            } else {
                this.loginTypeField.check(R.id.driver_radio_button);
            }
            verifyLogin();
        }

        // Set the selector to select Rider by default
        this.loginTypeField.check(R.id.rider_radio_button);

        rememberBox.setOnCheckedChangeListener((compoundButton, b) -> {
            if (compoundButton.isChecked()) {
                sharedPref.setRememberMe(true);
            } else {
                sharedPref.setRememberMe(false);
            }
        });
    }

    public void onLoginPressed(View view) {
        Log.d("Testing", "Verify Fields");
        // Check for empty fields
        if(usernameField.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Username Required", Toast.LENGTH_LONG).show();
            return;
        }

        if(passwordField.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Password Required", Toast.LENGTH_LONG).show();
            return;
        }

        verifyLogin();
    }

    private void verifyLogin() {
        // todo: proper implementation of sign in
        if (hasNetwork()) {
            if (radioButtonRider.isChecked()) {
                //login as rider
                Log.d("Testing", "Log in as rider!");
                MutableLiveData<Rider> liveRider = DatabaseManager.getInstance().logInAsRider(
                        usernameField.getText().toString(), passwordField.getText().toString(), this);
                liveRider.observe(this, rider -> {
                    if (rider != null) {
                        Log.d("Testing", "Login Success");
                        Application.getInstance().setUser(rider);
                        saveUserInfo("rider");
                        Intent intent = new Intent(this, RiderMainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid Username/Password", Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                Log.d("Testing", "Log in as driver!");
                MutableLiveData<Driver> liveRider = DatabaseManager.getInstance().logInAsDriver(
                        usernameField.getText().toString(), passwordField.getText().toString(), this);
                liveRider.observe(this, driver -> {
                    if (driver != null) {
                        Log.d("Testing", "Login Success");
                        Log.d("Testing", "Driver Main Activity not yet in this branch");
                        Application.getInstance().setUser(driver);
                        saveUserInfo("driver");
                        if (driver.getActiveRideRequestList() != null && driver.getActiveRideRequestList().size() > 0 ) {
                            Intent intent = new Intent(this, DriverAcceptedActivity.class);
                            String activeRideRequest = driver.getActiveRideRequestList().get(0).getPath();
                            intent.putExtra("ACTIVE", activeRideRequest);
                            intent.putExtra("PREV_ACTIVITY", "LoginActivity");
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(this, DriverMainActivity.class);
                            intent.putExtra("PREV_ACTIVITY", "LoginActivity");
                            startActivity(intent);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid Username/Password", Toast.LENGTH_LONG).show();
                    }
                });
            }
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();

        }
    }

    public void onRegisterPressed(View view) {

        // NotificationService.sendNotification("Register Pressed", "You pressed the register button!", getApplicationContext(), RegisterActivity.class);

        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        String username = usernameField.getText().toString();
        intent.putExtra("USERNAME", username);
        startActivity(intent);
    }

    public void saveUserInfo(String userType) {
        if (rememberBox.isChecked()) {
            sharedPref.setUsername(usernameField.getText().toString());
            sharedPref.setPassword(passwordField.getText().toString());
            sharedPref.setUserType(userType);
        }
    }

    public boolean hasNetwork() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }

}
