package com.cmput301w20t10.uberapp.database.dao;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import com.cmput301w20t10.uberapp.database.base.DAOBase;
import com.cmput301w20t10.uberapp.database.base.EntityBase;
import com.cmput301w20t10.uberapp.database.base.ModelBase;
import com.cmput301w20t10.uberapp.database.entity.RiderEntity;
import com.cmput301w20t10.uberapp.database.entity.UserEntity;
import com.cmput301w20t10.uberapp.database.util.GetTaskSequencer;
import com.cmput301w20t10.uberapp.models.Rider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Data Access Object (DAO) for Rider model.
 * DAO contains specific operations that are concerned with the model they are associated with.
 *
 * @author Allan Manuba
 * @version 1.4.2
 * Add dependency injection
 *
 * @version 1.1.1
 */
public class RiderDAO extends DAOBase<RiderEntity, Rider> {
    static final String LOC = "Tomate: RiderDAO: ";
    static final String COLLECTION = "riders";

    public RiderDAO() {
        super();
    }

    public RiderDAO(FirebaseFirestore db) {
        super(db);
    }

    /**
     * Registers a rider
     *
     * @param username
     * @param password
     * @param email
     * @param firstName
     * @param lastName
     * @param phoneNumber
     * @param image
     * @param owner
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
     *     <ul><b>Non-null Rider object:</b> the Rider object's fields were successfully added to the database.</ul>
     *     <ul><b>Null:</b> Registration failed.</ul>
     * </li>
     */
    @Nullable
    public MutableLiveData<Rider> registerRider(String username,
                                                String password,
                                                String email,
                                                String firstName,
                                                String lastName,
                                                String phoneNumber,
                                                String image,
                                                @NonNull LifecycleOwner owner) {
        RegisterRiderTask task = new RegisterRiderTask(username,
                password,
                email,
                firstName,
                lastName,
                phoneNumber,
                image,
                owner,
                db);
        return task.run();
    }

    /**
     * Attempts to log the user in as a rider.
     *
     * @param username
     * @param password
     * @param owner
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
     *     <ul><b>Non-null Rider object:</b> Log in was successful.</ul>
     *     <ul><b>Null:</b> Log in was unsuccessful.</ul>
     * </li>
     */
    public MutableLiveData<Rider> logInAsRider(String username, String password, LifecycleOwner owner) {
        LogInAsRiderTask task = new LogInAsRiderTask(username, password, owner, db);
        return task.run();
    }

    /**
     * @see DAOBase#saveModel(ModelBase)
     *
     * @param rider
     * @return
     */
    @Override
    public MutableLiveData<Boolean> saveModel(Rider rider) {
        RiderEntity riderEntity = new RiderEntity();
        rider.transferChanges(riderEntity);
        return saveEntity(riderEntity);
    }

    @Override
    protected String getCollectionName() {
        return COLLECTION;
    }

    @Override
    protected DAOBase<RiderEntity, Rider> create() {
        return new RiderDAO();
    }

    /**
     * @see DAOBase#createModelFromEntity(EntityBase)
     *
     * @param riderEntity
     * @return
     */
    // todo: deprecate class or make task shorter
    @Override
    protected MutableLiveData<Rider> createModelFromEntity(RiderEntity riderEntity) {
        GetRiderFromReferenceTask task = new GetRiderFromReferenceTask(riderEntity.getRiderReference(), db);
        return task.run();
    }

    @Override
    protected RiderEntity createObjectFromSnapshot(DocumentSnapshot snapshot) {
        return snapshot.toObject(RiderEntity.class);
    }

    /**
     * Rider requires RiderEntity and UserEntity so another sequence of functions is required
     *
     * @see DAOBase#getOtherEntities(MutableLiveData, EntityBase)
     *
     * @param liveData
     * @param mainEntity
     */
    @Override
    protected void getOtherEntities(MutableLiveData<List<EntityBase>> liveData,
                                    RiderEntity mainEntity) {
        GetUserEntity task = new GetUserEntity(liveData, mainEntity, db);
        task.run();
    }

