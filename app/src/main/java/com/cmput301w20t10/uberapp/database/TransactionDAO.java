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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import static android.content.ContentValues.TAG;

public class TransactionDAO extends DAOBase<TransactionEntity> {
    static final String COLLECTION = "transactions";
    private final static String LOC = "TransactionDAO: ";

    public MutableLiveData<Transaction> createTransaction(LifecycleOwner owner,
                                                          Rider sender,
                                                          Driver recipient,
                                                          int value) {
        CreateTransactionTask task = new CreateTransactionTask(owner, sender, recipient, value);
        return task.run();
    }

    public MutableLiveData<Transaction> createTransaction(LifecycleOwner owner,
                                                          RideRequest rideRequest) {
        CreateTransactionForRideTask task = new CreateTransactionForRideTask(owner, rideRequest);
        return task.run();
    }

    @Override
    public Task<Void> saveEntity(TransactionEntity entity) {
        final DocumentReference reference = entity.getTransactionReference();
        Task<Void> task = null;

        if (reference != null) {
            Log.e(TAG, "saveEntity: 1 : " + Arrays.toString(entity.getDirtyFieldSet()));
            final Map<String, Object> dirtyPairMap = new HashMap<>();

            for (TransactionEntity.Field field:
                    entity.getDirtyFieldSet()) {
                Object value = null;

                switch (field) {
                    case VALUE:
                        value = entity.getValue();
                        break;
                    case SENDER:
                        value = entity.getSender();
                        break;
                    case RECIPIENT:
                        value = entity.getRecipient();
                        break;
                    case TIMESTAMP:
                        value = entity.getTimestamp();
                        break;
                    case TRANSACTION_REFERENCE:
                        value = entity.getTransactionReference();
                        break;
                    default:
                        break;
                }

                if (value != null) {
                    dirtyPairMap.put(field.toString(), value);
                }
            }

            entity.clearDirtyStateSet();

            if (dirtyPairMap.size() > 0) {
                task = reference.update(dirtyPairMap);
            }
        } else {
            Log.e(TAG, LOC + "saveEntity: transactionReference is null");
        }

        return task;
    }

    public Task<Void> saveModel(final Transaction transaction) {
        return saveEntity(new TransactionEntity(transaction));
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
    public MutableLiveData<Transaction> run() {
        getSender();
        return liveData;
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
                        liveData.setValue(null);
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
                            liveData.setValue(transaction);
                        } else {
                            Log.e(TAG, LOC + "getRecipient: onComplete: ", task.getException());
                            liveData.setValue(null);
                        }
                    }
                });
    }
}

class CreateTransactionTask extends GetTaskSequencer<Transaction> {
    static final String LOC = "TransactionDAO: CreateTransactionTask: ";

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
    public MutableLiveData<Transaction> run() {
        initCreation();
        return liveData;
    }

    private void initCreation() {
        transactionEntity = new TransactionEntity(sender, recipient, value);
        db.collection(TransactionDAO.COLLECTION)
                .add(transactionEntity)
                .addOnSuccessListener(documentReference -> {
                    transactionEntity.setTransactionReference(documentReference);
                    updateTransactionEntity();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, LOC + "onFailure: ", e);
                    liveData.setValue(null);
                });
    }

    private void updateTransactionEntity() {
        transactionDAO = new TransactionDAO();
        transactionDAO.saveEntity(transactionEntity)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        convertToTransaction();
                    } else {
                        Log.e(TAG, "updateTransactionEntity: onComplete: ", task.getException());
                        liveData.setValue(null);
                    }
                });
    }

    private void convertToTransaction() {
        transactionDAO.transactionEntityToModel(transactionEntity)
                .observe(owner, transaction -> {
                    if (transaction != null) {
                        liveData.setValue(transaction);
                    } else {
                        Log.e(TAG, LOC + "convertToTransaction: onChanged: ");
                        liveData.setValue(null);
                    }
                });
    }
}

class CreateTransactionForRideTask extends GetTaskSequencer<Transaction> {
    final static String LOC = "TransactionDAO: CreateTransactionForRideTask: ";

    private final RideRequest rideRequest;
    private final LifecycleOwner owner;

    private Rider sender;
    private Driver recipient;

    CreateTransactionForRideTask(LifecycleOwner owner, RideRequest rideRequest) {
        this.owner = owner;
        this.rideRequest = rideRequest;
    }

    @Override
    public MutableLiveData<Transaction> run() {
        getRiderObject();
        return liveData;
    }

    private void getRiderObject() {
        RiderDAO riderDAO = new RiderDAO();
        riderDAO.getRiderFromRiderReference(rideRequest.getRiderReference())
                .observe(owner, new Observer<Rider>() {
                    @Override
                    public void onChanged(Rider rider) {
                        if (rider != null) {
                            sender = rider;
                            getDriverObject();
                        } else {
                            Log.e(TAG, LOC + "getRiderObject: onChanged: rider null");
                            liveData.setValue(null);
                        }
                    }
                });
    }

    private void getDriverObject() {
        DriverDAO driverDAO = new DriverDAO();
        driverDAO.getDriverFromDriverReference(rideRequest.getDriverReference())
        .observe(owner, new Observer<Driver>() {
            @Override
            public void onChanged(Driver driver) {
                if (driver != null) {
                    recipient = driver;
                    createTransaction();
                } else {
                    Log.e(TAG, LOC + "onChanged: driver null");
                    liveData.setValue(null);
                }
            }
        });
    }

    private void createTransaction() {
        // todo: yea
    }

    /**
     * Change state for ride
     * Add reference to transaction
     * To history for rider
     * Add transaction to list for rider
     * todo: rename all instance of payment stuff for rider
     * To history driver
     * Add transaction to list for driver
     * todo: rename all instance of payment stuff for driver
     */
    private void finishRideRequest() {

    }
}