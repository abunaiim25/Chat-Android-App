package com.example.chatsapp.Models;

import java.util.ArrayList;

public class UserStatus {
    private  String name, profileImage;
    private long lastUpdate;
    private ArrayList<Status> statuses;


    public UserStatus(String name, String profileImage, long lastUpdate, ArrayList<Status> statuses) {
        this.name = name;
        this.profileImage = profileImage;
        this.lastUpdate = lastUpdate;
        this.statuses = statuses;
    }

    public UserStatus() {
    }




    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public ArrayList<Status> getStatuses() {
        return statuses;
    }

    public void setStatuses(ArrayList<Status> statuses) {
        this.statuses = statuses;
    }
}
