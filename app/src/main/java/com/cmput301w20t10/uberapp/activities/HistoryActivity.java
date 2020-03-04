package com.cmput301w20t10.uberapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.cmput301w20t10.uberapp.R;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {
    ListView history;
    ArrayList<History> CityDataList;
    ArrayAdapter<History> HistoryAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        history = findViewById(R.id.history_list);

        CityDataList.add((new History("2020-03-04", "$250", "john")));

        HistoryAdapter = new CustomList(this, CityDataList);

        history.setAdapter(HistoryAdapter);
    }
}

