package com.cmput301w20t10.uberapp.database;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.HashSet;
import java.util.Set;

@IgnoreExtraProperties
public abstract class EntityModelBase<T> {
    @Exclude
    protected Set<T> dirtyFieldList = new HashSet<>();

    void setDirty(T state) {
        dirtyFieldList.add(state);
    }

    @Exclude
    public abstract T[] getDirtyFieldList();

    void clearDirtyStateList() {
        this.dirtyFieldList.clear();
    }
}
