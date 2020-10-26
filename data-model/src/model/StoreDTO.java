package model;

import java.util.List;

public class StoreDTO {
    private final int id;
    private final String name;
    private final String storeOwnerName;
    private final int deliveryPpk;
    private final LocationDTO location;
    private final List<StoreItemDTO> items;
    private final List<StoreOrderDTO> orders;
    private final double totalDeliveriesPayment;
    private final double totalItemsPrice;
    private final List<DiscountDTO> storeDiscounts;

    public StoreDTO (String storeOwnerName,
                     int id,
                     String name,
                     int deliveryPpk,
                     LocationDTO locationDTO,
                     List<StoreItemDTO> items,
                     List<StoreOrderDTO> orders,
                     double totalDeliveriesPayment,
                     List<DiscountDTO> storeDiscounts,
                     double totalItemsPrice) {
        this.storeOwnerName = storeOwnerName;
        this.id = id;
        this.name = name;
        this.deliveryPpk = deliveryPpk;
        this.location = locationDTO;
        this.items = items;
        this.orders = orders;
        this.totalDeliveriesPayment = totalDeliveriesPayment;
        this.storeDiscounts = storeDiscounts;
        this.totalItemsPrice = totalItemsPrice;
    }

    public List<DiscountDTO> getStoreDiscounts () {
        return storeDiscounts;
    }

    public int getId () {
        return id;
    }

    public String getName () {
        return name;
    }

    public int getDeliveryPpk () {
        return deliveryPpk;
    }

    public LocationDTO getLocation () {
        return location;
    }

    public List<StoreItemDTO> getItems () {
        return items;
    }

    public List<StoreOrderDTO> getOrders () {
        return orders;
    }

    public double getTotalDeliveriesPayment () {
        return totalDeliveriesPayment;
    }

    public String getStoreOwnerName () {
        return storeOwnerName;
    }

    public double getTotalItemsPrice () {
        return totalItemsPrice;
    }

    @Override
    public String toString () {
        return String.format("id: %s name: %s location: %s", id, name, location.toString());
    }
}
