package com.example.learnloop.models;

import com.google.gson.annotations.SerializedName;

/**
 * Model for user registration on the backend.
 */
public class UserProfileModel {

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    public UserProfileModel() {}

    public UserProfileModel(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
