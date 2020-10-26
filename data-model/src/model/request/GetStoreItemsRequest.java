package model.request;

public class GetStoreItemsRequest {

    private String zoneName;
    private Integer storeId;

    public GetStoreItemsRequest (String zoneName, Integer storeId) {
        this.zoneName = zoneName;
        this.storeId = storeId;
    }

    public String getZoneName () {
        return zoneName;
    }

    public Integer getStoreId () {
        return storeId;
    }
}
