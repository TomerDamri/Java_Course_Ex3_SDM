package course.java.sdm.engine.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Order implements Serializable {

    private final UUID parentId;
    private final UUID id;
    private final LocalDate orderDate;
    private final Location orderLocation;
    private Map<PricedItem, Double> pricedItems;
    private Integer numOfItemTypes;
    private Integer amountOfItems;
    private Double itemsPrice;
    private Double deliveryPrice;
    private Double distanceFromCustomerLocation;
    private Double totalPrice;
    private Map<Offer, Integer> selectedOfferToNumOfRealization;

    public Order(LocalDate orderDate, Location orderLocation, UUID parentId) {
        this.id = UUID.randomUUID();
        this.orderDate = orderDate;
        this.orderLocation = orderLocation;
        this.parentId = parentId;
        this.pricedItems = new HashMap<>();
        this.selectedOfferToNumOfRealization = new HashMap<>();
    }

    public UUID getParentId() {
        return parentId;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Double getDeliveryPrice() {
        return deliveryPrice;
    }

    public void setDeliveryPrice(Double deliveryPrice) {
        this.deliveryPrice = deliveryPrice;
    }

    public Double getItemsPrice() {
        return itemsPrice;
    }

    public void setItemsPrice(Double itemsPrice) {
        this.itemsPrice = itemsPrice;
    }

    public UUID getId() {
        return id;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public Location getOrderLocation() {
        return orderLocation;
    }

    public Map<PricedItem, Double> getPricedItems() {
        return pricedItems;
    }

    public Double getDistanceFromCustomerLocation() {
        return distanceFromCustomerLocation;
    }

    public void setDistanceFromCustomerLocation(Double distanceFromCustomerLocation) {
        this.distanceFromCustomerLocation = distanceFromCustomerLocation;
    }

    public Integer getNumOfItemTypes() {
        return numOfItemTypes;
    }

    public void setNumOfItemTypes(Integer numOfItemTypes) {
        this.numOfItemTypes = numOfItemTypes;
    }

    public Integer getAmountOfItems() {
        return amountOfItems;
    }

    public void setAmountOfItems(Integer amountOfItems) {
        this.amountOfItems = amountOfItems;
    }

    public Map<Offer, Integer> getSelectedOfferToNumOfRealization() {
        return selectedOfferToNumOfRealization;
    }

    @Override
    public String toString() {
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
                .toString();

    }
}
