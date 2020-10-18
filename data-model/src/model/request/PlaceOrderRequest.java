package model.request;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

public class PlaceOrderRequest {
    private UUID customerId;
    private int storeId;
    private final int xCoordinate;
    private final int yCoordinate;
    private LocalDate orderDate;

    public PlaceOrderRequest(UUID customerId, LocalDate orderDate, int xCoordinate, int yCoordinate) {
        this.customerId = customerId;
        this.orderDate = orderDate;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public void setOrderItemToAmount(Map<Integer, Double> orderItemToAmount) {
        this.orderItemToAmount = orderItemToAmount;
    }

    private Map<Integer, Double> orderItemToAmount;

    public int getStoreId() {
        return storeId;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public Map<Integer, Double> getOrderItemToAmount() {
        return orderItemToAmount;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public int getxCoordinate() {
        return xCoordinate;
    }

    public int getyCoordinate() {
        return yCoordinate;
    }
}
