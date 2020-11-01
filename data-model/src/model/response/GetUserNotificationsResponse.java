package model.response;

import java.util.List;

public class GetUserNotificationsResponse {
    List<String> userNotifications;

    public GetUserNotificationsResponse (List<String> userNotifications) {
        this.userNotifications = userNotifications;
    }

    public List<String> getUserNotifications () {
        return userNotifications;
    }
}
