package com.cmput301w20t10.uberapp.util;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cmput301w20t10.uberapp.R;
import com.cmput301w20t10.uberapp.database.dao.LoginRegisterDAO;
import com.cmput301w20t10.uberapp.models.Driver;
import com.cmput301w20t10.uberapp.models.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import de.hdodenhof.circleimageview.CircleImageView;

/*
 * This was created based on information from user Alex Mamo : https://stackoverflow.com/users/5246885/alex-mamo
 * from the stackoverflow post : https://stackoverflow.com/a/49277842
 * Alex is a Google Developer Expert for Firebase.
 * His answer help create this activity(SearchAdapter.java) and (SearchProfile.java) by making a recyclerview list adapter  with FirestoreRecycleAdapter in order to create a live list connected to the firestore.
 */
public class SearchAdapter extends FirestoreRecyclerAdapter<User, SearchAdapter.UserViewHolder> {
    private OnItemClickListener listener;
    private AppCompatActivity owner;

    /**
     * Supers the options - updates the searchAdapter with new options.
     * @param options -query options.
     */
    public SearchAdapter(@NonNull FirestoreRecyclerOptions<User> options, AppCompatActivity owner) {
        super(options);
        this.owner = owner;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull User user) {
        holder.setUsername(user.getUsername());
        holder.setImage(user.getImage());

        Log.d("Profile Testing", String.valueOf(user.getClass()));

        LoginRegisterDAO dao = new LoginRegisterDAO();
        MutableLiveData<Driver> liveData = dao.logInAsDriver(user.getUsername(), user.getPassword(), owner);
        liveData.observe(owner, driver -> {
            if (driver != null) {
                holder.setRating(driver.getRating(), true);
            } else {
                holder.setRating(0, false);
            }
        });
    }


    /**
     * @param parent -The current layout you're on
     * @param viewType -
     * @return - view holder (view on how the data should be displayed)
     */
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
        private String username;
        private String image;
        private int rating;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position, username);
                    }
                }
            });
        }

        void setUsername(String username) {
            this.username = username;
            TextView userText = view.findViewById(R.id.uName);
            userText.setText(username);
        }

        void setRating(int rating, boolean isDriver) {
            this.rating = rating;
            TextView userRating = view.findViewById(R.id.sPosRate);
            ImageView thmbUpButton = view.findViewById(R.id.sThmbUp);

            if (isDriver) {
                thmbUpButton.setImageResource(R.mipmap.thmbup);
                userRating.setText(String.valueOf(rating));
            } else {
                // https://stackoverflow.com/questions/2859212/how-to-clear-an-imageview-in-android
                thmbUpButton.setImageResource(android.R.color.transparent);
                userRating.setText("");
            }

        }

        void setImage(String image){
            this.image = image;
            CircleImageView profilePicture = view.findViewById(R.id.profile_image);
            if (image.length() > 5) {
                Glide.with(view)
                        .load(image)
                        .apply(RequestOptions.circleCropTransform())
                        .into(profilePicture);
            }

        }

    }

    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position, String username);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}
