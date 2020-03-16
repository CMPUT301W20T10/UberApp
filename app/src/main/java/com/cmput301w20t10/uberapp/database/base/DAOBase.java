package com.cmput301w20t10.uberapp.database.base;

import androidx.lifecycle.MutableLiveData;

/**
 * Base class for DAO's that handle read and writing for models and entities
 *
 * @param <Entity>
 *     Entities are the one-to-one representation of objects to the database
 * @param <Model>
 *     Models are application objects that front end programmers get to interact with
 *
 * @author Allan Manuba
 * @version 1.0.0
 */
public abstract class DAOBase<Entity extends EntityBase, Model extends ModelBase> {
    /**
     * Saves all dirty fields for a given entity. Dirty fields in an entity are unsaved fields.
     * DatabaseObjectBase detects these changes. It is not encouraged to use this.
     * <p>
     *
     * @param   entity    Entity to update
     * @return  MutableLiveData that views can observe to know whether saving was successful or not
     *
     * @see DatabaseObjectBase
     *
     * @author Allan Manuba
     * @version 1.0.0
     */
    public abstract MutableLiveData<Boolean> saveEntity(Entity entity);

    /**
     * Saves all dirty fields for a given model. Dirty fields in an entity are unsaved fields.
     * DatabaseObjectBase detects these changes. This is the method that's encouraged to use to
     * update your models. To update your models:
     * <p>
     *
     * <pre>
     *     Rider rider;
     *
     *     ...
     *     // code that changes your rider such as
     *     rider.setImage("new image");
     *     ...
     *
     *     MutableLiveData<RiderDAO> liveData = RiderDAO.getDAO();
     *     liveData.observe(lifecycleOwner, riderDAO -> {
     *         if (riderDAO != null) {
     *              // it is recommended that you keep your riderDAO as a local variable
     *              // once you have verified that riderDAO is null
     *              riderDAO.saveModel(rider)
     *                  .observe(...);
     *         } else {
     *              // error handling here
     *              // it is likely that the user was not logged in
     *              // or the user was logged out by the system
     *         }
     *     });
     * </pre>
     *
     * @param   model   Model to update
     * @return  MutableLiveData that views can observe to know whether saving was successful or not
     */
    public abstract MutableLiveData<Boolean> saveModel(Model model);
}
