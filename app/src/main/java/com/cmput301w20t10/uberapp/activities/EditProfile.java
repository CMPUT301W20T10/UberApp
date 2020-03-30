package com.cmput301w20t10.uberapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;


import android.os.Bundle;
import android.util.Log;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.cmput301w20t10.uberapp.Application;
import com.cmput301w20t10.uberapp.R;
import com.cmput301w20t10.uberapp.database.UserDAO;
import com.cmput301w20t10.uberapp.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*Used https://firebase.google.com/docs/firestore/manage-data/add-data official documentation for updating data.*/
public class EditProfile extends AppCompatActivity {

    SharedPref sharedPref;

    Button butCancel;
    Button butSave;
    ImageButton butPicture;
    private EditText firstNameField, lastNameField,emailField,phoneNumberField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState() == true) {
            setTheme(R.style.DarkTheme);
        } else { setTheme(R.style.AppTheme); }

        setContentView(R.layout.edit_profile);

        this.firstNameField = findViewById(R.id.editFname);
        this.lastNameField = findViewById(R.id.editLname);
        this.emailField = findViewById(R.id.editEmail);
        this.phoneNumberField = findViewById(R.id.editPhone);

        butCancel = findViewById(R.id.butCancel);
        butSave = findViewById(R.id.butSave);
        butPicture = findViewById(R.id.editPicture);
        User user = Application.getInstance().getCurrentUser();

        firstNameField.setText(user.getFirstName());
        lastNameField.setText(user.getLastName());
        emailField.setText(user.getEmail());
        phoneNumberField.setText(user.getPhoneNumber());



        butCancel.setOnClickListener(v -> {
            //Shouldn't need to do anything in this onclick, other than just return back to ProfilePage.
            finish();
        });

        butSave.setOnClickListener(v -> {
            /*
            need to save all values/changed values/appropriate values
            then need to access the USER/profile/Driver/Rider Class
            using getters/setters, compare and save the updated values from EditProfile activity
            save to firestore database?
            use finish(); to return back to previous activity - the ProfilePage
            */
            //Check that the email is valid
            String firstName = firstNameField.getText().toString();
            String lastName = lastNameField.getText().toString();
            String email = emailField.getText().toString();
            String phone = phoneNumberField.getText().toString();

            //This should set first name in user to what is inputted in the firstname field.
            user.setFirstName(firstName);
            user.setLastName(lastName);
            //Check that the email is valid
            if (!validateEmail(email)) {
                Toast.makeText(getApplicationContext(), "Email entered is not valid",
                        Toast.LENGTH_LONG).show();
            } else {
                user.setEmail(email);
            }

            //Check that the phone is valid
            if (!validatePhone(phone)) {
                Toast.makeText(getApplicationContext(), "Phone number entered is not valid",
                        Toast.LENGTH_LONG).show();
            } else {
                user.setPhoneNumber(phone);
            }


            UserDAO dao = new UserDAO();
            MutableLiveData<Boolean> result = dao.saveModel(user);
            finish();
        });

        butPicture.setOnClickListener(v -> {
            /*
            This button will be for editing/uploading a new photo.
            Will look at making this work later, but for now it will do nothing.
            No point in skeletal code as need to set it up with the database.
            wait to work on this until after the save button correctly works and saves the profile.
            LOW RISK NOT SUPER IMPORTANT...
            */
        });


    }
    /**
     * Validates that the email is valid
     *
     * @param email - The email to be validated
     *
     * @return - True if the validation succeeds, false otherwise
     */
    private boolean validateEmail(@NonNull String email) {
        String emailFormat = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w]+$";
        Pattern pattern = Pattern.compile(emailFormat);
        Matcher matcher = pattern.matcher(email);
        return matcher.find();
    }

    /**
     * Validates that the phone number is valid
     * @param phone  - the phone number to be validated
     * @return - True if the validation succeeds, false otherwise.
     */
    private boolean validatePhone(@NonNull String phone) {
        String phoneFormat = "^[0-9]{10}$";
        Pattern pattern = Pattern.compile(phoneFormat);
        Matcher matcher = pattern.matcher(phone);
        return matcher.find();
    }


}
