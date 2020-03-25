package com.cmput301w20t10.uberapp;

import com.cmput301w20t10.uberapp.models.User;

public final class Application {

    private static final Application INSTANCE = new Application();

    private User user;

    private Application() {
        this.user = null;
    }

    public static Application getInstance() {
        return INSTANCE;
    }

    public User getCurrentUser() throws RuntimeException {
        if (user == null) {
            throw new RuntimeException("No current user has been set");
        }
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
