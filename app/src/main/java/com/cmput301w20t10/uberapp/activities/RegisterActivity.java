package com.cmput301w20t10.uberapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.cmput301w20t10.uberapp.R;
import com.cmput301w20t10.uberapp.database.DatabaseManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Joshua Mayer
 * @version 1.0.3
 */
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
        String username = intent.getStringExtra("USERNAME");
        usernameField.setText(username);

    }

    public void onCancelPress(View view) {
        finish();
    }

    public void onRegisterPress(View view) {
        String password = passwordField.getText().toString();
        String conPassword = confirmPasswordField.getText().toString();
        String email = emailField.getText().toString();
        String phone = phoneField.getText().toString();

        // Non-empty fields
        if(!verifyFields()) {
            return;
        }

        // Check that the passwords match
        if (!password.equals(conPassword)) {
            Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_LONG).show();
            return;
        }

        // Check that the passwords match the requirements
        if (!validatePassword(password)) {
            Toast.makeText(getApplicationContext(), "Password does not meet requirements:\nMinimum 8 characters long\nMust contain a-z, A-Z and 0-9",
                    Toast.LENGTH_LONG).show();
            return;
        }

        //Check that the email is valid
        if (!validateEmail(email)) {
            Toast.makeText(getApplicationContext(), "Email entered is not valid",
                    Toast.LENGTH_LONG).show();
            return;
        }

        //Check that the phone is valid
        if (!validatePhone(phone)) {
            Toast.makeText(getApplicationContext(), "Phone number entered is not valid",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // Check that the user doesn't already have info in database


        // Submit info to database
        // Todo(Joshua): Verify this is the proper way to add user to DB
        DatabaseManager.getInstance().registerRider(
                usernameField.getText().toString(),
                    passwordField.getText().toString(),
                    emailField.getText().toString(),
                    firstNameField.getText().toString(),
                    lastNameField.getText().toString(),
                    phoneField.getText().toString(),
                    this);
        Toast.makeText(getApplicationContext(), "Successfully registered", Toast.LENGTH_LONG).show();
        finish();
    }

    /**
     * Checks the fields in the register activity to ensure that the user has not left
     * any blank. If any are found blank, the user is notified.
     *
     * @return True if the fields are valid, false otherwise
     */
    private boolean verifyFields() {
        if(firstNameField.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "First name field empty!", Toast.LENGTH_LONG).show();
            return false;
        }

        if(lastNameField.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Last name field empty!", Toast.LENGTH_LONG).show();
            return false;
        }

        if(usernameField.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Username field empty!", Toast.LENGTH_LONG).show();
            return false;
        }

        if(emailField.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Email field empty!", Toast.LENGTH_LONG).show();
            return false;
        }

        if(phoneField.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Phone number field empty!", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    /**
     * Validates that the email is valid
     *
     * @param email - The email to be validated
     *
     * @return - True if the validation succeeds, false otherwise
     */
    private boolean validateEmail(@NonNull String email) {
        String emailFormat = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w]+$";
        Pattern pattern = Pattern.compile(emailFormat);
        Matcher matcher = pattern.matcher(email);
        return matcher.find();
    }

    private boolean validatePhone(@NonNull String phone) {
        String phoneFormat = "^[0-9]{10}$";
        Pattern pattern = Pattern.compile(phoneFormat);
        Matcher matcher = pattern.matcher(phone);
        return matcher.find();
    }

    /**
     * Validates that the password follows the requirements. One upper and lowercase letter,
     * one number and a minimum of 8 characters.
     *
     * @param password - The password to be validated
     *
     * @return - True if the validation succeeds, false if there is an error
     */
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
