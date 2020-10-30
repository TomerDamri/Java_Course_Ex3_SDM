package model.response;

import java.util.UUID;

import model.User;

public class LoginResponse {
    private String nextPageURL;
    private UUID userId;
    private String username;
    private User.UserType userType;

    public LoginResponse (String nextPageURL, UUID userId, String username, User.UserType userType) {
        this.nextPageURL = nextPageURL;
        this.userId = userId;
        this.username = username;
        this.userType = userType;
    }
}
