package com.cmput301w20t10.uberapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.Rotate;
import com.bumptech.glide.request.RequestOptions;
import com.cmput301w20t10.uberapp.Application;
import com.cmput301w20t10.uberapp.R;
import com.cmput301w20t10.uberapp.models.User;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilePage extends BaseActivity {
    Button editProfile;
    TextView fName,lName,uName,pNumber,eMail;
    CircleImageView profilePicture;

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

    /**
     * When activity opens, gather info from Database based on currentuser supplied from Application singleton.
     */
    @Override
    protected void onResume() {
        super.onResume();
        fName = findViewById(R.id.Fname);
        lName = findViewById(R.id.Lname);
        uName = findViewById(R.id.Uname);
        pNumber = findViewById(R.id.Pnumber);
        eMail = findViewById(R.id.Email);
        profilePicture = findViewById(R.id.profile_image);

        User user = Application.getInstance().getCurrentUser();


        if (user.getImage() != "") {
            Glide.with(this)
                    .load(user.getImage())
                    .apply(RequestOptions.circleCropTransform())
                    .into(profilePicture);


        }

        fName.setText(user.getFirstName());
        lName.setText(user.getLastName());
        uName.setText(user.getUsername());
        pNumber.setText(user.getPhoneNumber());
        eMail.setText(user.getEmail());
    }
}