    /**
     * Using this for it's lifecycle owner so that I won't have to make my own above
     *
     * @version 1.4.2.2
     * Add dependency injection
     *
     * @version 1.1.1.1
     */
    private class GetUserEntity extends GetTaskSequencer<UserEntity> {
        private final DocumentReference reference;
        private final MutableLiveData<List<EntityBase>> liveData;

        public GetUserEntity(MutableLiveData<List<EntityBase>> liveData, RiderEntity riderEntity, FirebaseFirestore db) {
            super(db);
            this.reference = riderEntity.getUserReference();
            this.liveData = liveData;
        }

        @Override
        public void doFirstTask() {
            UserDAO userDAO = new UserDAO();
            userDAO.getEntityByReference(reference)
                    .observe(lifecycleOwner, userEntity -> {
                        List<EntityBase> entityBaseList = new ArrayList<>();

                        if (userEntity != null) {
                            entityBaseList.add(userEntity);
                        }

                        liveData.setValue(entityBaseList);
                    });
        }
    }

    /**
     * Gets a rider object given a DocumentReference pointing to a document under the collection
     * riders in Firestore
     *
     * @param riderReference
     * @return
     */
    MutableLiveData<Rider> getRiderFromRiderReference(DocumentReference riderReference) {
        GetRiderFromReferenceTask task = new GetRiderFromReferenceTask(riderReference, db);
        return task.run();
    }
}

/**
 * Sequence of functions to register rider
 * @see GetTaskSequencer
 *
 * @author Allan Manuba
 * @version 1.4.2
 * Add dependency injection
 *
 * @vesrion 1.1.1
 */
class RegisterRiderTask extends GetTaskSequencer<Rider> {
    private static final String LOC = RiderDAO.LOC + "RegisterRiderTask: ";
    private String username;
    private String password;
    private String email;
    String firstName;
    String lastName;
    String phoneNumber;
    String image;
    @NonNull LifecycleOwner owner;

    UserEntity userEntity;
    RiderEntity riderEntity;
    RiderDAO riderDAO;

    RegisterRiderTask(String username,
                      String password,
                      String email,
                      String firstName,
                      String lastName,
                      String phoneNumber,
                      String image,
                      @NonNull LifecycleOwner owner,
                      FirebaseFirestore db) {
        super(db);
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.image = image;
        this.owner = owner;
    }

    @Override
    public void doFirstTask() {
        addRiderEntity();
    }

    // task 1
    private void addRiderEntity() {
        this.riderEntity = new RiderEntity();
        db.collection(RiderDAO.COLLECTION)
            .add(riderEntity)
            .addOnSuccessListener(this::updateRiderEntity)
        .addOnFailureListener(e -> {
            Log.e(TAG, "addRiderEntity: onFailure: ", e);
            postResult(null);
        });
    }

    // task 2
    private void updateRiderEntity(DocumentReference riderReference) {
        this.riderEntity.setRiderReference(riderReference);
        this.riderDAO = new RiderDAO();
        this.riderDAO.saveEntity(this.riderEntity)
                .observe(owner, aBoolean -> {
                    if (aBoolean) {
                        registerRiderAsUser();
                    } else {
                        Log.e(TAG, "updateRiderEntity: ");
                        postResult(null);
                    }
                });
    }

    // task 3
    private void registerRiderAsUser() {
        UserDAO userDAO = new UserDAO();
        userDAO.registerUser(username,
                password,
                email,
                firstName,
                lastName,
                phoneNumber,
                image)
                .observe(owner, userEntity -> {
                    if (userEntity != null) {
                        this.userEntity = userEntity;
                        receiveUserEntity();
                    } else {
                        Log.e(TAG, "registerRiderAsUser: ");
                        postResult(null);
                    }
                });
    }

    // task 4
    private void receiveUserEntity() {
        if (userEntity != null && userEntity.getUserReference() != null) {
            UserDAO userDAO = new UserDAO();
            this.userEntity.setRiderReference(riderEntity.getRiderReference());
            userDAO.saveEntity(userEntity)
                    .observe(owner, aBoolean -> {
                        if (aBoolean) {
                            updateUserEntity();
                        } else {
                            Log.e(TAG, "receiveUserEntity: ");
                            postResult(null);
                        }
                    });
        }
    }

