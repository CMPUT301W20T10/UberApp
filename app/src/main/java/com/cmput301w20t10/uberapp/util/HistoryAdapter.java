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

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.cmput301w20t10.uberapp.R;
import com.cmput301w20t10.uberapp.database.DatabaseManager;
import com.cmput301w20t10.uberapp.database.dao.DriverDAO;
import com.cmput301w20t10.uberapp.database.dao.RiderDAO;
import com.cmput301w20t10.uberapp.models.Driver;
import com.cmput301w20t10.uberapp.models.RideRequest;
import com.cmput301w20t10.uberapp.models.Rider;
import com.cmput301w20t10.uberapp.models.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author Alexander Laevens
 */
public class HistoryAdapter extends BaseAdapter {
    Context context;
    private List<RideRequest> rideHistory;
    private final RequestManager glide;
    private User user;

    public HistoryAdapter(Context context, RequestManager glide, List<RideRequest> rideHistory, User user) {
        this.context = context;
        this.glide = glide;
        this.rideHistory = rideHistory;
        this.user = user;
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

        // get the easily accessable ride request information
        RideRequest request = rideHistory.get(position);
        Date time = request.getTimestamp();
        float offer = request.getFareOffer();

        // get reference to the UI elements
        TextView fareView = view.findViewById(R.id.rideFare);
        TextView uNameView = view.findViewById(R.id.driverUsername);
        TextView dateView = view.findViewById(R.id.rideDate);
        TextView statusText = view.findViewById(R.id.statusText);
        CircleImageView profilePicture = view.findViewById(R.id.driver_profile_picture);

        // write the easily accessible information
        DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd hh:mm a");
        dateView.setText(dateFormat.format(time));
        fareView.setText("$"+String.format("%.2f", offer));
        statusText.setText(String.valueOf(request.getState()));

        if (user instanceof Rider) {
            // Get the driver's information
            DriverDAO driverDAO = DatabaseManager.getInstance().getDriverDAO();
            MutableLiveData<Driver> liveDriver = driverDAO.getDriverFromDriverReference(request.getDriverReference());
            liveDriver.observe((AppCompatActivity) context, driver -> {
                if (driver != null) {
                    // retrieve and set the username display
                    uNameView.setText(driver.getUsername());

                    // draw the driver's profile picture
                    if (driver.getImage() != "") {
                        glide.load(driver.getImage())
                                .apply(RequestOptions.circleCropTransform())
                                .into(profilePicture);
                    }
                } else {
                    // in the event a driver hasn't been assigned yet
                    uNameView.setText("No Driver Assigned");
                }
            });
        } else {
            // Get the driver's information
            RiderDAO riderDAO = DatabaseManager.getInstance().getRiderDAO();
            MutableLiveData<Rider> liveRider = riderDAO.getModelByReference(request.getRiderReference());
            liveRider.observe((AppCompatActivity) context, rider -> {
                if (rider != null) {
                    // retrieve and set the username display
                    uNameView.setText(rider.getUsername());

                    // draw the driver's profile picture
                    if (rider.getImage() != "") {
                        glide.load(rider.getImage())
                                .apply(RequestOptions.circleCropTransform())
                                .into(profilePicture);
                    }
                } else {
                    // in the event a driver hasn't been assigned yet
                    uNameView.setText("No rider assigned? This shouldnt be possible");
                }
            });
        }


        return view;
    }
}
