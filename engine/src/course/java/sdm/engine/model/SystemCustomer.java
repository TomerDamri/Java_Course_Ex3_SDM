package course.java.sdm.engine.model;

public class SystemCustomer implements Mappable {

    private final Customer customer;
    private int numOfOrders = 0;
    private double avgItemsPrice = 0;
    private double avgDeliveryPrice = 0;

    public SystemCustomer (Customer customer) {
        this.customer = customer;
    }

    public int getId () {
        return customer.getId();
    }

    public String getName () {
        return customer.getName();
    }

    public Location getLocation () {
        return customer.getLocation();
    }

    public Customer getCustomer () {
        return customer;
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

    public void setNumOfOrders (int numOfOrders) {
        this.numOfOrders = numOfOrders;
    }

    public void setAvgItemsPrice (double avgItemsPrice) {
        this.avgItemsPrice = avgItemsPrice;
    }

    public void setAvgDeliveryPrice (double avgDeliveryPrice) {
        this.avgDeliveryPrice = avgDeliveryPrice;
    }
}
