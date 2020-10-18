package model;

import java.util.UUID;

public class CustomerDTO {

    private UUID id;
    private String name;
    private Integer numOfOrders;
    private Double avgItemsPrice;
    private Double avgDeliveryPrice;

    public CustomerDTO (UUID id, String name, int numOfOrders, double avgItemsPrice, double avgDeliveryPrice) {
        this.id = id;
        this.name = name;
        this.numOfOrders = numOfOrders;
        this.avgItemsPrice = avgItemsPrice;
        this.avgDeliveryPrice = avgDeliveryPrice;
    }

    public UUID getId () {
        return id;
    }

    public String getName () {
        return name;
    }

    public Integer getNumOfOrders () {
        return numOfOrders;
    }

    public Double getAvgItemsPrice () {
        return avgItemsPrice;
    }

    public Double getAvgDeliveryPrice () {
        return avgDeliveryPrice;
    }

    @Override
    public String toString () {
        return String.format("id: %s name: %s", id, name);
    }
}