package com.cmput301w20t10.uberapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioButton;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.cmput301w20t10.uberapp.R;
import com.cmput301w20t10.uberapp.database.DatabaseManager;
import com.cmput301w20t10.uberapp.database.viewmodel.DriverRegistrationViewModel;
import com.cmput301w20t10.uberapp.database.viewmodel.RiderViewModel;
import com.cmput301w20t10.uberapp.models.Driver;
import com.cmput301w20t10.uberapp.models.Rider;

public class LoginActivity extends AppCompatActivity {

    private RadioButton radioButtonRider;
    private Button buttonLogIn;
    private EditText emailField;
    private EditText passwordField;
    private RadioGroup loginTypeField;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        radioButtonRider = findViewById(R.id.rider_radio_button);
        buttonLogIn = findViewById(R.id.button_log_in);

        buttonLogIn.setOnClickListener(view -> onClick_signIn());
        
        this.emailField = findViewById(R.id.email_field);
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
        }

    }

    public void onLoginPressed(View view) {

    }

    public void onRegisterPressed(View view) {

    }


}
