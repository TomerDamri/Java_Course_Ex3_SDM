package course.java.sdm.engine.model;

import java.io.Serializable;

public class SystemItem implements Serializable {

    private Item item;
    private int storesCount;
    private double avgPrice;
    private double ordersCount;
    private double discountOrdersCount;
    private int storeSellsInCheapestPrice;

    public SystemItem (Item item) {
        this.item = item;
        this.storesCount = 0;
        this.avgPrice = 0;
        this.ordersCount = 0;
        this.discountOrdersCount = 0;
    }

    public double getDiscountOrdersCount () {
        return discountOrdersCount;
    }

    public void setDiscountOrdersCount (double discountOrdersCount) {
        this.discountOrdersCount = discountOrdersCount;
    }

    public int getStoresCount () {
        return storesCount;
    }

    public void setStoresCount (int storesCount) {
        this.storesCount = storesCount;
    }

    public Item getItem () {
        return item;
    }

    public int getId () {
        return item.getId();
    }

    public String getName () {
        return item.getName();
    }

    public Item.PurchaseCategory getPurchaseCategory () {
        return item.getPurchaseCategory();
    }

    public double getAvgPrice () {
        return avgPrice;
    }

    public void setAvgPrice (double avgPrice) {
        this.avgPrice = avgPrice;
    }

    public Double getOrdersCount () {
        return ordersCount;
    }

    public void setOrdersCount (double ordersCount) {
        this.ordersCount = ordersCount;
    }

    public Integer getStoreSellsInCheapestPrice () {
        return storeSellsInCheapestPrice;
    }

    public void setStoreSellsInCheapestPrice (int storeSellsInCheapestPrice) {
        this.storeSellsInCheapestPrice = storeSellsInCheapestPrice;
    }

    @Override
    public String toString () {
        StringBuilder builder = new StringBuilder(item.toString()).append(",\nNumber of stores supplied in: ")
                                                                  .append(storesCount)
                                                                  .append(",\nAverage price: ")
                                                                  .append(avgPrice)
                                                                  .append(",\nNumber of purchases: ")
                                                                  .append(ordersCount);
        return builder.toString();
    }
}