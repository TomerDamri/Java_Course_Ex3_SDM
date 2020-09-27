package model.request;

import java.time.LocalDate;
import java.util.Map;

public class PlaceOrderRequest {
    private int customerId;
    private int storeId;
    private LocalDate orderDate;

    public PlaceOrderRequest (int customerId, LocalDate orderDate) {
        this.customerId = customerId;
        this.orderDate = orderDate;
    }

    public void setCustomerId (int customerId) {
        this.customerId = customerId;
    }

    public void setStoreId (int storeId) {
        this.storeId = storeId;
    }

    public void setOrderDate (LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public void setOrderItemToAmount (Map<Integer, Double> orderItemToAmount) {
        this.orderItemToAmount = orderItemToAmount;
    }

    private Map<Integer, Double> orderItemToAmount;

    public int getStoreId () {
        return storeId;
    }

    public LocalDate getOrderDate () {
        return orderDate;
    }

    public Map<Integer, Double> getOrderItemToAmount () {
        return orderItemToAmount;
    }

    public int getCustomerId () {
        return customerId;
    }

}
