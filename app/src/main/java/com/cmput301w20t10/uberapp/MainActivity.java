package com.cmput301w20t10.uberapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button buttonDriverTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonDriverTest = findViewById(R.id.button_test_driver);
        buttonDriverTest.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, DriverMainActivity.class);
            startActivity(intent);
        });
    }
}
