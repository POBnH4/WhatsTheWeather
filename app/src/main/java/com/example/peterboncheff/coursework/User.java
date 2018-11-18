package com.example.peterboncheff.coursework;

import android.preference.PreferenceActivity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String username, password, email;
    private List<Location> preferredLocations;
    public static final short MAX_STORED_LOCATIONS = 10;


    public User(@NonNull String username,@NonNull String password, String email){
        this.username = username;
        this.password = password;
        this.email = email;
        this.preferredLocations = new ArrayList<>(MAX_STORED_LOCATIONS);
    }

    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public String getEmail() {
        return email;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public List<Location> getPreferredLocations() {
        return preferredLocations;
    }
}
