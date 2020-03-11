package com.cmput301w20t10.uberapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioButton;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.cmput301w20t10.uberapp.R;
import com.cmput301w20t10.uberapp.database.DatabaseManager;
import com.cmput301w20t10.uberapp.models.*;

/**
 * @author Joshua Mayer
 * @version 1.0.2
 */
public class LoginActivity extends AppCompatActivity {

    private RadioButton radioButtonRider;
    private Button buttonLogIn;
    private EditText usernameField;
    private EditText passwordField;
    private RadioGroup loginTypeField;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        radioButtonRider = findViewById(R.id.rider_radio_button);
        buttonLogIn = findViewById(R.id.button_log_in);

        buttonLogIn.setOnClickListener(view -> onLoginPressed());
        
        this.usernameField = findViewById(R.id.username_field);
        this.passwordField = findViewById(R.id.password_field);
        this.loginTypeField = findViewById(R.id.rider_driver_toggle);

        // Set the selector to select Rider by default
        this.loginTypeField.check(R.id.rider_radio_button);
    }

    public void onLoginPressed() {
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

        if (radioButtonRider.isChecked()) {
            //login as rider
            Log.d("Testing", "Log in as rider!");
            MutableLiveData<Rider> liveRider = DatabaseManager.getInstance().logInAsRider(
                    usernameField.getText().toString(), passwordField.getText().toString(), this);
            liveRider.observe(this, rider -> {
                if (rider != null) {
                    Log.d("Testing", "Login Success");
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
                    /*Intent intent = new Intent(this, DriverMainActivity.class);
                    startActivity(intent);*/
                } else {
                    Toast.makeText(getApplicationContext(), "Invalid Username/Password", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void onRegisterPressed(View view) {
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        String username = usernameField.getText().toString();
        intent.putExtra("USERNAME", username);
        startActivity(intent);
    }


}
