package com.cmput301w20t10.uberapp.database.entity;

import com.cmput301w20t10.uberapp.database.base.EntityModelBase;
import com.cmput301w20t10.uberapp.models.Driver;
import com.cmput301w20t10.uberapp.models.Transaction;
import com.cmput301w20t10.uberapp.models.Rider;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Exclude;

import java.util.Date;

/**
 * Entity representation for Transaction model.
 * Entity objects are the one-to-one representation of objects from the database.
 *
 * @author Allan Manuba
 */
public class TransactionEntity extends EntityModelBase<TransactionEntity.Field> {
    private DocumentReference transactionReference;
    private Timestamp timestamp;
    private DocumentReference recipient;
    private DocumentReference sender;
    private float value;

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

    public TransactionEntity() {}

    public TransactionEntity(Rider sender, Driver recipient, int value) {
        this.sender = sender.getUserReference();
        this.recipient = recipient.getUserReference();
        this.timestamp = new Timestamp(new Date());
        this.value = value;
    }

    public TransactionEntity(Transaction transaction) {
        super();
        this.transactionReference = transaction.getTransactionReference();
        this.timestamp = new Timestamp(transaction.getTimestamp());
        this.recipient = transaction.getRecipient().getUserReference();
        this.sender = transaction.getSender().getUserReference();
        this.value = transaction.getValue();
    }

    @Override
    @Exclude
    public Field[] getDirtyFieldSet() {
        return dirtyFieldSet.toArray(new Field[0]);
    }


    // region setters and getters
    public DocumentReference getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(DocumentReference transactionReference) {
        addDirtyField(Field.TRANSACTION_REFERENCE);
        this.transactionReference = transactionReference;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        addDirtyField(Field.TIMESTAMP);
        this.timestamp = timestamp;
    }

    public DocumentReference getRecipient() {
        return recipient;
    }

    public void setRecipient(DocumentReference recipient) {
        addDirtyField(Field.RECIPIENT);
        this.recipient = recipient;
    }

    public DocumentReference getSender() {
        return sender;
    }

    public void setSender(DocumentReference sender) {
        addDirtyField(Field.SENDER);
        this.sender = sender;
    }

    public float getValue() {
        return value;
    }

    public void setValue(int value) {
        addDirtyField(Field.VALUE);
        this.value = value;
    }
    // endregion setters and getters
}