    // task 5
    private void updateUserEntity() {
        riderEntity.setUserReference(userEntity.getUserReference());
        this.riderDAO.saveEntity(riderEntity)
                .observe(owner, aBoolean -> {
                    if (aBoolean) {
                        Rider rider = new Rider(riderEntity, userEntity);
                        postResult(rider);
                    } else {
                        Log.e(TAG, LOC + "receiveUserEntity: ");
                        postResult(null);
                    }
                });
    }
}

/**
 * Sequence of functions to log in as rider
 * @see GetTaskSequencer
 * @author Allan Manuba
 * @version 1.4.2
 * Add dependency injection
 * @vesrion 1.1.1
 */
class LogInAsRiderTask extends GetTaskSequencer<Rider> {
    static final String LOC = RiderDAO.LOC + "LogInAsRiderTask: ";

    private final String password;
    private final String username;
    private final LifecycleOwner owner;
    private UserEntity userEntity;
    private RiderEntity riderEntity;

    LogInAsRiderTask(String username, String password, LifecycleOwner owner, FirebaseFirestore db) {
        super(db);
        this.owner = owner;
        this.username = username;
        this.password = password;
    }

    @Override
    public void doFirstTask() {
        userLogin();
    }

    private void userLogin() {
        UserDAO userDAO = new UserDAO();
        userDAO.logIn(username, password)
                .observe(owner, userEntity -> {
                    if (userEntity == null || userEntity.getRiderReference() == null) {
                        postResult(null);
                    } else {
                        this.userEntity = userEntity;
                        getRiderEntity();
                    }
                });
    }

    private void getRiderEntity() {
        userEntity.getRiderReference()
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        riderEntity = task.getResult().toObject(RiderEntity.class);
                        convertToModel();
                    } else {
                        Log.e(TAG, LOC +"getDriverEntity: onComplete: ", task.getException());
                        postResult(null);
                    }
                });
    }

    private void convertToModel() {
        if (riderEntity == null) {
            Log.e(TAG, LOC + "convertToModel: riderEntity is null");
            postResult(null);
        } else {
            Rider rider = new Rider(riderEntity, userEntity);
            postResult(rider);
        }
    }
}

/**
 * Sequence of functions to get Rider model based on a DocumentReference
 * @see GetTaskSequencer
 * @author Allan Manuba
 * @version 1.4.2
 * Add dependency injection
 * @vesrion 1.1.1
 */
class GetRiderFromReferenceTask extends GetTaskSequencer<Rider> {
    static final String LOC = "RiderDAO: GetRiderFromReferenceTask: ";
    private final DocumentReference riderReference;
    private RiderEntity riderEntity;

    GetRiderFromReferenceTask(DocumentReference riderReference, FirebaseFirestore db) {
        super(db);
        this.riderReference = riderReference;
    }

    @Override
    public void doFirstTask() {
        getRiderEntity();
    }

    private void getRiderEntity() {
        if (riderReference != null) {
            riderReference.get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            this.riderEntity = task.getResult().toObject(RiderEntity.class);
                            DocumentReference userReference = riderEntity.getUserReference();
                            getUserEntity(userReference);
                        } else {
                            Log.e(TAG, LOC + "getRiderEntity: onComplete: ", task.getException());
                            postResult(null);
                        }
                    });
        } else {
            Log.e(TAG, LOC + "getRiderEntity: ");
            postResult(null);
        }
    }

    private void getUserEntity(DocumentReference userReference) {
        if (userReference != null) {
            userReference.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        UserEntity userEntity = task.getResult().toObject(UserEntity.class);
                        Rider rider = new Rider(riderEntity, userEntity);
                        postResult(rider);
                    } else {
                        Log.e(TAG, LOC + "getUserEntity: onComplete: ", task.getException());
                        postResult(null);
                    }
                });
        } else {
            postResult(null);
            Log.e(TAG, LOC + "getUserEntity: No UserEntity");
        }
    }

}