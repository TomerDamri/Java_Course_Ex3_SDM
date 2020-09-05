package model;

public class CustomerDTO {

    private int id;
    private String name;
    private final int xCoordinate;
    private final int yCoordinate;
    private int numOfOrders;
    private double avgItemsPrice;
    private double avgDeliveryPrice;

    public CustomerDTO (int id,
                        String name,
                        int xCoordinate,
                        int yCoordinate,
                        int numOfOrders,
                        double avgItemsPrice,
                        double avgDeliveryPrice) {
        this.id = id;
        this.name = name;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.numOfOrders = numOfOrders;
        this.avgItemsPrice = avgItemsPrice;
        this.avgDeliveryPrice = avgDeliveryPrice;
    }

    public int getId () {
        return id;
    }

    public String getName () {
        return name;
    }

    public int getxCoordinate () {
        return xCoordinate;
    }

    public int getyCoordinate () {
        return yCoordinate;
    }

    public int getNumOfOrders () {
        return numOfOrders;
    }

    public double getAvgItemsPrice () {
        return avgItemsPrice;
    }

    public double getAvgDeliveryPrice () {
        return avgDeliveryPrice;
    }
}