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
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
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
    private FirestoreRecyclerAdapter<User,UserViewHolder> recyclerAdapter;


    @Override
    protected void onStart() {
        super.onStart();
        recyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (recyclerAdapter != null) {
            recyclerAdapter.stopListening();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_profile);


        SearchField = findViewById(R.id.searchProfile);
        SearchList = findViewById(R.id.profileList);

        SearchList.setLayoutManager(new LinearLayoutManager(this));

        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        Query query = rootRef.collection("users");


        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query,User.class)
                .build();

        recyclerAdapter = new FirestoreRecyclerAdapter<User, UserViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull User model) {
                holder.setUserName(model.getUsername());
            }

            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_profile_content, parent, false);
                return new UserViewHolder(view);
            }
        };
        SearchList.setAdapter(recyclerAdapter);


    }


    /**
     * This is a view holder for the incoming items (USERS), it is passed to the adapter to get inserted into the list.
     * Handles setting the username,image and etc.
     */
    private class UserViewHolder extends RecyclerView.ViewHolder {
        private View view;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }

        void setUserName(String username) {
            TextView userText = view.findViewById(R.id.uName);
            userText.setText(username);
        }

    }

}


