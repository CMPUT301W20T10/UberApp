package com.cmput301w20t10.uberapp.activities;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.SearchView;
import android.widget.Toast;

import com.cmput301w20t10.uberapp.R;
import com.cmput301w20t10.uberapp.fragments.ViewProfileFragment;
import com.cmput301w20t10.uberapp.models.User;
import com.cmput301w20t10.uberapp.util.SearchAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


/*
 * This was created based on information from user Alex Mamo : https://stackoverflow.com/users/5246885/alex-mamo
 * from the stackoverflow post : https://stackoverflow.com/a/49277842
 * Alex is a Google Developer Expert for Firebase.
 * His answer help create this activity(SearchProfile.java) by making a recyclerview list update with FirestoreRecycleAdapter in order to create a live list connected to the firestore.
 */


// TODO: 2020-03-13 Need to add functionality: Search specific names, click on searched user profile?  to contact that user? 
public class SearchProfile extends BaseActivity {

    private SearchView SearchField;
    private RecyclerView SearchList;
    private SearchAdapter recyclerAdapter;
    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    private Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_profile);
        SearchField = (SearchView) findViewById(R.id.searchProfile);
        setUpSearchList();

        SearchField.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return true;
            }
        });



    }


    private void searchList(String searchText) {
        onStop();
        query = rootRef.collection("users").orderBy("username").startAt(searchText).endAt(searchText + "\uf8ff");
        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();
        recyclerAdapter = new SearchAdapter(options);
        SearchList = (RecyclerView) findViewById(R.id.profileList);
        SearchList.setLayoutManager(new LinearLayoutManager(this));
        SearchList.setAdapter(recyclerAdapter);
        onStart();
    }
    private void setUpSearchList() {
        query = rootRef.collection("users").orderBy("username");
        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();
        recyclerAdapter = new SearchAdapter(options);

        SearchList = (RecyclerView) findViewById(R.id.profileList);
        SearchList.setLayoutManager(new LinearLayoutManager(this));
        SearchList.setAdapter(recyclerAdapter);
        recyclerAdapter.setOnItemClickListener(new SearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                ViewProfileFragment.newInstance(documentSnapshot.getId()).show(getSupportFragmentManager(),"User");
            }
        });
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


