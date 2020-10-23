package model.request;

public class BaseUpdateStoreRequest {

    private String zoneName;
    protected final Integer storeId;
    protected final Integer itemId;

    public BaseUpdateStoreRequest (String zoneName, Integer storeId, Integer itemId) {
        this.storeId = storeId;
        this.itemId = itemId;
        this.zoneName = zoneName;
    }

    public Integer getStoreId () {
        return storeId;
    }

    public Integer getItemId () {
        return itemId;
    }

    public String getZoneName () {
        return zoneName;
    }
}