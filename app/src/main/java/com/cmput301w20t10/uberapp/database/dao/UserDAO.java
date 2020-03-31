package com.cmput301w20t10.uberapp.database.dao;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.cmput301w20t10.uberapp.database.base.DAOBase;
import com.cmput301w20t10.uberapp.database.base.EntityBase;
import com.cmput301w20t10.uberapp.database.base.ModelBase;
import com.cmput301w20t10.uberapp.database.entity.UserEntity;
import com.cmput301w20t10.uberapp.models.User;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static android.content.ContentValues.TAG;

/**
 * Data Access Object (DAO) for User model.
 * DAO contains specific operations that are concerned with the model they are associated with.
 *
 * @author Allan Manuba
 * @version 1.1.1
 */
public class UserDAO extends DAOBase<UserEntity, User> {
    static final String COLLECTION = "users";
    final static String LOC = "Tomate: UserDAO: ";

    public UserDAO() {}

    /**
     * Log the user in
     *
     * @param username
     * @param password
     * @return
     * Returns a MutableLiveData object. To observe a MutableLiveData object:
     *
     * <pre>
     *      DatabaseManager db = DatabaseManager.getInstance();
     *      DAO dao = db.getDAO();
     *      MutableLiveData<Model> liveData = dao.getModel(...);
     *      liveData.observe(this, model -> {
     *          // receive model inside here
     *      });
     * </pre>
     *
     * When observed, the object may receive model as the following:
     * <li>
     *     <ul><b>Non-null UserEntity object:</b> Log in was successful.</ul>
     *     <ul><b>Null:</b> Log in failed.</ul>
     * </li>
     */
    // todo: improve
    LiveData<UserEntity> logIn(String username, String password) {
        MutableLiveData<UserEntity> userLiveData = new MutableLiveData<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        System.out.println("Username: " + username);
        db.collection(COLLECTION)
                .whereEqualTo(UserEntity.Field.USERNAME.toString(), username)
                .whereEqualTo(UserEntity.Field.PASSWORD.toString(), password)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            Log.d(TAG, LOC + "logIn: No matching data");
                            userLiveData.setValue(null);
                        } else {
                            if (task.getResult().size() > 1) {
                                Log.w(TAG, LOC + "onComplete: This should not happen\nMore than one account found");
                            }

                            DocumentSnapshot snapshot = task.getResult().getDocuments().get(0);
                            UserEntity userEntity = snapshot.toObject(UserEntity.class);
                            userLiveData.setValue(userEntity);
                        }
                    } else {
                        Log.d(TAG, LOC + "onComplete: ", task.getException());
                        userLiveData.setValue(null);
                    }
                });

        return userLiveData;
    }

    /**
     * Checks for how many users have the same given username. Usage:
     * <pre>
     UserDAO dao = new UserDAO();
     MutableLiveData<Integer> liveData = dao.checkForUserCount("usernameHere");
     liveData.observe(this, count -> {
         if (count == null) {
            // no internet connection
         } else if (count == 0) {
            // not yet made
         } else if (count == 1) {
            // one account made with username
         } else { // count > 1
            // two or more accounts with the same username
         }
     });
     * </pre>
     *
     * @param username
     * @return MutableLiveData which returns an Integer indicating the count of users with the
     * same username or null indicating an error has occurred somewhere, likely connection loss
     */
    public MutableLiveData<Integer> checkForUserCount(String username) {
        MutableLiveData<Integer> liveData = new MutableLiveData<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(COLLECTION)
                .whereEqualTo(UserEntity.Field.USERNAME.toString(), username)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        liveData.setValue(task.getResult().size());
                    } else {
                        liveData.setValue(null);
                    }
                });

        return liveData;
    }

    /**
     * Registers a user
     *
     * @param username
     * @param password
     * @param email
     * @param firstName
     * @param lastName
     * @param phoneNumber
     * @return  null if user already registered
     */
    // todo: improve
    public LiveData<UserEntity> registerUser(String username,
                                                    String password,
                                                    String email,
                                                    String firstName,
                                                    String lastName,
                                                    String phoneNumber,
                                                    String image) {
        MutableLiveData<UserEntity> userLiveData = new MutableLiveData<>();

        // todo: validate if user was already registered

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        UserEntity userEntity = new UserEntity(username,
                password,
                email,
                firstName,
                lastName,
                phoneNumber,
                image);
        db.collection(COLLECTION)
                .add(userEntity)
                .addOnSuccessListener(
                        userReference -> {
                            if (userReference != null) {
                                userEntity.setUserReference(userReference);
                                UserDAO.this.saveEntity(userEntity);
                                userLiveData.setValue(userEntity);
                            } else {
                                Log.e(TAG, LOC + "registerUser: Failed to add user");
                                userLiveData.setValue(null);
                            }
                        }
                )
                .addOnFailureListener(e -> {
                    Log.w(TAG, "onFailure: ", e);
                    userLiveData.setValue(null);
                });

        return userLiveData;
    }

    /**
     * @see DAOBase#saveModel(ModelBase)
     * @param   model   Model to update
     * @return
     */
    @Override
    public MutableLiveData<Boolean> saveModel(User model) {
        final DocumentReference reference = model.getUserReference();

        if (reference != null) {
            UserEntity userEntity = new UserEntity();
            model.transferChanges(userEntity);
            return saveEntity(userEntity);
        } else {
            Log.e(TAG, LOC + "saveModel: Reference is null");
            MutableLiveData<Boolean> liveData = new MutableLiveData<>();
            liveData.setValue(false);
            return liveData;
        }
    }

    @Override
    protected String getCollectionName() {
        return COLLECTION;
    }

    @Override
    protected DAOBase<UserEntity, User> create() {
        return new UserDAO();
    }

    /**
     * @see DAOBase#createModelFromEntity(EntityBase)
     * @param userEntity
     * @return
     */
    @Override
    protected MutableLiveData<User> createModelFromEntity(UserEntity userEntity) {
        MutableLiveData<User> liveData = new MutableLiveData<>();
        liveData.setValue(new User(userEntity));
        return liveData;
    }

    @Override
    protected UserEntity createObjectFromSnapshot(DocumentSnapshot snapshot) {
        return snapshot.toObject(UserEntity.class);
    }

    /**
     * Gets a User object using their DocumentID.
     *
     * <pre>
     *     UserDAO dao = new UserDAO();
     *     MutableLiveData<User> liveData = dao.getUserByUserID(userId);
     *     liveData.observe(this, user -> {
     *         if (user != null) {
     *             // user found
     *         } else {
     *             // no internet connection
     *         }
     *     });
     * </pre>
     *
     * @param userId    Document ID for the User
     * @return  User    returns a User object if User was successfully found
     *          null    returns a null object if User was not found or an error has occurred
     */
    public MutableLiveData<User> getUserByUserID(String userId) {
        return getModelByID(userId);
    }
}