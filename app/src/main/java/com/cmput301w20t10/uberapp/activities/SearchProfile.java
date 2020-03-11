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

public class SearchProfile extends AppCompatActivity {

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


        SearchField = (SearchView) findViewById(R.id.searchProfile);
        SearchList = (RecyclerView) findViewById(R.id.profileList);

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


