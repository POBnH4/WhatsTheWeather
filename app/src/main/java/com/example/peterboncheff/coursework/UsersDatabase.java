package com.example.peterboncheff.coursework;

import java.util.ArrayList;
import java.util.List;

public class UsersDatabase {
    public static List<User> users = new ArrayList<>();

    private static UsersDatabase soleInstance;

    private UsersDatabase(){
    }

    public static UsersDatabase getInstance(){
        if(soleInstance != null) soleInstance = new UsersDatabase();
        return soleInstance;
    }

    private void populate(){

    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
