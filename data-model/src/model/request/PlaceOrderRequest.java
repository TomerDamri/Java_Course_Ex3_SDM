package model.request;

import java.time.LocalDateTime;
import java.util.Map;

public class PlaceOrderRequest {
    private final int customerId;
    private final int storeId;
    private final LocalDateTime orderDate;
    private final Map<Integer, Double> orderItemToAmount;

    public PlaceOrderRequest (int customerId,
                              int storeId,
                              LocalDateTime orderDate,
                              Map<Integer, Double> orderItemToAmount) {
        this.customerId = customerId;
        this.storeId = storeId;
        this.orderDate = orderDate;
        this.orderItemToAmount = orderItemToAmount;
    }

    public int getStoreId () {
        return storeId;
    }

    public LocalDateTime getOrderDate () {
        return orderDate;
    }

    public Map<Integer, Double> getOrderItemToAmount () {
        return orderItemToAmount;
    }

    public int getCustomerId () {
        return customerId;
    }

}
