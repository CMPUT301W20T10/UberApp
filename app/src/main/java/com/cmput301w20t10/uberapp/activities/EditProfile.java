package com.cmput301w20t10.uberapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.MutableLiveData;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cmput301w20t10.uberapp.Application;
import com.cmput301w20t10.uberapp.R;
import com.cmput301w20t10.uberapp.database.UserDAO;
import com.cmput301w20t10.uberapp.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * For info on how to take pictures, upload and set:
 * https://www.youtube.com/watch?v=CDv05EP45JQ
 * by yoursTRULY
 * https://www.youtube.com/channel/UCr0y1P0-zH2o3cFJyBSfAKg
 */
public class EditProfile extends AppCompatActivity {

    SharedPref sharedPref;

    Button butCancel;
    Button butSave;
    ImageButton butPicture;
    private EditText firstNameField, lastNameField,emailField,phoneNumberField;
    int TAKE_IMAGE_CODE = 10001;
    private static final int REQUEST_CODE = 101;
    CircleImageView profilePicture;

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
        this.profilePicture = findViewById(R.id.profile_image);

        butCancel = findViewById(R.id.butCancel);
        butSave = findViewById(R.id.butSave);
        butPicture = findViewById(R.id.editPicture);
        User user = Application.getInstance().getCurrentUser();

        firstNameField.setText(user.getFirstName());
        lastNameField.setText(user.getLastName());
        emailField.setText(user.getEmail());
        phoneNumberField.setText(user.getPhoneNumber());
        if (user.getImage() != "") {
            Glide.with(this)
                    .load(user.getImage())
                    .apply(RequestOptions.circleCropTransform())
                    .into(profilePicture);


        }



        butCancel.setOnClickListener(v -> {
            //Shouldn't need to do anything in this onclick, other than just return back to ProfilePage.
            finish();
        });

        butSave.setOnClickListener(v -> {
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
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, REQUEST_CODE);}
            else{
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) !=null) {
                    startActivityForResult(intent, TAKE_IMAGE_CODE);
                }
            }

        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) !=null) {
                    startActivityForResult(intent, TAKE_IMAGE_CODE);
                }
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_IMAGE_CODE) {
            switch (resultCode) {
                case RESULT_OK:
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    profilePicture.setImageBitmap(bitmap);
                    handleUpload(bitmap);

            }
        }
    }

    private void handleUpload(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);

        String uid = Application.getInstance().getCurrentUser().getUserReference().getId();
        StorageReference ref = FirebaseStorage.getInstance().getReference()
                .child("profileImages")
                .child(uid+".jpeg");

        ref.putBytes(baos.toByteArray())
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        getDownloadUrl(ref);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("upload","onFailure: ",e.getCause());
                    }
                });
    }

    private void getDownloadUrl(StorageReference ref) {
        ref.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("Upload","onSuccess: " + uri);
                        setUserProfileUrl(uri);
                    }
                });
    }

    private void setUserProfileUrl(Uri uri) {
        User user = Application.getInstance().getCurrentUser();
        user.setImage(uri.toString());


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
