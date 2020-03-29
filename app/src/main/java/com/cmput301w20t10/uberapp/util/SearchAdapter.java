package com.cmput301w20t10.uberapp.util;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cmput301w20t10.uberapp.R;
import com.cmput301w20t10.uberapp.activities.SearchProfile;
import com.cmput301w20t10.uberapp.models.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

/*
 * This was created based on information from user Alex Mamo : https://stackoverflow.com/users/5246885/alex-mamo
 * from the stackoverflow post : https://stackoverflow.com/a/49277842
 * Alex is a Google Developer Expert for Firebase.
 * His answer help create this activity(SearchAdapter.java) and (SearchProfile.java) by making a recyclerview list adapter  with FirestoreRecycleAdapter in order to create a live list connected to the firestore.
 */
public class SearchAdapter extends FirestoreRecyclerAdapter<User, SearchAdapter.UserViewHolder> {

    public SearchAdapter(@NonNull FirestoreRecyclerOptions<User> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull User user) {
        holder.setUserName(user.getUsername());
    }


    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_profile_content, parent, false);
        return new UserViewHolder(view);
    }


    /**
     * This is a view holder for the incoming items (USERS), it is passed to the adapter to get inserted into the list.
     * Handles setting the username,image and etc.
     */
    class UserViewHolder extends RecyclerView.ViewHolder {
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
