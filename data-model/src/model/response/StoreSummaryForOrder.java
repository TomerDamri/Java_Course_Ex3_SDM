package model.response;

import java.util.Iterator;
import java.util.List;

public class StoreSummaryForOrder {

    private final int id;
    private final String name;
    private final int deliveryPpk;
    private final Double distanceBetweenStoreAndCustomer;
    private final Double totalDeliveryPrice;
    private final List<ItemSummaryForOrder> storePurchasedItems;

    public StoreSummaryForOrder (int id,
                                 String name,
                                 int deliveryPpk,
                                 Double distanceBetweenStoreAndCustomer,
                                 Double totalDeliveryPrice,
                                 List<ItemSummaryForOrder> storePurchasedItems) {
        this.id = id;
        this.name = name;
        this.deliveryPpk = deliveryPpk;
        this.distanceBetweenStoreAndCustomer = distanceBetweenStoreAndCustomer;
        this.totalDeliveryPrice = totalDeliveryPrice;
        this.storePurchasedItems = storePurchasedItems;
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

    public Double getDistanceBetweenStoreAndCustomer () {
        return distanceBetweenStoreAndCustomer;
    }

    public Double getTotalDeliveryPrice () {
        return totalDeliveryPrice;
    }

    public List<ItemSummaryForOrder> getStorePurchasedItems () {
        return storePurchasedItems;
    }

    @Override
    public String toString () {
        StringBuilder builder = new StringBuilder().append("Store details:\n\n")
                                                   .append("id= ")
                                                   .append(id)
                                                   .append(",\nname= '")
                                                   .append(name)
                                                   .append('\'')
                                                   .append(",\nppk=")
                                                   .append(deliveryPpk)
                                                   .append(",\ndistance Between Store And Customer= ")
                                                   .append(distanceBetweenStoreAndCustomer)
                                                   .append(",\ntotal Delivery Price= ")
                                                   .append(totalDeliveryPrice)
                                                   .append(",\nstore Purchased Items:\n");

        Iterator<ItemSummaryForOrder> iterator = storePurchasedItems.iterator();
        while (iterator.hasNext()) {
            ItemSummaryForOrder itemSummaryForOrder = iterator.next();
            builder.append("\n{" + itemSummaryForOrder + "}");
            if (iterator.hasNext()) {
                builder.append(",\n");
            }
        }
        builder.append("\n");

        return builder.toString();
    }
}
