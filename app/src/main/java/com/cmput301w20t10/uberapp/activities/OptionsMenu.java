package com.cmput301w20t10.uberapp.activities;

import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cmput301w20t10.uberapp.R;

public class OptionsMenu extends AppCompatActivity {

    private ImageButton themeButton;
    SharedPref sharedPref;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.dark_mode_switch);
        item.setActionView(R.layout.switch_item);
        themeButton = item.getActionView().findViewById(R.id.switch_id);
        themeButton.setOnClickListener(onSwitchClicked);

        sharedPref = new SharedPref(this);
        return true;
    }

    View.OnClickListener onSwitchClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            sharedPref = new SharedPref(getBaseContext());
            if (sharedPref.loadNightModeState() == true) {
                themeButton.setBackgroundResource(R.mipmap.darkmode);
                sharedPref.setNightModeState(false);
            } else {
                themeButton.setBackgroundResource(R.mipmap.lightmode);
                sharedPref.setNightModeState(true);
            }
            notifyRestart();
        }
    };

    public void notifyRestart() {
        Toast toast = Toast.makeText(this, "DARK MODE TOGGLED\nRESTART REQUIRED", Toast.LENGTH_SHORT);
        TextView textView = toast.getView().findViewById(android.R.id.message);
        textView.setGravity(Gravity.CENTER);
        toast.show();
    }
}
