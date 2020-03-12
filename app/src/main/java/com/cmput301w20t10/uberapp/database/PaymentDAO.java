package com.cmput301w20t10.uberapp.database;

import android.util.Log;

import com.cmput301w20t10.uberapp.database.base.DAOBase;
import com.cmput301w20t10.uberapp.database.entity.PaymentEntity;
import com.cmput301w20t10.uberapp.database.entity.UserEntity;
import com.cmput301w20t10.uberapp.database.util.GetTaskSequencer;
import com.cmput301w20t10.uberapp.models.Driver;
import com.cmput301w20t10.uberapp.models.Payment;
import com.cmput301w20t10.uberapp.models.Rider;
import com.cmput301w20t10.uberapp.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

class PaymentDAO extends DAOBase<PaymentEntity> {
    static final String COLLECTION = "payments";
    private final static String LOC = "PaymentDAO: ";

    public MutableLiveData<Payment> createPayment(LifecycleOwner owner,
                                                  Rider sender,
                                                  Driver recipient,
                                                  int value) {
        CreatePaymentTask task = new CreatePaymentTask(owner, sender, recipient, value);
        return task.run();
    }

    @Override
    public Task<Void> saveEntity(PaymentEntity entity) {
        final DocumentReference reference = entity.getPaymentReference();
        Task<Void> task = null;

        if (reference != null) {
            Log.e(TAG, "saveEntity: 1 : " + Arrays.toString(entity.getDirtyFieldSet()));
            final Map<String, Object> dirtyPairMap = new HashMap<>();

            for (PaymentEntity.Field field:
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
                    case PAYMENT_REFERENCE:
                        value = entity.getPaymentReference();
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
            Log.e(TAG, LOC + "saveEntity: paymentReference is null");
        }

        return task;
    }

    public Task<Void> saveModel(final Payment payment) {
        return saveEntity(new PaymentEntity(payment));
    }

    public MutableLiveData<Payment> paymentEntityToModel(PaymentEntity entity) {
        PaymentEntityToModelTask task = new PaymentEntityToModelTask(entity);
        return task.run();
    }
}

class PaymentEntityToModelTask extends GetTaskSequencer<Payment> {
    static final String LOC = "PaymentDAO: PaymentEntityToModelTask: ";

    private final PaymentEntity paymentEntity;
    private User sender;

    PaymentEntityToModelTask(PaymentEntity paymentEntity) {
        this.paymentEntity = paymentEntity;
    }

    @Override
    public MutableLiveData<Payment> run() {
        getSender();
        return liveData;
    }

    private void getSender() {
        paymentEntity.getSender().get()
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
        paymentEntity.getRecipient().get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            UserEntity userEntity = task.getResult().toObject(UserEntity.class);
                            User recipient = new User(userEntity);
                            Payment payment = new Payment(paymentEntity.getPaymentReference(),
                                    paymentEntity.getTimestamp().toDate(),
                                    sender,
                                    recipient,
                                    paymentEntity.getValue());
                            liveData.setValue(payment);
                        } else {
                            Log.e(TAG, LOC + "getRecipient: onComplete: ", task.getException());
                            liveData.setValue(null);
                        }
                    }
                });
    }
}

class CreatePaymentTask extends GetTaskSequencer<Payment> {
    static final String LOC = "PaymentDAO: CreatePaymentTask: ";

    private final int value;
    private final Driver recipient;
    private final Rider sender;
    private final LifecycleOwner owner;

    private PaymentEntity paymentEntity;
    private PaymentDAO paymentDAO;

    CreatePaymentTask(LifecycleOwner owner, Rider sender, Driver recipient, int value) {
        this.owner = owner;
        this.sender = sender;
        this.recipient = recipient;
        this.value = value;
    }

    @Override
    public MutableLiveData<Payment> run() {
        initCreation();
        return liveData;
    }

    private void initCreation() {
        paymentEntity = new PaymentEntity(sender, recipient, value);
        db.collection(PaymentDAO.COLLECTION)
                .add(paymentEntity)
                .addOnSuccessListener(documentReference -> {
                    paymentEntity.setPaymentReference(documentReference);
                    updatePaymentEntity();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, LOC + "onFailure: ", e);
                    liveData.setValue(null);
                });
    }

    private void updatePaymentEntity() {
        paymentDAO = new PaymentDAO();
        paymentDAO.saveEntity(paymentEntity)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        convertToPayment();
                    } else {
                        Log.e(TAG, "updatePaymentEntity: onComplete: ", task.getException());
                        liveData.setValue(null);
                    }
                });
    }

    private void convertToPayment() {
        paymentDAO.paymentEntityToModel(paymentEntity)
                .observe(owner, payment -> {
                    if (payment != null) {
                        liveData.setValue(payment);
                    } else {
                        Log.e(TAG, LOC + "convertToPayment: onChanged: ");
                        liveData.setValue(null);
                    }
                });
    }
}