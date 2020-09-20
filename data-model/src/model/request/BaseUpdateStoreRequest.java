package model.request;

public class BaseUpdateStoreRequest {

    protected final Integer storeId;
    protected final Integer itemId;

    public BaseUpdateStoreRequest (Integer storeId, Integer itemId) {
        this.storeId = storeId;
        this.itemId = itemId;
    }

    public Integer getStoreId () {
        return storeId;
    }

    public Integer getItemId () {
        return itemId;
    }
}