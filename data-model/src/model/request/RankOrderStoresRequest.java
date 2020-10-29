package model.request;

import java.util.List;
import java.util.UUID;

public class RankOrderStoresRequest {

    private String zoneName;
    private UUID orderId;
    private UUID customerId;
    private List<StoreRank> storeRanks;

    public RankOrderStoresRequest (String zoneName, UUID orderId, UUID customerId, List<StoreRank> storeRanks) {
        this.zoneName = zoneName;
        this.orderId = orderId;
        this.customerId = customerId;
        this.storeRanks = storeRanks;
    }

    public String getZoneName () {
        return zoneName;
    }

    public UUID getOrderId () {
        return orderId;
    }

    public UUID getCustomerId () {
        return customerId;
    }

    public List<StoreRank> getOrderStoreRanks () {
        return storeRanks;
    }
}
