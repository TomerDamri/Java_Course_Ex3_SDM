package model.request;

import java.util.UUID;

public abstract class AccountRequest {

    protected UUID userId;

    protected AccountRequest (UUID userId) {
        this.userId = userId;
    }

    protected UUID getUserId () {
        return userId;
    }
}
