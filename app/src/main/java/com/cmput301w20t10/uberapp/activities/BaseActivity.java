package com.cmput301w20t10.uberapp.activities;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cmput301w20t10.uberapp.Application;
import com.cmput301w20t10.uberapp.LogOut;
import com.cmput301w20t10.uberapp.R;
import com.cmput301w20t10.uberapp.models.Driver;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
/**
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

    /**
     * Overrides backpressed to close menu.
     */
    @Override
    public void onBackPressed() {
        if (!isOpen) {
            super.onBackPressed();
        } else {
            closeMenu();
        }
    }

    /**
     * Finds all the buttons and calls whether or not to open the menu or close it.
     * @param layout - the layout xml of the current page
     */
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

        if (hasNetwork()) {
            //Begin onclickListeners for each fab button, each sends to new activity. This activity should extend baseactivity so it can also have Menu.
            fabProfile.setOnClickListener(v -> {
                if (Application.getInstance().getPrevActivity().equals("activities.ProfilePage")) {
                    closeMenu();
                    return;
                }
                Intent intent = new Intent(BaseActivity.this, ProfilePage.class);
                Application.getInstance().setPrevActivity(this.getLocalClassName());
                startActivity(intent);
                closeMenu();
            });

            fabSearch.setOnClickListener(v -> {
                if (Application.getInstance().getPrevActivity().equals("activities.SearchProfile")) {
                    closeMenu();
                    return;
                }
                Intent intent = new Intent(BaseActivity.this, SearchProfile.class);
                Application.getInstance().setPrevActivity(this.getLocalClassName());
                startActivity(intent);
                closeMenu();
            });

            fabHistory.setOnClickListener(v -> {
                if (Application.getInstance().getPrevActivity().equals("activities.RideHistoryActivity")) {
                    closeMenu();
                    return;
                }
                Intent intent = new Intent(BaseActivity.this, RideHistoryActivity.class);
                Application.getInstance().setPrevActivity(this.getLocalClassName());
                startActivity(intent);
                closeMenu();
            });

            fabHome.setOnClickListener(v -> {
                if (sharedPref.loadHomeActivity().equals(this.getLocalClassName())) {
                    closeMenu();
                    return;
                } else {
                    if (sharedPref.loadUserType().equals("rider")) {
                        Intent intent = new Intent(BaseActivity.this, RiderMainActivity.class);
                        Application.getInstance().setPrevActivity(this.getLocalClassName());
                        startActivity(intent);
                        closeMenu();
                    } else {
                        Driver driver = (Driver) Application.getInstance().getCurrentUser();
                        if (driver != null) {
                            if (driver.getActiveRideRequestList() != null && driver.getActiveRideRequestList().size() > 0) {
                                Intent intent = new Intent(BaseActivity.this, DriverAcceptedActivity.class);
                                String activeRideRequest = driver.getActiveRideRequestList().get(0).getPath();
                                Application.getInstance().setActiveRidePath(activeRideRequest);
                                Application.getInstance().setPrevActivity(this.getLocalClassName());
                                startActivity(intent);
                                closeMenu();
                            } else {
                                Intent intent = new Intent(BaseActivity.this, DriverMainActivity.class);
                                Application.getInstance().setPrevActivity(this.getLocalClassName());
                                startActivity(intent);
                                closeMenu();
                            }
                        }
                    }
                }
            });
        } else {
            fabProfile.setOnClickListener(v -> {
                Toast.makeText(this, "Internet required to view profile", Toast.LENGTH_SHORT).show();
            });
            fabHome.setOnClickListener(v -> {
                Toast.makeText(this, "Internet required to go to home page", Toast.LENGTH_SHORT).show();
            });
            fabHistory.setOnClickListener(v -> {
                Toast.makeText(this, "Internet required to view history", Toast.LENGTH_SHORT).show();
            });
            fabSearch.setOnClickListener(v -> {
                Toast.makeText(this, "Internet required to search for profiles", Toast.LENGTH_SHORT).show();
            });
        }
        fabDarkMode.setOnClickListener(v -> {
            if (sharedPref.loadNightModeState() == true) {
                sharedPref.setNightModeState(false);
                Toast toast = Toast.makeText(this, "DARK MODE DISABLED\nRESTART REQUIRED", Toast.LENGTH_SHORT);
                TextView textView = toast.getView().findViewById(android.R.id.message);
                textView.setGravity(Gravity.CENTER);
                toast.show();
            } else {
                sharedPref.setNightModeState(true);
                Toast toast = Toast.makeText(this, "DARK MODE ENABLED\nRESTART REQUIRED", Toast.LENGTH_SHORT);
                TextView textView = toast.getView().findViewById(android.R.id.message);
                textView.setGravity(Gravity.CENTER);
                toast.show();
            }
            closeMenu();
        });

        fabExit.setOnClickListener(v -> {
            sharedPref.eraseContents();
            Log.d("TestUsername: ", sharedPref.loadUsername());
            Log.d("TestPassword: ", sharedPref.loadPassword());
            Log.d("TestRemember: ", String.valueOf(sharedPref.loadRememberMeState()));
            Log.d("TestNight: ", String.valueOf(sharedPref.loadNightModeState()));
            Log.d("TestHome: ", sharedPref.loadHomeActivity());
            Log.d("TestUser: ", sharedPref.loadUserType());
            LogOut.clearRestart(this);
        });
    }

    public boolean hasNetwork() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }
}