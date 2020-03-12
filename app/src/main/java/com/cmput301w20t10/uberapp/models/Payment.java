package com.cmput301w20t10.uberapp.models;

import com.cmput301w20t10.uberapp.database.base.EntityModelBase;
import com.cmput301w20t10.uberapp.database.entity.PaymentEntity;
import com.google.firebase.firestore.DocumentReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class Payment extends EntityModelBase<Payment.Field> {
    private DocumentReference paymentReference;
    private final Date timestamp;
    private final User recipient;
    private final User sender;
    private final int value;

    public enum Field {
        VALUE ("value"),
        SENDER ("sender"),
        RECIPIENT ("recipient"),
        TIMESTAMP ("timestamp"),
        PAYMENT_REFERENCE ("paymentReference");

        private String stringValue;

        Field(String fieldName) {
            this.stringValue = fieldName;
        }

        public String toString() {
            return stringValue;
        }
    }

    public Payment(User sender, User recipient, int value) {
        this.value = value;
        this.sender = sender;
        this.recipient = recipient;
        this.timestamp = new Date();
        this.paymentReference = null;
    }

    public Payment(DocumentReference paymentReference,
                   Date timestamp,
                   User recipient,
                   User sender,
                   int  value) {
        this.paymentReference = paymentReference;
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
        data.put(Field.PAYMENT_REFERENCE.toString(), paymentReference.getPath());
        return data;
    }

    @Override
    public Field[] getDirtyFieldSet() {
        return dirtyFieldSet.toArray(new Field[0]);
    }

    // region getters and setters
    public DocumentReference getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(DocumentReference paymentReference) {
        addDirtyField(Field.PAYMENT_REFERENCE);
        this.paymentReference = paymentReference;
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

    public int getValue() {
        return value;
    }
    // endregion getters and setters
}
