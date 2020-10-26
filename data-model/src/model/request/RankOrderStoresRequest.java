package model.request;

import java.util.List;
import java.util.UUID;

public class RankOrderStoresRequest {

    private String zoneName;
    private UUID orderId;
    private UUID customerId;
    private List<OrderStoreRank> orderStoreRanks;

    public RankOrderStoresRequest (String zoneName, UUID orderId, UUID customerId, List<OrderStoreRank> orderStoreRanks) {
        this.zoneName = zoneName;
        this.orderId = orderId;
        this.customerId = customerId;
        this.orderStoreRanks = orderStoreRanks;
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

    public List<OrderStoreRank> getOrderStoreRanks () {
        return orderStoreRanks;
    }
}
