package com.cmput301w20t10.uberapp.database.base;

import com.google.android.gms.tasks.Task;

public abstract class DAOBase<T> {
    public abstract Task save(final T entity);
}
