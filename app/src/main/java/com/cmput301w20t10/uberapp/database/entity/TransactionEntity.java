package com.cmput301w20t10.uberapp.database.entity;

import com.cmput301w20t10.uberapp.database.base.EntityBase;
import com.cmput301w20t10.uberapp.models.Driver;
import com.cmput301w20t10.uberapp.models.Transaction;
import com.cmput301w20t10.uberapp.models.Rider;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Exclude;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.cmput301w20t10.uberapp.database.entity.TransactionEntity.*;

/**
 * Entity representation for Transaction model.
 * @see EntityBase
 *
 * @author Allan Manuba
 * @version 1.0.0
 */
public class TransactionEntity extends EntityBase<Field> {

    // region Fields
    /**
     * Fields
     * @version 1.0.0
     */

    private DocumentReference transactionReference;
    private Timestamp timestamp;
    private DocumentReference recipient;
    private DocumentReference sender;
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

        @Override
        public String toString() {
            return stringValue;
        }
    }
    // endregion Fields

    // region Constructors
    /**
     * Constructors
     * @version 1.0.0
     */
    public TransactionEntity() { super(); }

    public TransactionEntity(Rider sender, Driver recipient, int value) {
        super();
        this.sender = sender.getUserReference();
        this.recipient = recipient.getUserReference();
        this.timestamp = new Timestamp(new Date());
        this.value = value;
    }
    // endregion Constructors

    /**
     * @see EntityBase#addDirtyField(Object)
     *
     * @return a map that can be used to update a Firestore reference
     *
     * @author Allan Manuba
     * @version 1.0.0
     */
    @Override
    @Exclude
    public Map<String, Object> getDirtyFieldMap() {
        Map<String, Object> dirtyFieldMap = new HashMap<>();
        for (Field dirtyField :
                dirtyFieldSet) {
            switch (dirtyField) {
                case VALUE:
                    dirtyFieldMap.put(dirtyField.toString(), getValue());
                    break;
                case SENDER:
                    dirtyFieldMap.put(dirtyField.toString(), getSender());
                    break;
                case RECIPIENT:
                    dirtyFieldMap.put(dirtyField.toString(), getRecipient());
                    break;
                case TIMESTAMP:
                    dirtyFieldMap.put(dirtyField.toString(), getTimestamp());
                    break;
                case TRANSACTION_REFERENCE:
                    dirtyFieldMap.put(dirtyField.toString(), getTransactionReference());
                    break;
            }
        }
        return dirtyFieldMap;
    }

    // region Setters and getters
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

    public void setValue(float value) {
        addDirtyField(Field.VALUE);
        this.value = value;
    }
    // endregion Setters and getters
}
