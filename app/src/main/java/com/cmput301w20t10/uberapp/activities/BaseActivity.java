package com.cmput301w20t10.uberapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cmput301w20t10.uberapp.LogOut;
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

    private FloatingActionButton fabMenu;
    private FloatingActionButton fabHome;
    private FloatingActionButton fabSearch;
    private FloatingActionButton fabProfile;
    private FloatingActionButton fabDarkMode;
    private FloatingActionButton fabExit;
    private FloatingActionButton fabHistory;

    private boolean isOpen = false;

    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.float_main);
    }

    @Override
    public void onBackPressed() {
        if (!isOpen) {
            if (getIntent().getExtras().getString("PREV_ACTIVITY").equals("LoginActivity")) {
                return;
            } else {
                super.onBackPressed();
            }
        } else {
            closeMenu();
        }
    }

    @Override
    public void setContentView(int layout) {
        super.setContentView(layout);
        fabMenu = findViewById(R.id.floatButton);
        fabHome = findViewById(R.id.floatButtonHome);
        fabProfile = findViewById(R.id.floatButtonProfile);
        fabDarkMode = findViewById(R.id.floatButtonSettings);
        fabExit = findViewById(R.id.floatButtonLogout);
        fabSearch = findViewById(R.id.floatButtonSearch);
        fabHistory = findViewById(R.id.floatButtonHistory);

        sharedPref = new SharedPref(this);

        fabMenu.setOnClickListener(v -> {
            if (!isOpen) {
                openMenu();
            } else {
                closeMenu();
            }
        });
    }

    /**
     * This is called when fabMenu is clicked on and if isOpen=True
     * Animates the menu back to just only showing fabMenu and sets isOpen to false
     */
    private void closeMenu() {
        isOpen = false;
        fabHome.animate().translationY(0);
        fabProfile.animate().translationY(0);
        fabDarkMode.animate().translationY(0);
        fabHistory.animate().translationY(0);
        fabSearch.animate().translationY(0);
        fabExit.animate().translationY(0);
    }

    /**
     * This is called when fabMenu is clicked on and if isOpen=false
     * This animates the menu to expand, sets isOpen to true and handles the onclick listeners to change activities.
     */
    private void openMenu() {
        isOpen = true;
        fabHome.animate().translationY(200);
        fabProfile.animate().translationY(350);
        fabHistory.animate().translationY(500);
        fabSearch.animate().translationY(650);
        fabDarkMode.animate().translationY(800);
        fabExit.animate().translationY(950);

        //Begin onclickListeners for each fab button, each sends to new activity. This activity should extend baseactivity so it can also have Menu.
        fabProfile.setOnClickListener(v -> {
            Intent intent = new Intent(BaseActivity.this, ProfilePage.class);
            startActivity(intent);
        });

        fabSearch.setOnClickListener(v -> {
            Intent intent = new Intent(BaseActivity.this, SearchProfile.class);
            startActivity(intent);
        });

        fabHome.setOnClickListener(v -> {
            Intent intent = new Intent(BaseActivity.this, RiderMainActivity.class);
            startActivity(intent);
        });

        fabDarkMode.setOnClickListener(v -> {
            if (sharedPref.loadNightModeState() == true) {
                sharedPref.setNightModeState(false);
            } else {
                sharedPref.setNightModeState(true);
            }
            notifyRestart();
        });

        fabExit.setOnClickListener(v -> {
//            sharedPref.eraseContents();
//            Intent intent = getBaseContext().getPackageManager()
//                    .getLaunchIntentForPackage( getBaseContext().getPackageName() );
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent);
//            finish();
            LogOut.clearRestart(this);
        });
    }

    public void notifyRestart() {
        Toast toast = Toast.makeText(this, "DARK MODE TOGGLED\nRESTART REQUIRED", Toast.LENGTH_SHORT);
        TextView textView = toast.getView().findViewById(android.R.id.message);
        textView.setGravity(Gravity.CENTER);
        toast.show();
    }
}