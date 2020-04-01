package com.cmput301w20t10.uberapp.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.MutableLiveData;

import com.cmput301w20t10.uberapp.R;
import com.cmput301w20t10.uberapp.activities.SharedPref;
import com.cmput301w20t10.uberapp.database.dao.UserDAO;
import com.cmput301w20t10.uberapp.models.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ViewProfileFragment extends DialogFragment {
    private TextView firstName;
    private TextView lastName;
    private TextView eMail;
    private TextView phoneNumber;
    private Query query;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private User user;
    private Dialog dialog;

    SharedPref sharedPref;

    /**
     * Empty constructor needed for fragments.
     */
    public ViewProfileFragment() {
    }

    /**
     * @param userID  - users unique Document ID
     * @param username - Users unique username.
     * @return The fragment to be used/created.
     */
    public static ViewProfileFragment newInstance(String userID, String username) {
        ViewProfileFragment frag = new ViewProfileFragment();
        Bundle args = new Bundle();
        args.putString("userID",userID);
        args.putString("username",username);
        frag.setArguments(args);
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog (@Nullable Bundle savedInstanceState) {
        //inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_view_profile,null);

        firstName = view.findViewById(R.id.view_first_name);
        lastName = view.findViewById(R.id.view_last_name);
        eMail = view.findViewById(R.id.view_email);
        phoneNumber = view.findViewById(R.id.view_phonenumber);

        sharedPref = new SharedPref(getContext());

        String username = getArguments().getString("username");

        AlertDialog.Builder builder;
        if (sharedPref.loadNightModeState()) {
            builder = new AlertDialog.Builder(getContext(), R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Background); //copied format from previous code written in lab.
        } else {
            builder = new AlertDialog.Builder(getContext()); //copied format from previous code written in lab.
        }
        this.dialog = builder
                .setView(view)
                .setTitle(Html.fromHtml("<b>"+username+"</b>"))
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Phone The User", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse("tel:"+phoneNumber.getText().toString()));
                        startActivity(callIntent);
                    }
                })
                .setNegativeButton("Email The User", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent emailIntent = new Intent(Intent.ACTION_VIEW);
                        emailIntent.setData( Uri.parse("mailto:"+eMail.getText()));
                        startActivity(emailIntent);
                    }
                }).create();
        return this.dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        String userID = getArguments().getString("userID");

        UserDAO dao = new UserDAO();
        MutableLiveData<User> liveData = dao.getUserByUserID(userID);
        liveData.observe(this, user -> {
            if (user != null) {
                firstName.setText(user.getFirstName());
                lastName.setText(user.getLastName());
                eMail.setText(user.getEmail());
                phoneNumber.setText(user.getPhoneNumber());
            } else {
                // no internet connection
            }
        });
    }

    /**
     * When it opens, begin to gather the information through livedata to populate a User.
     */
    @Override
    public void onResume() {
        super.onResume();
        String userID = getArguments().getString("userID");

        UserDAO dao = new UserDAO();
        MutableLiveData<User> liveData = dao.getUserByUserID(userID);
        liveData.observe(this, user -> {
            if (user != null) {
                firstName.setText(user.getFirstName());
                lastName.setText(user.getLastName());
                eMail.setText(user.getEmail());
                phoneNumber.setText(user.getPhoneNumber());
            } else {
                // no internet connection
            }
        });

    }
}


