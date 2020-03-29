package com.cmput301w20t10.uberapp.database;


import android.util.Log;

import com.cmput301w20t10.uberapp.database.base.DAOBase;
import com.cmput301w20t10.uberapp.database.entity.TransactionEntity;
import com.cmput301w20t10.uberapp.database.entity.UserEntity;
import com.cmput301w20t10.uberapp.database.util.GetTaskSequencer;
import com.cmput301w20t10.uberapp.models.Driver;
import com.cmput301w20t10.uberapp.models.RideRequest;
import com.cmput301w20t10.uberapp.models.Transaction;
import com.cmput301w20t10.uberapp.models.Rider;
import com.cmput301w20t10.uberapp.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import static android.content.ContentValues.TAG;

public class TransactionDAO extends DAOBase<TransactionEntity, Transaction> {
    static final String COLLECTION = "transactions";
    private final static String LOC = "TransactionDAO: ";

    public TransactionDAO() {}

    @Deprecated
    public MutableLiveData<Transaction> createTransaction(LifecycleOwner owner,
                                                          Rider sender,
                                                          Driver recipient,
                                                          int value) {
        CreateTransactionTask task = new CreateTransactionTask(owner, sender, recipient, value);
        return task.run();
    }

    public MutableLiveData<Transaction> createTransaction(LifecycleOwner owner,
                                                          RideRequest rideRequest,
                                                          int value) {
        CreateTransactionForRideTask task = new CreateTransactionForRideTask(owner, rideRequest, value);
        return task.run();
    }

    @Override
    public MutableLiveData<Boolean> saveModel(Transaction transaction) {
        TransactionEntity entity = new TransactionEntity();
        transaction.transferChanges(entity);
        return saveEntity(entity);
    }

    @Override
    protected String getCollectionName() {
        return COLLECTION;
    }

    @Override
    protected DAOBase<TransactionEntity, Transaction> create() {
        return new TransactionDAO();
    }

    @Override
    protected MutableLiveData<Transaction> createModelFromEntity(TransactionEntity entity) {
        TransactionEntityToModelTask task = new TransactionEntityToModelTask(entity);
        return task.run();
    }

    @Override
    protected TransactionEntity createObjectFromSnapshot(DocumentSnapshot snapshot) {
        return snapshot.toObject(TransactionEntity.class);
    }

    public MutableLiveData<Transaction> transactionEntityToModel(TransactionEntity entity) {
        TransactionEntityToModelTask task = new TransactionEntityToModelTask(entity);
        return task.run();
    }
}

class TransactionEntityToModelTask extends GetTaskSequencer<Transaction> {
    static final String LOC = "PaymentDAO: PaymentEntityToModelTask: ";

    private final TransactionEntity transactionEntity;
    private User sender;

    TransactionEntityToModelTask(TransactionEntity transactionEntity) {
        this.transactionEntity = transactionEntity;
    }

    @Override
    public void doFirstTask() {
        getSender();
    }

    private void getSender() {
        transactionEntity.getSender().get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        UserEntity userEntity = task.getResult().toObject(UserEntity.class);
                        sender = new User(userEntity);
                        getRecipient();
                    } else {
                        Log.e(TAG, LOC + "getSender: onComplete: ", task.getException());
                        postResult(null);
                    }
                });
    }

    private void getRecipient() {
        transactionEntity.getRecipient().get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            UserEntity userEntity = task.getResult().toObject(UserEntity.class);
                            User recipient = new User(userEntity);
                            Transaction transaction = new Transaction(transactionEntity.getTransactionReference(),
                                    transactionEntity.getTimestamp().toDate(),
                                    sender,
                                    recipient,
                                    transactionEntity.getValue());
                            postResult(transaction);
                        } else {
                            Log.e(TAG, LOC + "getRecipient: onComplete: ", task.getException());
                            postResult(null);
                        }
                    }
                });
    }
}

class CreateTransactionTask extends GetTaskSequencer<Transaction> {
    static final String LOC = "Toamte: TransactionDAO: CreateTransactionTask: ";

    private final int value;
    private final Driver recipient;
    private final Rider sender;
    private final LifecycleOwner owner;

    private TransactionEntity transactionEntity;
    private TransactionDAO transactionDAO;

    CreateTransactionTask(LifecycleOwner owner, Rider sender, Driver recipient, int value) {
        this.owner = owner;
        this.sender = sender;
        this.recipient = recipient;
        this.value = value;
    }

    @Override
    public void doFirstTask() {
        initCreation();
    }

