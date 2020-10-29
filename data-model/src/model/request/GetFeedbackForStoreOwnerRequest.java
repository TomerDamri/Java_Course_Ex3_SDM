package model.request;

import java.util.UUID;

public class GetFeedbackForStoreOwnerRequest {

    private String zoneName;
    private UUID storeOwnerID;

    public GetFeedbackForStoreOwnerRequest (String zoneName, UUID storeOwnerID) {
        this.zoneName = zoneName;
        this.storeOwnerID = storeOwnerID;
    }

    public UUID getStoreOwnerID () {
        return storeOwnerID;
    }

    public String getZoneName () {
        return zoneName;
    }
}
