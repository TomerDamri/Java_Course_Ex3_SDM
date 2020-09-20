package model.request;

import java.time.LocalDateTime;
import java.util.Map;

public class PlaceDynamicOrderRequest {
    private final int customerId;
    private final LocalDateTime orderDate;
    private final int xCoordinate;
    private final int yCoordinate;
    private final Map<Integer, Double> orderItemToAmount;

    public PlaceDynamicOrderRequest (int customerId,
                                     LocalDateTime orderDate,
                                     int xCoordinate,
                                     int yCoordinate,
                                     Map<Integer, Double> orderItemToAmount) {
        this.customerId = customerId;
        this.orderDate = orderDate;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.orderItemToAmount = orderItemToAmount;
    }

    public Map<Integer, Double> getOrderItemToAmount () {
        return orderItemToAmount;
    }

    public int getxCoordinate () {
        return xCoordinate;
    }

    public int getyCoordinate () {
        return yCoordinate;
    }

    public LocalDateTime getOrderDate () {
        return orderDate;
    }

    public int getCustomerId () {
        return customerId;
    }
}
