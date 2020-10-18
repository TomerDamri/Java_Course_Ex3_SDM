package course.java.sdm.engine.model;

import java.util.UUID;

public class SystemCustomer implements Mappable {

    private final Customer customer;
    private int numOfOrders = 0;
    private double totalItemsPrice = 0;
    private double totalDeliveryPrice = 0;

    public SystemCustomer (UUID id, String name) {
        this.customer = new Customer(id, name);
    }

    public SystemCustomer (Customer customer) {
        this.customer = customer;
    }

    public UUID getId () {
        return customer.getId();
    }

    public String getName () {
        return customer.getName();
    }

    public Customer getCustomer () {
        return customer;
    }

    public int getNumOfOrders () {
        return numOfOrders;
    }

    public void setNumOfOrders (int numOfOrders) {
        this.numOfOrders = numOfOrders;
    }

    public double getTotalItemsPrice () {
        return totalItemsPrice;
    }

    public void setTotalItemsPrice (double totalItemsPrice) {
        this.totalItemsPrice = totalItemsPrice;
    }

    public double getTotalDeliveryPrice () {
        return totalDeliveryPrice;
    }

    public void setTotalDeliveryPrice (double totalDeliveryPrice) {
        this.totalDeliveryPrice = totalDeliveryPrice;
    }
}
