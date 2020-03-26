package com.cmput301w20t10.uberapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cmput301w20t10.uberapp.Application;
import com.cmput301w20t10.uberapp.R;
import com.cmput301w20t10.uberapp.models.User;

public class ProfilePage extends BaseActivity {
    Button editProfile;
    TextView fName,lName,uName,pNumber,eMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page);
        editProfile = findViewById(R.id.butEditProf);


        editProfile.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(),EditProfile.class);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fName = findViewById(R.id.Fname);
        lName = findViewById(R.id.Lname);
        uName = findViewById(R.id.Uname);
        pNumber = findViewById(R.id.Pnumber);
        eMail = findViewById(R.id.Email);

        User user = Application.getInstance().getCurrentUser();

        fName.setText(user.getFirstName());
        lName.setText(user.getLastName());
        uName.setText(user.getUsername());
        pNumber.setText(user.getPhoneNumber());
        eMail.setText(user.getEmail());
    }
}