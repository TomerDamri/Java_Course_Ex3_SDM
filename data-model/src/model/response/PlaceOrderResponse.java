package model.response;

import java.util.UUID;

public class PlaceOrderResponse {
    private final UUID orderId;

    public PlaceOrderResponse (UUID orderId) {
        this.orderId = orderId;
    }

    public UUID getOrderId () {
        return orderId;
    }
}
