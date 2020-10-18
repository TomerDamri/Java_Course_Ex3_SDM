package course.java.sdm.engine.model;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

public class TempOrder implements Serializable {

    private final UUID orderId;
    private final UUID customerId;
    private Map<StoreDetails, Order> staticOrders;

    public TempOrder (UUID orderId, Map<StoreDetails, Order> staticOrders, UUID customerId) {
        this.orderId = orderId;
        this.staticOrders = staticOrders;
        this.customerId = customerId;
    }

    public TempOrder (UUID orderId, Map<StoreDetails, Order> staticOrders) {
        this.orderId = orderId;
        this.staticOrders = staticOrders;
        this.customerId = null;
    }

    public UUID getOrderId () {
        return orderId;
    }

    public Map<StoreDetails, Order> getStaticOrders () {
        return staticOrders;
    }

    public UUID getCustomerId () {
        return customerId;
    }
}