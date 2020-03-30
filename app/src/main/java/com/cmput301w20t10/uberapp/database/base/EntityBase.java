package com.cmput301w20t10.uberapp.database.base;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Exclude;

import java.util.Map;

/**
 * Base class for all Entity and Model classes.
 * It is structured to detect dirty fields by adding the state on the dirty field list if set is
 * ever used.
 *
 * @param <Field>
 *     T should be of type enum where the fields represent the fields in the database.
 *     It should override Object.toString(), returning camel case version of these fields.
 *
 * @author Allan Manuba
 * @version 1.1.4
 * Remove redundant clearDirtyFieldSet();
 *
 * @version 1.1.3
 * Add abstract getMainReference for DAOBase usage in saveEntity
 *
 * @version 1.1.2
 * Remove dirtyFieldSet that shadows the dirtyFieldSet in DatabaseObjectBase
 *
 * @version 1.1.1
 */
public abstract class EntityBase<Field> extends DatabaseObjectBase<Field> {
    /**
     * Add @Exclude every time you override this
     *
     * @return  Primary document reference which points to the database counterpart of the entity
     */
    @Exclude
    public abstract DocumentReference getMainReference();

    /**
     * When overriding, also add the @Exclude annotation to avoid
     * deserializing errors
     *
     * @return A Map<String, Object> object which can be used to update documents in Firestore
     */
    @Exclude
    public abstract Map<String, Object> getDirtyFieldMap();
}
