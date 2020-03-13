package com.cmput301w20t10.uberapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.cmput301w20t10.uberapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/*
* Based on Youtube Video By: AnionCode - https://www.youtube.com/channel/UCseP9k1DwSAqzZ-iyeAlTvg
* Video: https://www.youtube.com/watch?v=pThlcmRUi_s "Android Studio- #Tip1 Floating Action Button Toturial /Source code"
* For details about floating buttons and creating XML and setting up the buttons/vertical translation
*/


/**
 * BaseActivity is used to extend to activities that need the menu floating action button functionality.
 * activities extending this will need to INCLUDE float_main.xml layout to add the buttons to their layout.
 */
public class BaseActivity extends AppCompatActivity {

    FloatingActionButton fabMenu,fabHome,fabSearch,fabProfile,fabExit,fabHistory;
    boolean isOpen=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.float_main);




    }
    @Override
    public void onBackPressed() {
        if(!isOpen){
            super.onBackPressed();
        }
        else{
            closeMenu();
        }
    }

    @Override
    public void setContentView(int layout) {
        super.setContentView(layout);
        fabMenu = findViewById(R.id.floatButton);
        fabHome = findViewById(R.id.floatButtonHome);
        fabProfile = findViewById(R.id.floatButtonProfile);
        fabExit = findViewById(R.id.floatButtonLogout);
        fabSearch = findViewById(R.id.floatButtonSearch);
        fabHistory = findViewById(R.id.floatButtonHistory);

        fabMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isOpen){
                    openMenu();
                }
                else{
                    closeMenu();
                }
            }
        });
    }

    /**
     * This is called when fabMenu is clicked on and if isOpen=True
     * Animates the menu back to just only showing fabMenu and sets isOpen to false
     */
    private void closeMenu() {
        isOpen=false;
        fabHome.animate().translationY(0);
        fabProfile.animate().translationY(0);
        fabHistory.animate().translationY(0);
        fabSearch.animate().translationY(0);
        fabExit.animate().translationY(0);
    }

    /**
     * This is called when fabMenu is clicked on and if isOpen=false
     * This animates the menu to expand, sets isOpen to true and handles the onclick listeners to change activities.
     */
    private void openMenu() {
        isOpen=true;
        fabHome.animate().translationY(200);
        fabProfile.animate().translationY(350);
        fabHistory.animate().translationY(500);
        fabSearch.animate().translationY(650);
        fabExit.animate().translationY(800);

        //Begin onclickListeners for each fab button, each sends to new activity. This activity should extend baseactivity so it can also have Menu.
        fabProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BaseActivity.this, ProfilePage.class);
                startActivity(intent);
            }
        });

        fabSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BaseActivity.this,SearchProfile.class);
                startActivity(intent);
            }
        });

        fabHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BaseActivity.this,RiderMainActivity.class);
                startActivity(intent);
            }
        });

    }


}
