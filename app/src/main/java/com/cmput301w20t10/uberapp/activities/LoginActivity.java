package com.cmput301w20t10.uberapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
    private static final int REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }

        radioButtonRider = findViewById(R.id.rider_radio_button);
        buttonLogIn = findViewById(R.id.button_log_in);

        buttonLogIn.setOnClickListener(view -> onClick_signIn());
        
        this.usernameField = findViewById(R.id.username_field);
        this.passwordField = findViewById(R.id.password_field);
        this.loginTypeField = findViewById(R.id.rider_driver_toggle);

        // Set the selector to select Rider by default
        this.loginTypeField.check(R.id.rider_radio_button);
    }

    private void onClick_signIn() {
        // todo: proper implementation of sign in
        if (radioButtonRider.isChecked()) {
            Intent intent = new Intent(this, RiderMainActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, DriverMainActivity.class);
            startActivity(intent);
            /**
            Log.d("Testing", "onClick_signIn: Driver");
//            DatabaseManager.getInstance().registerRider("Appletun",
//                    "yum yum jelly jelly",
//                    "mrmr@gmail.com",
//                    "Appletun",
//                    "3.14",
//                    "123",
//                    this);
            DatabaseManager.getInstance().logInAsDriver(
                    "Thomas", "choo choo", this)
                    .observe(this, driver -> Log.d("Testing", "onChanged: Success: " + (driver != null)));
             **/
        }

    }

    public void onLoginPressed(View view) {
        // Check for empty fields
        if(usernameField.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Username Required", Toast.LENGTH_LONG).show();
            return;
        }

        if(passwordField.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Password Required", Toast.LENGTH_LONG).show();
            return;
        }

        // Todo: Send login details to database for login validation
        // if the email doesn't exist, should we transition to register screen automatically?
        // Todo: Transition to appropriate screen (Rider/Driver)
    }

    public void onRegisterPressed(View view) {
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        String username = usernameField.getText().toString();
        intent.putExtra("USERNAME", username);
        startActivity(intent);
    }


}