    private void initCreation() {
        transactionEntity = new TransactionEntity(sender, recipient, value);
        db.collection(TransactionDAO.COLLECTION)
                .add(transactionEntity)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, LOC + "initCreation: ");
                    transactionEntity.setTransactionReference(documentReference);
                    updateTransactionEntity();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, LOC + "onFailure: ", e);
                    postResult(null);
                });
    }

    private void updateTransactionEntity() {
        transactionDAO = new TransactionDAO();
        transactionDAO.saveEntity(transactionEntity)
                .observe(owner, aBoolean -> {
                    if (aBoolean) {
                        convertToTransaction();
                    } else {
                        Log.e(TAG, LOC + "updateTransactionEntity: onComplete: ");
                        postResult(null);
                    }
                });
    }

    private void convertToTransaction() {
        transactionDAO.transactionEntityToModel(transactionEntity)
                .observe(owner, transaction -> {
                    if (transaction != null) {
                        postResult(transaction);
                    } else {
                        Log.e(TAG, LOC + "convertToTransaction: onChanged: ");
                        postResult(null);
                    }
                });
    }
}

class CreateTransactionForRideTask extends GetTaskSequencer<Transaction> {
    final static String LOC = "Tomate: TransactionDAO: CreateTransactionForRideTask: ";

    private final RideRequest rideRequest;
    private final LifecycleOwner owner;
    private final int value;

    private TransactionEntity transactionEntity;
    private Rider sender;
    private Driver recipient;
    RiderDAO riderDAO;
    DriverDAO driverDAO;

    CreateTransactionForRideTask(LifecycleOwner owner, RideRequest rideRequest, int value) {
        this.value = value;
        this.owner = owner;
        this.rideRequest = rideRequest;
    }

    @Override
    public void doFirstTask() {
        getRiderObject();
    }

    private void getRiderObject() {
        riderDAO = new RiderDAO();
        riderDAO.getRiderFromRiderReference(rideRequest.getRiderReference())
                .observe(owner, rider -> {
                    if (rider != null) {
                        sender = rider;
                        getDriverObject();
                    } else {
                        Log.e(TAG, LOC + "getRiderObject: onChanged: rider null");
                        postResult(null);
                    }
                });
    }

    private void getDriverObject() {
        driverDAO = new DriverDAO();
        driverDAO.getDriverFromDriverReference(rideRequest.getDriverReference())
        .observe(owner, driver -> {
            if (driver != null) {
                recipient = driver;
                createTransaction();
            } else {
                Log.e(TAG, LOC + "onChanged: driver null");
                postResult(null);
            }
        });
    }

    private void createTransaction() {
        transactionEntity = new TransactionEntity(sender, recipient, value);
        db.collection(TransactionDAO.COLLECTION)
                .add(transactionEntity)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, LOC + "createTransaction: ");
                    updateTransactionEntity(documentReference);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, LOC + "createTransaction: onFailure: ", e);
                    postResult(null);
                });
    }

    private void updateTransactionEntity(DocumentReference documentReference) {
        transactionEntity.setTransactionReference(documentReference);
        TransactionDAO transactionDAO = new TransactionDAO();
        transactionDAO.saveEntity(transactionEntity)
                .observe(owner, aBoolean -> {
                    if (aBoolean) {
                        Log.d(TAG, LOC + "updateTransactionEntity: ");
                        updateRideRequest();
                    } else {
                        Log.e(TAG, LOC + "updateTransactionEntity: onComplete: ");
                        postResult(null);
                    }});
    }

    private void updateRideRequest() {
        rideRequest.setState(RideRequest.State.TransactionFinished);
        RideRequestDAO rideRequestDAO = new RideRequestDAO();
        rideRequestDAO.saveModel(rideRequest)
                .observe(owner, aBoolean -> {
                    if (aBoolean) {
                        updateRider();
                    } else {
                        Log.e(TAG, LOC + "updateRideRequest: onComplete: Fail to save ride request");
                        postResult(null);
                    }
                });
    }

    private void updateRider() {
        sender.deactivateRideRequest(rideRequest);
        riderDAO.saveModel(sender)
                .observe(owner, aBoolean -> {
                    if (aBoolean) {
                        Log.d(TAG, "updateRider: saveComplete: ");
                        updateDriver();
                    } else {
                        Log.e(TAG, LOC + "updateRider: onComplete: Fail to update rider");
                        postResult(null);
                    }
                });
    }

    private void updateDriver() {
        recipient.deactivateRideRequest(rideRequest);
        driverDAO.saveModel(recipient)
                .observe(owner, aBoolean -> {
                    if (aBoolean) {
                        Transaction transaction = new Transaction(transactionEntity.getTransactionReference(),
                                transactionEntity.getTimestamp().toDate(),
                                recipient,
                                sender,
                                value);
                        postResult(transaction);
                    } else {
                        Log.e(TAG, LOC + "updateDriver: onChanged: ");
                        postResult(null);
                    }
                });
    }
}