package com.cmput301w20t10.uberapp.database.base;

import android.util.Log;

import com.google.firebase.firestore.Exclude;

import java.util.HashSet;
import java.util.Set;

/**
 * Base class for both Models and Entities
 *
 * @param <Field>
 *     Fields are enums that represents the fields of the object.
 *     If that object's parent is EntityBase, this field must be camel cased and
 *     it should represent the database columns or fields in Firestore
 *
 * @author Allan Manuba
 * @version 1.0.1
 */
public abstract class DatabaseObjectBase<Field> {
    /**
     * Stores all the dirty fields
     */
    @Exclude
    protected Set<Field> dirtyFieldSet = new HashSet<>();

    /**
     * Adds the field to dirtyFieldSet
     * @param field
     */
    protected void addDirtyField(Field field) {
        dirtyFieldSet.add(field);
    }

    /**
     * Clears the set of dirty states
     */
    public void clearDirtyFieldSet() {
        this.dirtyFieldSet.clear();
    }
}
