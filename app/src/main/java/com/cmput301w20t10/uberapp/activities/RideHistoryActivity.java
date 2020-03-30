package com.cmput301w20t10.uberapp.activities;

import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;

import com.cmput301w20t10.uberapp.R;
import com.cmput301w20t10.uberapp.util.SearchAdapter;

public class RideHistoryActivity extends BaseActivity {
    private RecyclerView historyList;
    private SearchAdapter searchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState() == true) {
            setTheme(R.style.DarkTheme);
        } else { setTheme(R.style.AppTheme); }

        setContentView(R.layout.activity_ride_history);
    }
}
