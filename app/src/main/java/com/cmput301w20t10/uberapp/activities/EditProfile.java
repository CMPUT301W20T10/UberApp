package com.cmput301w20t10.uberapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.cmput301w20t10.uberapp.R;


// TODO: 2020-03-13 look at comments in the button onclick listeners. Explains that it needs functionality set up first. Basically a empty activity for right now. 
public class EditProfile extends AppCompatActivity {
    Button butCancel;
    Button butSave;
    ImageButton butPicture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        butCancel = findViewById(R.id.butCancel);
        butSave = findViewById(R.id.butSave);
        butPicture = findViewById(R.id.editPicture);

        butCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Shouldn't need to do anything in this onclick, other than just return back to ProfilePage.
                finish();
            }
        });

        butSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                need to save all values/changed values/appropriate values
                then need to access the USER/profile/Driver/Rider Class
                using getters/setters, compare and save the updated values from EditProfile activity
                save to firestore database?
                use finish(); to return back to previous activity - the ProfilePage
                */
                finish();
            }
        });

        butPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                This button will be for editing/uploading a new photo.
                Will look at making this work later, but for now it will do nothing.
                No point in skeletal code as need to set it up with the database.
                wait to work on this until after the save button correctly works and saves the profile.
                LOW RISK NOT SUPER IMPORTANT...
                */
            }
        });


    }
}
