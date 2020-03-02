package com.cmput301w20t10.uberapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.cmput301w20t10.uberapp.R;

public class RegisterActivity extends AppCompatActivity {

    private EditText firstNameField;
    private EditText lastNameField;
    private EditText usernameField;
    private EditText emailField;
    private EditText passwordField;
    private EditText confirmPasswordField;
    private EditText phoneField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        this.firstNameField = findViewById(R.id.first_name_field);
        this.lastNameField = findViewById(R.id.last_name_field);
        this.usernameField = findViewById(R.id.username_field);
        this.emailField = findViewById(R.id.email_field);
        this.passwordField = findViewById(R.id.password_field);
        this.confirmPasswordField = findViewById(R.id.confirm_password_field);
        this.phoneField = findViewById(R.id.phone_field);

        final Intent intent = getIntent();
        String email = intent.getStringExtra("EMAIL");
        emailField.setText(email);

    }

    public void onCancelPress(View view) {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    public void onRegisterPress(View view) {
        String password = passwordField.getText().toString();
        String conPassword = confirmPasswordField.getText().toString();

        // Check that the passwords match
        if (!password.equals(conPassword)) {
            Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_LONG).show();
            return;
        }

        // Check that the passwords match the requirements
        if (!validatePassword(password)) {
            Toast.makeText(getApplicationContext(), "Password does not meet requirements:\nMust contain a-z, A-Z and 0-9",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // Non-empty fields


        // Check that the user doesn't already have info in database
        // Submit info to database
    }

    private boolean validatePassword(@NonNull String password) {
        boolean upper = false;
        boolean lower = false;
        boolean number = false;

        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) {
                number = true;
            }
            if (Character.isUpperCase(c)) {
                upper = true;
            }
            if (Character.isLowerCase(c)) {
                lower = true;
            }
        }

        boolean length = password.length() >= 8;

        return upper && lower && number && length;
    }

}
