package model.request;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

public class PlaceOrderRequest {

    private String zoneName;
    private UUID customerId;
    private int storeId;
    private final int xCoordinate;
    private final int yCoordinate;
    private LocalDate orderDate;
    private Map<Integer, Double> orderItemToAmount;

    public PlaceOrderRequest (String zoneName,
                              int storeId,
                              UUID customerId,
                              LocalDate orderDate,
                              int xCoordinate,
                              int yCoordinate,
                              Map<Integer, Double> orderItemToAmount) {
        this.zoneName = zoneName;
        this.storeId = storeId;
        this.customerId = customerId;
        this.orderDate = orderDate;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.orderItemToAmount = orderItemToAmount;
    }

    public void setCustomerId (UUID customerId) {
        this.customerId = customerId;
    }

    public void setStoreId (int storeId) {
        this.storeId = storeId;
    }

    public void setOrderDate (LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public void setOrderItemToAmount (Map<Integer, Double> orderItemToAmount) {
        this.orderItemToAmount = orderItemToAmount;
    }

    public int getStoreId () {
        return storeId;
    }

    public LocalDate getOrderDate () {
        return orderDate;
    }

    public Map<Integer, Double> getOrderItemToAmount () {
        return orderItemToAmount;
    }

    public UUID getCustomerId () {
        return customerId;
    }

    public String getZoneName () {
        return zoneName;
    }

    public int getxCoordinate () {
        return xCoordinate;
    }

    public int getyCoordinate () {
        return yCoordinate;
    }
}
