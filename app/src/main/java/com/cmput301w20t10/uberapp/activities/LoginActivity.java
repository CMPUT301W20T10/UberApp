package com.cmput301w20t10.uberapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import com.cmput301w20t10.uberapp.R;

public class LoginActivity extends AppCompatActivity {

    private RadioButton radioButtonDriver;
    private Button buttonLogIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        radioButtonDriver = findViewById(R.id.driver_radio_button);
        buttonLogIn = findViewById(R.id.button_log_in);

        buttonLogIn.setOnClickListener(view -> onClick_signIn());
    }

    private void onClick_signIn() {
        // todo: proper implementation of sign in
        if (radioButtonDriver.isChecked()) {
            Intent intent = new Intent(this, DriverMainActivity.class);
            startActivity(intent);
        } else {
            Log.d("Testing", "onClick_signIn: Rider");
        }
    }
}
