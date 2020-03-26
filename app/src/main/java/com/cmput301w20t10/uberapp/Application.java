package com.cmput301w20t10.uberapp;

import com.cmput301w20t10.uberapp.models.User;

public final class Application {

    private static final Application INSTANCE = new Application();

    private User user;
    private String messagingToken;

    private Application() {
        this.user = null;
    }

    public static Application getInstance() {
        return INSTANCE;
    }

    /**
     * Retrieves the current user data
     *
     * @return The current user, if no user has been set, null is returned
     */
    public User getCurrentUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMessagingToken() {
        return messagingToken;
    }

    public void setMessagingToken(String messagingToken) {
        this.messagingToken = messagingToken;
    }
}
