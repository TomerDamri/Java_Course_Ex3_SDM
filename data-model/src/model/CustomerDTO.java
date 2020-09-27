package model;

public class CustomerDTO {

    private Integer id;
    private String name;
    private final LocationDTO location;
    private Integer numOfOrders;
    private Double avgItemsPrice;
    private Double avgDeliveryPrice;

    public CustomerDTO (int id, String name, LocationDTO location, int numOfOrders, double avgItemsPrice, double avgDeliveryPrice) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.numOfOrders = numOfOrders;
        this.avgItemsPrice = avgItemsPrice;
        this.avgDeliveryPrice = avgDeliveryPrice;
    }

    public Integer getId () {
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

    public LocationDTO getLocation () {
        return location;
    }

    @Override
    public String toString () {
        return String.format("id: %s name: %s", id, name);
    }
}