package model.request;

import java.util.UUID;

public class GetUserNotificationsRequest extends AccountRequest {

    public GetUserNotificationsRequest (UUID userId) {
        super(userId);
    }

    public UUID getUserId () {
        return userId;
    }
}
