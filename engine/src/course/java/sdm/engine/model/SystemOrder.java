package course.java.sdm.engine.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

public class SystemOrder implements Serializable {

    private final Order order;
    private final String storeName;
    private final Integer storeId;

    public SystemOrder (Order order, String storeName, Integer storeId) {
        this.order = order;
        this.storeName = storeName;
        this.storeId = storeId;
    }

    public Order getOrder () {
        return order;
    }

    public UUID getId () {
        return order.getId();
    }

    public LocalDate getOrderDate () {
        return order.getOrderDate();
    }

    public Location getOrderLocation () {
        return order.getOrderLocation();
    }

    public Map<PricedItem, Double> getOrderItems () {
        return order.getPricedItems();
    }

    public Integer getNumOfItemTypes () {
        return order.getNumOfItemTypes();
    }

    public Integer getAmountOfItems () {
        return order.getAmountOfItems();
    }

    public Double getItemsPrice () {
        return order.getItemsPrice();
    }

    public Double getDeliveryPrice () {
        return order.getDeliveryPrice();
    }

    public Double getTotalPrice () {
        return order.getTotalPrice();
    }

    public String getStoreName () {
        return storeName;
    }

    public Integer getStoreId () {
        return storeId;
    }

    @Override
    public String toString () {
        return order.toString() + ",\nStore id: " + storeId + ",\nStore Name: " + storeName;
    }
}
