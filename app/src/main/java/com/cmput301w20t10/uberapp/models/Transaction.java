package com.cmput301w20t10.uberapp.models;

import com.cmput301w20t10.uberapp.database.base.EntityModelBase;
import com.cmput301w20t10.uberapp.database.entity.TransactionEntity;
import com.google.firebase.firestore.DocumentReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class Transaction extends EntityModelBase<Transaction.Field> {
    private DocumentReference transactionReference;
    private final Date timestamp;
    private final User recipient;
    private final User sender;
    private final float value;

    public enum Field {
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
    public Field[] getDirtyFieldSet() {
        return dirtyFieldSet.toArray(new Field[0]);
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

    public User getRecipient() {
        return recipient;
    }

    public User getSender() {
        return sender;
    }

    public float getValue() {
        return value;
    }
    // endregion getters and setters
}
