package com.cmput301w20t10.uberapp.database.entity;

import com.cmput301w20t10.uberapp.database.base.EntityModelBase;
import com.cmput301w20t10.uberapp.models.Driver;
import com.cmput301w20t10.uberapp.models.Payment;
import com.cmput301w20t10.uberapp.models.Rider;
import com.cmput301w20t10.uberapp.models.User;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import java.util.Date;

/**
 * Entity representation for Payment model.
 * Entity objects are the one-to-one representation of objects from the database.
 *
 * @author Allan Manuba
 */
public class PaymentEntity extends EntityModelBase<PaymentEntity.Field> {
    private DocumentReference paymentReference;
    private Timestamp timestamp;
    private DocumentReference recipient;
    private DocumentReference sender;
    private int value;

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

    public PaymentEntity() {}

    public PaymentEntity(Rider sender, Driver recipient, int value) {
        this.sender = sender.getUserReference();
        this.recipient = recipient.getUserReference();
        this.timestamp = new Timestamp(new Date());
        this.value = value;
    }

    public PaymentEntity(Payment payment) {
        super();
        this.paymentReference = payment.getPaymentReference();
        this.timestamp = new Timestamp(payment.getTimestamp());
        this.recipient = payment.getRecipient().getUserReference();
        this.sender = payment.getSender().getUserReference();
        this.value = payment.getValue();
    }

    @Override
    public Field[] getDirtyFieldSet() {
        return dirtyFieldSet.toArray(new Field[0]);
    }


    // region setters and getters
    public DocumentReference getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(DocumentReference paymentReference) {
        this.paymentReference = paymentReference;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public DocumentReference getRecipient() {
        return recipient;
    }

    public void setRecipient(DocumentReference recipient) {
        this.recipient = recipient;
    }

    public DocumentReference getSender() {
        return sender;
    }

    public void setSender(DocumentReference sender) {
        this.sender = sender;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
    // endregion setters and getters
}
