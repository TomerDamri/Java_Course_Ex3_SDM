package model;

public class DynamicOrderEntityDTO {

    private final Integer storeId;
    private final String storeName;
    private final int xCoordinate;
    private final int yCoordinate;
    private final Double distanceFromCustomer;
    private final Integer ppk;
    private final Integer totalItemTypes;
    private final Double deliveryPrice;
    private final Double totalItemsPrice;

    public DynamicOrderEntityDTO (Integer storeId,
                                  String storeName,
                                  int xCoordinate,
                                  int yCoordinate,
                                  Double distanceFromCustomer,
                                  Integer ppk,
                                  Integer totalItemTypes,
                                  Double deliveryPrice,
                                  Double totalPrice) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
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

    public int getX_Coordinate () {
        return xCoordinate;
    }

    public int getY_Coordinate () {
        return yCoordinate;
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

    @Override
    public String toString () {
        return new StringBuilder("Store id: ").append(storeId)
                                              .append(",\nStore name: ")
                                              .append(storeName)
                                              .append(",\nStore location: [")
                                              .append(xCoordinate)
                                              .append(",")
                                              .append(yCoordinate)
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