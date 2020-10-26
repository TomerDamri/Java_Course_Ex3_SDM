package model.request;

import java.util.UUID;

public class GetFeedbackForStoreOwnerRequest {

    private UUID storeOwnerID;

    public GetFeedbackForStoreOwnerRequest (UUID storeOwnerID) {
        this.storeOwnerID = storeOwnerID;
    }

    public UUID getStoreOwnerID () {
        return storeOwnerID;
    }
}
