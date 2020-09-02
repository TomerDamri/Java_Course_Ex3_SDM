package model.request;

import java.time.LocalDateTime;
import java.util.Map;

public class PlaceOrderRequest {
    private final int storeId;
    private final LocalDateTime orderDate;
    private final int xCoordinate;
    private final int yCoordinate;
    private final Map<Integer, Double> orderItemToAmount;

    public PlaceOrderRequest (int storeId,
                              LocalDateTime orderDate,
                              int xCoordinate,
                              int yCoordinate,
                              Map<Integer, Double> orderItemToAmount) {
        this.storeId = storeId;
        this.orderDate = orderDate;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.orderItemToAmount = orderItemToAmount;
    }

    public int getStoreId () {
        return storeId;
    }

    public LocalDateTime getOrderDate () {
        return orderDate;
    }

    public int getxCoordinate () {
        return xCoordinate;
    }

    public int getyCoordinate () {
        return yCoordinate;
    }

    public Map<Integer, Double> getOrderItemToAmount () {
        return orderItemToAmount;
    }
}
