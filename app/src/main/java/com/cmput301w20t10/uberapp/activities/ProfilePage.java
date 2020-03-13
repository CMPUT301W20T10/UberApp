package com.cmput301w20t10.uberapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cmput301w20t10.uberapp.R;

public class ProfilePage extends BaseActivity {
    Button editProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page);

        editProfile = findViewById(R.id.butEditProf);

// TODO: 2020-03-13  Need to add functionality - update info based on who is logged in.


        editProfile.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(),EditProfile.class);
            v.getContext().startActivity(intent);
        });
    }


}