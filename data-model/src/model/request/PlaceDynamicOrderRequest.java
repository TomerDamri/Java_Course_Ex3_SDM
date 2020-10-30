package model.request;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

public class PlaceDynamicOrderRequest {

    private String zoneName;
    private UUID customerId;
    private LocalDate orderDate;
    private int xCoordinate;
    private int yCoordinate;
    private Map<Integer, Double> orderItemToAmount;

    public PlaceDynamicOrderRequest (String zoneName,
                                     UUID customerId,
                                     LocalDate orderDate,
                                     int xCoordinate,
                                     int yCoordinate,
                                     Map<Integer, Double> orderItemToAmount) {
        this.zoneName = zoneName;
        this.customerId = customerId;
        this.orderDate = orderDate;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.orderItemToAmount = orderItemToAmount;
    }

    public void setOrderItemToAmount (Map<Integer, Double> orderItemToAmount) {
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

    public LocalDate getOrderDate () {
        return orderDate;
    }

    public UUID getCustomerId () {
        return customerId;
    }

    public String getZoneName () {
        return zoneName;
    }
}
