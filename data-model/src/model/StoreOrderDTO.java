package model;

import java.time.LocalDate;
import java.util.UUID;

public class StoreOrderDTO {
    private UUID parentId;
    private UUID id;
    private LocalDate orderDate;
    private LocationDTO location;
    private Integer numOfItemTypes;
    private Integer amountOfItems;
    private Double itemsPrice;
    private Double deliveryPrice;
    private Double totalPrice;

    public StoreOrderDTO (UUID parentId,
                          UUID id,
                          LocalDate orderDate,
                          LocationDTO locationDTO,
                          Integer numOfItemTypes,
                          Integer amountOfItems,
                          Double itemsPrice,
                          Double deliveryPrice,
                          Double totalPrice) {
        this.parentId = parentId;
        this.id = id;
        this.orderDate = orderDate;
        this.location = locationDTO;
        this.numOfItemTypes = numOfItemTypes;
        this.amountOfItems = amountOfItems;
        this.itemsPrice = itemsPrice;
        this.deliveryPrice = deliveryPrice;
        this.totalPrice = totalPrice;
    }

    public UUID getId () {
        return id;
    }

    public LocalDate getOrderDate () {
        return orderDate;
    }

    public UUID getParentId () {
        return parentId;
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

    @Override
    public String toString () {
        String parentOrderIdStr = parentId != null ? String.format("\nParent order id: %s,", parentId) : "";
        return new StringBuilder().append(parentOrderIdStr)
                                  .append("\nOrder id: ")
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
                                  .toString();
    }
}
