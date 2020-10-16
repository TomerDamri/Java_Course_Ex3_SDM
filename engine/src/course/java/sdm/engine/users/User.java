package course.java.sdm.engine.users;

public class User {
    private int id;
    private String name;

    public enum UserType {
        CUSTOMER, STORE_OWNER;
    }

    private UserType userType;

    public User(int id, String name, UserType userType) {
        this.id = id;
        this.name = name;
        this.userType = userType;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public UserType getUserType() {
        return userType;
    }
}
