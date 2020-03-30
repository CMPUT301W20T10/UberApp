package com.cmput301w20t10.uberapp.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import com.cmput301w20t10.uberapp.R;
import com.cmput301w20t10.uberapp.activities.RideHistoryActivity;
import com.cmput301w20t10.uberapp.database.DatabaseManager;
import com.cmput301w20t10.uberapp.database.dao.DriverDAO;
import com.cmput301w20t10.uberapp.models.Driver;
import com.cmput301w20t10.uberapp.models.RideRequest;
import com.google.firebase.firestore.DocumentReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class HistoryAdapter extends BaseAdapter {
    Context context;
    private List<RideRequest> rideHistory;

    public HistoryAdapter(Context context, List<RideRequest> rideHistory) {
        this.context = context;
        this.rideHistory = rideHistory;
    }

    @Override
    public int getCount() {
        return rideHistory.size();
    }

    @Override
    public Object getItem(int position) {
        return rideHistory.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.ride_history_content, parent, false);
        }

        RideRequest request = rideHistory.get(position);
        Date time = request.getTimestamp();
        float offer = request.getFareOffer();

        TextView fareView = view.findViewById(R.id.rideFare);
        TextView uNameView = view.findViewById(R.id.driverUsername);
        TextView dateView = view.findViewById(R.id.rideDate);

        DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd hh:mm a");
        dateView.setText(dateFormat.format(time));
        fareView.setText("$"+String.format("%.2f", offer));

        DriverDAO driverDAO = DatabaseManager.getInstance().getDriverDAO();
        MutableLiveData<Driver> liveDriver = driverDAO.getDriverFromDriverReference(request.getDriverReference());

        liveDriver.observe((AppCompatActivity) context, driver -> {
            if (driver != null) {
                uNameView.setText(driver.getUsername());
            } else {
                uNameView.setText("No Driver Assigned");
            }
        });

        return view;
    }
}
