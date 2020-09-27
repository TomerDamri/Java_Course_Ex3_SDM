package course.java.sdm.engine.model;

public class SystemCustomer implements Mappable {

    private final Customer customer;
    private int numOfOrders = 0;
    private double totalItemsPrice = 0;
    private double totalDeliveryPrice = 0;

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

    public double getTotalItemsPrice () {
        return totalItemsPrice;
    }

    public double getTotalDeliveryPrice () {
        return totalDeliveryPrice;
    }

    public void setNumOfOrders (int numOfOrders) {
        this.numOfOrders = numOfOrders;
    }

    public void setTotalItemsPrice (double totalItemsPrice) {
        this.totalItemsPrice = totalItemsPrice;
    }

    public void setTotalDeliveryPrice (double totalDeliveryPrice) {
        this.totalDeliveryPrice = totalDeliveryPrice;
    }
}
