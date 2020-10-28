package model;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

public class SubOrderDTO {

    private UUID id;
    private LocalDate orderDate;
    private LocationDTO location;
    private Integer numOfItemTypes;
    private Integer amountOfItems;
    private Double itemsPrice;
    private Double deliveryPrice;
    private Double totalPrice;
    private String storeName;
    private Integer storeId;
    private String zoneName;

    public SubOrderDTO(String zoneName,
                       UUID id,
                       LocalDate orderDate,
                       LocationDTO locationDTO,
                       Map<Integer, Double> pricedItems,
                       Integer numOfItemTypes,
                       Integer amountOfItems,
                       Double itemsPrice,
                       Double deliveryPrice,
                       Double totalPrice,
                       String storeName,
                       Integer storeId) {
        this.zoneName = zoneName;
        this.id = id;
        this.orderDate = orderDate;
        this.location = locationDTO;
        this.numOfItemTypes = numOfItemTypes;
        this.amountOfItems = amountOfItems;
        this.itemsPrice = itemsPrice;
        this.deliveryPrice = deliveryPrice;
        this.totalPrice = totalPrice;
        this.storeName = storeName;
        this.storeId = storeId;
    }

    public UUID getId () {
        return id;
    }

    public LocalDate getOrderDate () {
        return orderDate;
    }

    public LocationDTO getLocation () {
        return location;
    }

    public Integer getNumOfItemTypes () {
        return numOfItemTypes;
    }

    public Integer getAmountOfItems () {
        return amountOfItems;
    }

    public Double getItemsPrice () {
        return itemsPrice;
    }

    public Double getDeliveryPrice () {
        return deliveryPrice;
    }

    public Double getTotalPrice () {
        return totalPrice;
    }

    public String getStoreName () {
        return storeName;
    }

    public Integer getStoreId () {
        return storeId;
    }

    public String getZoneName() {
        return zoneName;
    }

    @Override
    public String toString () {
        return new StringBuilder().append("Order id: ")
                                  .append(id)
                                  .append(",\nDate: ")
                                  .append(orderDate)
                                  .append(",\nNumber of item types: ")
                                  .append(numOfItemTypes)
                                  .append(",\nTotal number of items: ")
                                  .append(amountOfItems)
                                  .append(",\nTotal items cost: ")
                                  .append(itemsPrice)
                                  .append(",\nDelivery price: ")
                                  .append(deliveryPrice)
                                  .append(",\nTotal price of the order: ")
                                  .append(totalPrice)
                                  .append(",\nStore id: ")
                                  .append(storeId)
                                  .append(",\nStore Name: ")
                                  .append(storeName)
                                  .toString();
    }

}
