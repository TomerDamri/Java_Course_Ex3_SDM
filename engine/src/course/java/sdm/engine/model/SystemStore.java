package course.java.sdm.engine.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SystemStore implements Serializable, Mappable {

    private String storeOwnerName;
    private Store store;
    private List<Order> orders;
    private double totalDeliveriesPayment = 0;

    public SystemStore (Store store, String storeOwnerName) {
        this.store = store;
        this.storeOwnerName = storeOwnerName;
        this.orders = new ArrayList<>();
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