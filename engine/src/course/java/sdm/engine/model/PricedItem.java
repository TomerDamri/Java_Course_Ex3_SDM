package course.java.sdm.engine.model;

import java.io.Serializable;

public class PricedItem implements Serializable {
    private final Item item;
    private int price;

    public Item getItem () {
        return item;
    }

    public PricedItem (Item item, int price) {
        this.item = item;
        this.price = price;
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

    public int getPrice () {
        return price;
    }

    public void setPrice (int price) {
        this.price = price;
    }

    @Override
    public String toString () {
        return item.toString() + ",\nPrice in store: " + price;
    }
}