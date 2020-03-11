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
        //setContentView(R.layout.profile_page);

        /**
         * Adding our layout to parent class frame layout.
         */
        getLayoutInflater().inflate(R.layout.profile_page, frameLayout);

        /**
         * Setting title and itemChecked
         */
        mDrawerList.setItemChecked(position, true);
        setTitle(listArray[position]);

        editProfile = findViewById(R.id.butEditProf);

        /*
        StackOverflow post by Martin Sing:
        https://stackoverflow.com/users/6906943/martin-sing
        Answer:
        https://stackoverflow.com/a/41389737
        for information regarding how to go to next activity on button press.
        */
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),EditProfile.class);
                v.getContext().startActivity(intent);
            }
        });
    }


}