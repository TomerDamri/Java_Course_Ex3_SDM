package course.java.sdm.engine.model;

import java.util.UUID;

public class SystemUser {
    private UUID id;
    private String name;
    private UserType userType;

    public enum UserType {
        CUSTOMER, STORE_OWNER;
    }

    public SystemUser(UUID id, String name, UserType userType) {
        this.id = id;
        this.name = name;
        this.userType = userType;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public UserType getUserType() {
        return userType;
    }
}
