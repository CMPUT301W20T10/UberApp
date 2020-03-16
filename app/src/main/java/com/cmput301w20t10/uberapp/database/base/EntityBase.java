package com.cmput301w20t10.uberapp.database.base;

import android.util.Log;

import com.google.firebase.firestore.Exclude;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Base class for all Entity and Model classes.
 * It is structured to detect dirty fields by adding the state on the dirty field list if set is
 * ever used.
 *
 * @param <Field>
 *     T should be of type enum where the fields represent the fields in the database.
 *     It should override Object.toString(), returning camel case version of these fields.
 * @author Allan Manuba
 * @version 1.0.0
 * @version 1.0.1
 * Remove dirtyFieldSet that shadows the dirtyFieldSet in DatabaseObjectBase
 */
public abstract class EntityBase<Field> extends DatabaseObjectBase<Field> {
    /**
     * When overriding, also add the @Exclude annotation to avoid
     * deserializing errors
     *
     * @return A Map<String, Object> object which can be used to update documents in Firestore
     */
    @Exclude
    public abstract Map<String, Object> getDirtyFieldMap();

    /**
     * Clears the set of dirty states
     */
    public void clearDirtyFieldSet() {
        this.dirtyFieldSet.clear();
    }
}
