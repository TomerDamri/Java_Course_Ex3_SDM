package course.java.sdm.engine.model;

import java.util.Map;
import java.util.UUID;

public class DynamicOrder extends TempOrder {

    private boolean isConfirmed;

    public DynamicOrder (UUID orderId, Map<StoreDetails, Order> staticOrders, Integer customerId) {
        super(orderId, staticOrders, customerId);
    }

    public DynamicOrder (UUID orderId, Map<StoreDetails, Order> staticOrders) {
        super(orderId, staticOrders);
    }

    public boolean isConfirmed () {
        return isConfirmed;
    }

    public void setConfirmed (boolean confirmed) {
        isConfirmed = confirmed;
    }
}
