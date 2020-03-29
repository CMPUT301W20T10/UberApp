package com.cmput301w20t10.uberapp.database.base;

import android.media.midi.MidiDevice;
import android.view.Display;

import com.cmput301w20t10.uberapp.database.util.DatabaseLogger;
import com.cmput301w20t10.uberapp.database.util.GetTaskSequencer;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
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
 * @version 1.1.3
 * Add way to convert references to Model objects
 *
 * @version 1.1.2
 * Apply same default saving style for entities and model for all subclasses
 *
 * @version 1.1.1
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
     * @version 1.1.1.1
     */
    public MutableLiveData<Boolean> saveEntity(Entity entity) {
        final DocumentReference reference = entity.getMainReference();
        final MutableLiveData<Boolean> liveData = new MutableLiveData<>();

        if (reference != null) {
            final Map<String, Object> dirtyFieldMap = entity.getDirtyFieldMap();
            reference.update(dirtyFieldMap)
                    .addOnCompleteListener(task -> {
                        final boolean isSuccessful = task.isSuccessful();
                        if (!isSuccessful) {
                            DatabaseLogger.error(new Exception(), "", task.getException());
                        }
                        liveData.setValue(isSuccessful);
                    });
        } else {
            DatabaseLogger.error(new Exception(), "Reference is null", null);
            liveData.setValue(false);
        }

        return liveData;
    }

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

    /**
     * Get Firestore Collection name associated with the DAO
     *
     * @return Collection name
     */
    protected abstract String getCollectionName();

    public MutableLiveData<Model> getModelByID(String docId) {
            GetModelByReferenceTask task = new GetModelByReferenceTask(docId);
        return task.run();
    }

    public MutableLiveData<Model> getModelByReference(DocumentReference documentReference) {
        GetModelByReferenceTask task = new GetModelByReferenceTask(documentReference);
        return task.run();
    }

    public MutableLiveData<Entity> getEntityByID(String docId) {
        GetEntityByReferenceTask task = new GetEntityByReferenceTask(docId);
        return task.run();
    }

    public MutableLiveData<Entity> getEntityByReference(DocumentReference documentReference) {
        GetEntityByReferenceTask task = new GetEntityByReferenceTask(documentReference);
        return task.run();
    }

    /**
     * @return  DAOBase initialized using new under its subclass
     */
    protected abstract DAOBase<Entity, Model> create();

    /**
     * It's in here because it follows the generic pattern
     *
     * @version 1.1.3.1
     */
    private class GetEntityByReferenceTask extends GetTaskSequencer<Entity> {
        private final String docId;
        private final DocumentReference documentReference;

        /**
         * Using a String of the document ID
         *
         * @param docId
         */
        GetEntityByReferenceTask(String docId) {
            this.docId = docId;
            this.documentReference = null;
        }

        /**
         * Using a DocumentReference
         *
         * @param documentReference
         */
        GetEntityByReferenceTask(DocumentReference documentReference) {
            this.docId = null;
            this.documentReference = documentReference;
        }

        @Override
        public void doFirstTask() {
            if (documentReference != null) {
                getDocumentSnapshot(documentReference);
            } else if (docId != null) {
                findDocumentReference();
            } else {
                postResult(null);
            }
        }

        private void findDocumentReference() {
            DocumentReference documentReference = db.collection(create().getCollectionName()).document(docId);
            getDocumentSnapshot(documentReference);
        }

        private void getDocumentSnapshot(DocumentReference documentReference) {
            documentReference.get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            convertToEntity(task.getResult());
                        } else {
                            postResult(null);
                        }
                    });
        }

        private void convertToEntity(DocumentSnapshot snapshot) {
            if (snapshot != null) {
                Entity entity = createObjectFromSnapshot(snapshot);

                if (entity != null) {
                    postResult(entity);
                } else {
                    postResult(null);
                }
            } else {
                postResult(null);
            }
        }
    }

    /**
     * It's in here because it follows the generic pattern
     *
     * @version 1.1.3.1
     */
    private class GetModelByReferenceTask extends GetTaskSequencer<Model> {
        private String docId;
        private DocumentReference documentReference;
        private MutableLiveData<List<EntityBase>> otherEntitiesLiveData;
        private Entity entity;

        /**
         * Using a String of the document ID
         *
         * @param docId
         */
        GetModelByReferenceTask(String docId) {
            this.docId = docId;
            this.documentReference = null;
            otherEntitiesLiveData = new MutableLiveData<>();
        }

        /**
         * Using a DocumentReference
         *
         * @param documentReference
         */
        GetModelByReferenceTask(DocumentReference documentReference) {
            this.docId = null;
            this.documentReference = documentReference;
            otherEntitiesLiveData = new MutableLiveData<>();
        }

        @Override
        public void doFirstTask() {
            MutableLiveData<Entity> liveData;
            if (documentReference != null) {
                liveData = getEntityByReference(documentReference);
            } else if (docId != null) {
                liveData = getEntityByID(docId);
            } else {
                postResult(null);
                return;
            }

            liveData.observe(lifecycleOwner, entityResult -> {
                GetModelByReferenceTask.this.entity = entityResult;
                convertToModel();
            });
        }

        private void convertToModel() {
            getOtherEntities(otherEntitiesLiveData, entity);
            createModelFromEntity(entity)
                .observe(lifecycleOwner, this::postResult);
        }
    }

    /**
     * Follows the pattern:
     * <pre>return new Model(entity);</pre>
     *
     * Some Models require several entities so some methods would expect:
     * <pre>return new Model(entity, otherEntityList.get(0))</pre>
     *
     * @param   entity             Entity
     * @return  Model
     *
     * @version 1.1.3.1
     */
    protected abstract MutableLiveData<Model> createModelFromEntity(Entity entity);

    /**
     * Follows that pattern:
     * <pre>snapshot.toObject(Entity.class)</pre>
     *
     * @param   snapshot
     * @return  Entity
     *
     * @version 1.1.3.1
     */
    protected abstract Entity createObjectFromSnapshot(DocumentSnapshot snapshot);

    /**
     * If model does not require additional entities,.
     * <pre>liveData.setValue(new ArrayList<>()</>);</pre>
     *
     * @param liveData
     * @param mainEntity
     *
     * @version 1.1.3.1
     */
    protected void getOtherEntities(MutableLiveData<List<EntityBase>> liveData, Entity mainEntity) {
        liveData.setValue(new ArrayList<>());
    }
}