package com.cmput301w20t10.uberapp.database.base;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Exclude;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class DatabaseObjectBase<Field> {
    @Exclude
    protected Set<Field> dirtyFieldSet = new HashSet<>();

    /**
     * Adds the state to dirtyFieldSet
     * @param state
     */
    protected void addDirtyField(Field state) {
        dirtyFieldSet.add(state);
    }

    /**
     * Clears the set of dirty states
     */
    public void clearDirtyStateSet() {
        this.dirtyFieldSet.clear();
    }
}
