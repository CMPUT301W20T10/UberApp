package com.cmput301w20t10.uberapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cmput301w20t10.uberapp.R;

public class LoginActivity extends AppCompatActivity {

    private EditText emailField;
    private EditText passwordField;
    private RadioGroup loginTypeField;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.emailField = findViewById(R.id.email_field);
        this.passwordField = findViewById(R.id.password_field);
        this.loginTypeField = findViewById(R.id.rider_driver_toggle);

        // Set the selector to select Rider by default
        this.loginTypeField.check(R.id.rider_radio_button);

    }

    public void onLoginPressed(View view) {
        // Check for empty fields
        if(emailField.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Email Required", Toast.LENGTH_LONG).show();
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
        String email = emailField.getText().toString();
        intent.putExtra("EMAIL", email);
        startActivity(intent);
    }


}
