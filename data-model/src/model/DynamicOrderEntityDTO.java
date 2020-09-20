package model;

public class DynamicOrderEntityDTO {

    private final Integer storeId;
    private final String storeName;
    private final Double distanceFromCustomer;
    private final Integer ppk;
    private final Integer totalItemTypes;
    private final Double deliveryPrice;
    private final Double totalItemsPrice;
    private final LocationDTO location;

    public DynamicOrderEntityDTO (Integer storeId,
                                  String storeName,
                                  LocationDTO location,
                                  Double distanceFromCustomer,
                                  Integer ppk,
                                  Integer totalItemTypes,
                                  Double deliveryPrice,
                                  Double totalPrice) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.location = location;
        this.distanceFromCustomer = distanceFromCustomer;
        this.ppk = ppk;
        this.totalItemTypes = totalItemTypes;
        this.deliveryPrice = deliveryPrice;
        this.totalItemsPrice = totalPrice;
    }

    public final Integer getStoreId () {
        return storeId;
    }

    public final String getStoreName () {
        return storeName;
    }

    public final Double getDistanceFromCustomer () {
        return distanceFromCustomer;
    }

    public final Integer getPpk () {
        return ppk;
    }

    public final Integer getTotalItemTypes () {
        return totalItemTypes;
    }

    public final Double getDeliveryPrice () {
        return deliveryPrice;
    }

    public final Double getTotalItemsPrice () {
        return totalItemsPrice;
    }

    public LocationDTO getLocation () {
        return location;
    }

    @Override
    public String toString () {
        return new StringBuilder("Store id: ").append(storeId)
                                              .append(",\nStore name: ")
                                              .append(storeName)
                                              .append(",\nStore location: [")
                                              .append(location)
                                              .append("],\nDistance from store:")
                                              .append(distanceFromCustomer)
                                              .append(",\nDelivery PPK: ")
                                              .append(ppk)
                                              .append(",\nNumber of item types: ")
                                              .append(totalItemTypes)
                                              .append(",\nDelivery price: ")
                                              .append(deliveryPrice)
                                              .append(",\nItems price: ")
                                              .append(totalItemsPrice)
                                              .append(",\nOrder total price: ")
                                              .append(totalItemsPrice + deliveryPrice)
                                              .toString();

    }
}