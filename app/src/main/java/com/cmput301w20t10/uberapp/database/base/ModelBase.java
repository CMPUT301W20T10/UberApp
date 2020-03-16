package com.cmput301w20t10.uberapp.database.base;

public abstract class ModelBase<Field, Entity extends EntityBase> extends DatabaseObjectBase<Field> {
    public abstract void transferChanges(Entity entity);
}
