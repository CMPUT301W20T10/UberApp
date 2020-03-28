package com.cmput301w20t10.uberapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;

import com.cmput301w20t10.uberapp.R;
import com.cmput301w20t10.uberapp.models.User;
import com.cmput301w20t10.uberapp.util.SearchAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;





// TODO: 2020-03-13 Need to add functionality: Search specific names, click on searched user profile?  to contact that user? 
public class SearchProfile extends BaseActivity {

    private SearchView SearchField;
    private RecyclerView SearchList;
    private SearchAdapter recyclerAdapter;
    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_profile);
        SearchField = (SearchView) findViewById(R.id.searchProfile);

        setUpSearchList();

    }


    private void setUpSearchList() {
        Query query = rootRef.collection("users");
        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();
        recyclerAdapter = new SearchAdapter(options);

        SearchList = (RecyclerView) findViewById(R.id.profileList);
        SearchList.setLayoutManager(new LinearLayoutManager(this));
        SearchList.setAdapter(recyclerAdapter);
    }

    /**
     * Calls on activity start, tells list to startListening - update continuously.
     */
    @Override
    protected void onStart() {
        super.onStart();
        recyclerAdapter.startListening();
    }

    /**
     * When activity stops, list does not need to listen anymore. no more updates.
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (recyclerAdapter != null) {
            recyclerAdapter.stopListening();
        }
    }

}


