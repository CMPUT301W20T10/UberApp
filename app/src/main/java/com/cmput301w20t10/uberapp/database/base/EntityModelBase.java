package com.cmput301w20t10.uberapp.database.base;

import com.google.firebase.firestore.Exclude;

import java.util.HashSet;
import java.util.Set;

/**
 * Base class for all Entity and Model classes.
 * It is structured to detect dirty fields by adding the state on the dirty field list if set is
 * ever used.
 *
 * @param <T>
 *     T should be of type enum where the fields represent the fields in the database.
 *     It should override Object.toString(), returning camel case version of these fields.
 * @author Allan Manuba
 */
public abstract class EntityModelBase<T> {
    @Exclude
    protected Set<T> dirtyFieldSet = new HashSet<>();

    /**
     * Adds the state to dirtyFieldSet
     * @param state
     */
    protected void addDirtyField(T state) {
        dirtyFieldSet.add(state);
    }

    /**
     * When overriding, also add the @Exclude annotation to avoid
     * deserializing errors.
     *
     * @return
     */
    @Exclude
    public abstract T[] getDirtyFieldSet();

    /**
     * Clears the set of dirty states
     */
    void clearDirtyStateSet() {
        this.dirtyFieldSet.clear();
    }
}
