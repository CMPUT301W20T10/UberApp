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
import android.widget.Toast;

import com.cmput301w20t10.uberapp.R;
import com.cmput301w20t10.uberapp.database.DatabaseManager;
import com.cmput301w20t10.uberapp.database.LoginRegisterDAO;
import com.cmput301w20t10.uberapp.database.RiderDAO;
import com.cmput301w20t10.uberapp.models.Driver;
import com.cmput301w20t10.uberapp.models.Rider;

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
            Log.d("Testing", "onClick_signIn: Driver");
            LoginRegisterDAO dao = DatabaseManager.getInstance().getLoginRegisterDAO();
            dao.logInAsRider("RyanBowler", "Password1",  this)
                    .observe(this, rider -> {if (rider != null) {
                        Toast.makeText(LoginActivity.this, "Creating ride request", Toast.LENGTH_SHORT).show();
                    } else{
                        Toast.makeText(LoginActivity.this, "Failure", Toast.LENGTH_SHORT).show();
                    }
                    });
        }

    }

    public void onLoginPressed(View view) {
        // Check for empty fields
        if(usernameField.getText().toString().isEmpty()) {
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
        String email = usernameField.getText().toString();
        intent.putExtra("EMAIL", email);
        startActivity(intent);
    }


}
