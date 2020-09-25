package model.request;

import java.time.LocalDate;
import java.util.Map;

public class PlaceDynamicOrderRequest {
    private int customerId;
    private LocalDate orderDate;
    private int xCoordinate;
    private int yCoordinate;

    public PlaceDynamicOrderRequest(int customerId, LocalDate orderDate) {
        this.customerId = customerId;
        this.orderDate = orderDate;
    }

    public void setOrderItemToAmount(Map<Integer, Double> orderItemToAmount) {
        this.orderItemToAmount = orderItemToAmount;
    }

    private Map<Integer, Double> orderItemToAmount;

    public Map<Integer, Double> getOrderItemToAmount() {
        return orderItemToAmount;
    }

    public int getxCoordinate() {
        return xCoordinate;
    }

    public int getyCoordinate() {
        return yCoordinate;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public int getCustomerId() {
        return customerId;
    }
}
