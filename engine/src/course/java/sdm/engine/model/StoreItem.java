package course.java.sdm.engine.model;

import java.io.Serializable;

public class StoreItem implements Serializable {
    private PricedItem pricedItem;
    private double purchasesCount;

    public StoreItem (Item item, Integer price) {
        this.pricedItem = new PricedItem(item, price);
        this.purchasesCount = 0;
    }

    public int getId () {
        return pricedItem.getId();
    }

    public String getName () {
        return pricedItem.getName();
    }

    public Item.PurchaseCategory getPurchaseCategory () {
        return pricedItem.getPurchaseCategory();
    }

    public int getPrice () {
        return pricedItem.getPrice();
    }

    public void setPrice (int price) {
        pricedItem.setPrice(price);
    }

    public Double getPurchasesCount () {
        return purchasesCount;
    }

    public void setPurchasesCount (Double purchasesCount) {
        this.purchasesCount = purchasesCount;
    }

    public PricedItem getPricedItem () {
        return pricedItem;
    }

    public void setPricedItem (PricedItem pricedItem) {
        this.pricedItem = pricedItem;
    }

    @Override
    public String toString () {
        return pricedItem.toString() + ",\nNumber of purchases in store: " + purchasesCount;
    }
}
