package course.java.sdm.engine.model;

import java.io.Serializable;
import java.util.*;

import course.java.sdm.engine.model.notifications.publisher.NotificationsPublisher;

public class SystemStore extends NotificationsPublisher implements Serializable, Mappable {

    private UUID storeOwnerId;
    private String storeOwnerName;
    private Store store;
    private List<Order> orders;
    private List<CustomerFeedback> customersFeedback;
    private double totalDeliveriesPayment = 0;

    public SystemStore (Store store, String storeOwnerName, UUID storeOwnerId) {
        this.store = store;
        this.storeOwnerName = storeOwnerName;
        this.storeOwnerId = storeOwnerId;
        this.orders = new ArrayList<>();
        this.customersFeedback = new ArrayList<>();
    }

    public List<CustomerFeedback> getCustomersFeedback () {
        return customersFeedback;
    }

    public UUID getStoreOwnerId () {
        return storeOwnerId;
    }

    public double getTotalDeliveriesPayment () {
        return totalDeliveriesPayment;
    }

    public void setTotalDeliveriesPayment (double totalDeliveriesPayment) {
        this.totalDeliveriesPayment = totalDeliveriesPayment;
    }

    public Store getStore () {
        return store;
    }

    public int getId () {
        return store.getId();
    }

    public String getName () {
        return store.getName();
    }

    public int getDeliveryPpk () {
        return store.getDeliveryPpk();
    }

    public Location getLocation () {
        return store.getLocation();
    }

    public String getStoreOwnerName () {
        return storeOwnerName;
    }

    public Map<Integer, StoreItem> getItemIdToStoreItem () {
        return store.getItemIdToStoreItem();
    }

    public List<Order> getOrders () {
        if (orders == null) {
            orders = new ArrayList<>();
        }
        return orders;
    }

    public Map<Integer, List<Discount>> getStoreDiscounts () {
        return store.getStoreDiscounts();
    }

    @Override
    public String toString () {
        StringBuilder builder = new StringBuilder(store.toString()).append("\nStore Orders:\n");
        if (!orders.isEmpty()) {
            builder.append("[");
            Iterator<Order> iterator = orders.iterator();
            while (iterator.hasNext()) {
                Order order = iterator.next();
                builder.append("{").append(order.toString()).append("}");
                if (iterator.hasNext()) {
                    builder.append(",\n");
                }
            }
            builder.append("]");
        }
        else {
            builder.append("There are no orders");
        }
        builder.append("\nTotal deliveries payment: ").append(totalDeliveriesPayment);
        return builder.toString();
    }
}