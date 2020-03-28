package com.cmput301w20t10.uberapp.models;

import android.util.Log;

import com.cmput301w20t10.uberapp.database.base.EntityBase;
import com.cmput301w20t10.uberapp.database.base.ModelBase;
import com.cmput301w20t10.uberapp.database.entity.TransactionEntity;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.cmput301w20t10.uberapp.models.Transaction.*;

public class Transaction extends ModelBase<Field, TransactionEntity> {
    private static final String LOC = "Transaction";
    private DocumentReference transactionReference;
    private Date timestamp;
    private User recipient;
    private User sender;
    private float value;

    enum Field {
        VALUE ("value"),
        SENDER ("sender"),
        RECIPIENT ("recipient"),
        TIMESTAMP ("timestamp"),
        TRANSACTION_REFERENCE("transactionReference");

        private String stringValue;

        Field(String fieldName) {
            this.stringValue = fieldName;
        }

        public String toString() {
            return stringValue;
        }
    }

    public Transaction(User sender, User recipient, float value) {
        this.value = value;
        this.sender = sender;
        this.recipient = recipient;
        this.timestamp = new Date();
        this.transactionReference = null;
    }

    public Transaction(DocumentReference transactionReference,
                       Date timestamp,
                       User recipient,
                       User sender,
                       float  value) {
        this.transactionReference = transactionReference;
        this.timestamp = timestamp;
        this.recipient = recipient;
        this.sender = sender;
        this.value = value;
    }

    // todo: use this instead so it could be savable
    public JSONObject generateJSONObject() throws JSONException {
        JSONObject data = new JSONObject();
        data.put(Field.SENDER.toString(), sender.getUsername());
        data.put(Field.RECIPIENT.toString(), recipient.getUsername());
        data.put(Field.VALUE.toString(), value);
        data.put(Field.TRANSACTION_REFERENCE.toString(), transactionReference.getPath());
        data.put(Field.TIMESTAMP.toString(), timestamp.getTime());
        return data;
    }

    @Override
    public void transferChanges(TransactionEntity entity) {
        for (Field dirtyField :
                dirtyFieldSet) {
            switch (dirtyField) {
                case VALUE:
                    entity.setValue(getValue());
                    break;
                case SENDER:
                    entity.setSender(getSender().getUserReference());
                    break;
                case RECIPIENT:
                    entity.setRecipient(getRecipient().getUserReference());
                    break;
                case TIMESTAMP:
                    entity.setTimestamp(new Timestamp(getTimestamp()));
                    break;
                case TRANSACTION_REFERENCE:
                    entity.setTransactionReference(getTransactionReference());
                    break;
                default:
                    Log.e(TAG, LOC + "transferChanges: Unknown field: " + dirtyField.toString());
                    break;
            }
        }
    }

    // region getters and setters
    public DocumentReference getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(DocumentReference transactionReference) {
        addDirtyField(Field.TRANSACTION_REFERENCE);
        this.transactionReference = transactionReference;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        addDirtyField(Field.TIMESTAMP);
        this.timestamp = timestamp;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        addDirtyField(Field.RECIPIENT);
        this.recipient = recipient;
    }

    public User getSender() {
        return sender;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        addDirtyField(Field.VALUE);
        this.value = value;
    }
    // endregion getters and setters
}
