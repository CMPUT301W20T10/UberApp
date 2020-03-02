package com.cmput301w20t10.uberapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

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

    }

    public void onRegisterPressed(View view) {
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        String email = emailField.getText().toString();
        intent.putExtra("EMAIL", email);
        startActivity(intent);
    }


}
