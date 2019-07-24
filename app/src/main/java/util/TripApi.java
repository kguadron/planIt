package util;

import android.app.Application;

public class TripApi extends Application {

    private String username;
    private String userId;
    private static TripApi instance;

    public static  TripApi getInstance() {
        if (instance == null)
            instance = new TripApi();
        return instance;
    }

    public TripApi(){}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
