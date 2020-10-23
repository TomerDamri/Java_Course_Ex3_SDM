package model;

import java.util.UUID;

public class User {
    private UUID id;
    private String name;
    private UserType userType;

    public User (UUID id, String name, UserType userType) {
        this.id = id;
        this.name = name;
        this.userType = userType;
    }

    public UUID getId () {
        return id;
    }

    public String getName () {
        return name;
    }

    public UserType getUserType () {
        return userType;
    }

    public enum UserType {
        CUSTOMER, STORE_OWNER;
    }
}
