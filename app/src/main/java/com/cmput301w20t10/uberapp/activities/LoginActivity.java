package com.cmput301w20t10.uberapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioButton;

import com.cmput301w20t10.uberapp.R;

public class LoginActivity extends AppCompatActivity {

    private RadioButton radioButtonRider;
    private Button buttonLogIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        radioButtonRider = findViewById(R.id.rider_radio_button);
        buttonLogIn = findViewById(R.id.button_log_in);

        buttonLogIn.setOnClickListener(view -> onClick_signIn());
    }

    private void onClick_signIn() {
        // todo: proper implementation of sign in
        if (radioButtonRider.isChecked()) {
            Intent intent = new Intent(this, RiderMainActivity.class);
            startActivity(intent);
        } else {
            Log.d("Testing", "onClick_signIn: Driver");
        }
    }
}
