package com.cmput301w20t10.uberapp.activities;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.cmput301w20t10.uberapp.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class CustomList extends ArrayAdapter<History> {

    private ArrayList<History> histories;
    private Context context;

    public CustomList(Context context, ArrayList<History> cities){
        super(context,0,cities);
        this.histories = cities;
        this.context = context;
    }



    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //return super.getView(position, convertView, parent);
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.content, parent, false);

        }

        History history = histories.get(position);

        TextView date = view.findViewById(R.id.date);
        TextView payment = view.findViewById(R.id.payment);
        TextView driver = view.findViewById(R.id.driver);

        date.setText(history.getdate());
        payment.setText(history.getPrice());
        driver.setText(history.getDriver());

        return view;
    }
}
