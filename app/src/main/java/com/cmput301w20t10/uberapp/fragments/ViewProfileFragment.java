package com.cmput301w20t10.uberapp.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.MutableLiveData;

import com.cmput301w20t10.uberapp.R;
import com.cmput301w20t10.uberapp.database.UserDAO;
import com.cmput301w20t10.uberapp.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ViewProfileFragment extends DialogFragment {
    private TextView firstName;
    private TextView lastName;
    private TextView eMail;
    private TextView phoneNumber;
    private Query query;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private User user;

    public ViewProfileFragment() {
    }

    public static ViewProfileFragment newInstance(String userID) {
        ViewProfileFragment frag = new ViewProfileFragment();
        Bundle args = new Bundle();
        args.putString("userID",userID);
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



        String userID = getArguments().getString("userID");


/*        UserDAO dao = new UserDAO();
        MutableLiveData<User> liveData = dao.getUserByUserID(userId);
        liveData.observe(this, user -> {
            if (user != null) {
                // user found
            } else {
                // no internet connection
            }
        });*/



        firstName.setText(user.getFirstName());
        lastName.setText(user.getLastName());
        eMail.setText(user.getEmail());
        phoneNumber.setText(user.getPhoneNumber());



        AlertDialog.Builder builder = new AlertDialog.Builder(getContext()); //copied format from previous code written in lab.
            return builder
                    .setView(view)
                    .setTitle(user.getUsername())
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.dismiss();
                        }
                    }).create();
        }

}


