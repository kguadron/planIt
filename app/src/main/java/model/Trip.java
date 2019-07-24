package model;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Trip {
    private String name;
    private String description;
    private ArrayList<String> users = new ArrayList<String>();
    private String userOwnerId;
    private String userOwnerName;
    private Timestamp timeAdded;
    private String tripId;

    public Trip() { } //empty constructor for Firestore

    public Trip(String name, String description, ArrayList<String> users,
                String userOwnerId, String userOwnerName, Timestamp timeAdded,
                String tripId) {
        this.name = name;
        this.description = description;
        this.users = users;
        this.userOwnerId = userOwnerId;
        this.userOwnerName = userOwnerName;
        this.timeAdded = timeAdded;
        this.tripId = tripId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<String> users) {
        this.users = users;
    }


    public String getUserOwnerId() {
        return userOwnerId;
    }

    public void setUserOwnerId(String userId) {
        this.userOwnerId = userId;
    }

    public String getUserOwnerName() {
        return userOwnerName;
    }

    public void setUserOwnerName(String userName) {
        this.userOwnerName = userName;
    }

    public Timestamp getTimeAdded() {
        return timeAdded;
    }

    public void setTimeAdded(Timestamp timeAdded) {
        this.timeAdded = timeAdded;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }
}
