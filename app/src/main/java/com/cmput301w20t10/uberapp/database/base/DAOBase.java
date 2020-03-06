package com.cmput301w20t10.uberapp.database.base;

import com.google.android.gms.tasks.Task;

/**
 * Base class for all DAO
 * Created for consistency
 * @param <E extends EntityModelBase>
 *     T should be an Entity object
 *
 * @author Allan Manuba
 */
public abstract class DAOBase<E extends EntityModelBase> {
    /**
     * Saves all the dirty fields in a given entity. Dirty fields in an entity
     * are unsaved fields. EntityModelBase automatically detects these changes.
     *
     * @param entity
     * @return Task which returns the result about whether saving was successful or not
     * @author Allan Manuba
     */
    public abstract Task saveEntity(final E entity);
}
