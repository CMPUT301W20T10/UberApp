package com.cmput301w20t10.uberapp.database.base;

import android.util.Log;

import com.cmput301w20t10.uberapp.database.entity.UserEntity;
import com.google.firebase.firestore.DocumentReference;

import java.util.List;
import java.util.Map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import static android.content.ContentValues.TAG;
import static android.net.wifi.rtt.CivicLocationKeys.LOC;

/**
 * Base class for all DAO
 * Created for consistency
 * @param <Entity extends EntityModelBase>
 *     T should be an Entity object
 *
 * @author Allan Manuba
 */
public abstract class DAOBase<Entity extends EntityBase, Model extends ModelBase> {
    /**
     * Saves all the dirty fields in a given entity. Dirty fields in an entity
     * are unsaved fields. EntityModelBase automatically detects these changes.
     *
     * @param entity
     * @return Task which returns the result about whether saving was successful or not
     * @author Allan Manuba
     */
    public abstract MutableLiveData<Boolean> saveEntity(Entity entity);

    public abstract MutableLiveData<Boolean> saveModel(Model model);
}
