package com.cmput301w20t10.uberapp.database;


import com.cmput301w20t10.uberapp.models.User;
import com.google.firebase.firestore.FirebaseFirestore;

public class Database {

    private static final Database INSTANCE = new Database();

    public static Database getInstance() { return INSTANCE; }

    private final FirebaseFirestore db;

    private Database() {
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Retrieves a user from the firebase using the email as the given key
     * @param email - The user identification used to look up user information in the firebase
     * @return - The user object containing the appropriate data of the user from the given
     *              identifier
     */
    public User getUserData(String email) {
        // Todo (Joshua): retrieve the user data from the firebase
        return null;
    }

}
