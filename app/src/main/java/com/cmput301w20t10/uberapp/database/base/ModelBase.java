package com.cmput301w20t10.uberapp.database.base;

import androidx.lifecycle.MutableLiveData;

/**
 * Base class for Model objects
 *
 * @param <Field>
 *     Field that corresponds to all the field of its sub classes.
 *     It is used to record the fields that should be saved.
 * @param <Entity>
 *     This is the entity equivalent of this model object.
 *
 * @author Allan Manuba
 * @version 1.1.1
 */
public abstract class ModelBase<Field, Entity extends EntityBase> extends DatabaseObjectBase<Field> {
    /**
     * Transfers all essential fields in the model base to its
     * principal entity equivalent. This should be used only in database related classes.
     *
     * @param entity
     *
     * @author Allan Manuba
     */
    public abstract void transferChanges(Entity entity);
}
