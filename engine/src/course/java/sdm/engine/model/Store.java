package course.java.sdm.engine.model;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

public class Store implements Serializable {
    private StoreDetails storeDetails;
    private Map<Integer, StoreItem> itemIdToStoreItem;

    public Store (String name, int deliveryPpk, Location location, Map<Integer, StoreItem> itemIdToStoreItem, int id) {
        this.storeDetails = new StoreDetails(id, name, deliveryPpk, location);
        this.itemIdToStoreItem = itemIdToStoreItem;
    }

    public String getName () {
        return storeDetails.getName();
    }

    public int getDeliveryPpk () {
        return storeDetails.getDeliveryPpk();
    }

    public Location getLocation () {
        return storeDetails.getLocation();
    }

    public Map<Integer, StoreItem> getItemIdToStoreItem () {
        return itemIdToStoreItem;
    }

    public int getId () {
        return storeDetails.getId();
    }

    public final StoreDetails getStoreDetails () {
        return storeDetails;
    }

    @Override
    public String toString () {
        StringBuilder builder = new StringBuilder("Store id: ").append(getId())
                                                               .append(",\nName: ")
                                                               .append(getName())
                                                               .append(",\nPPK: ")
                                                               .append(getDeliveryPpk())
                                                               .append(",\nStore Items:\n");
        if (!itemIdToStoreItem.isEmpty()) {
            builder.append("[");
            Iterator<StoreItem> iterator = itemIdToStoreItem.values().iterator();
            while (iterator.hasNext()) {
                StoreItem item = iterator.next();
                builder.append("{").append(item.toString()).append("}");
                if (iterator.hasNext()) {
                    builder.append(",\n");
                }
            }
            builder.append("]");
        }
        else {
            builder.append("There are no items");
        }
        return builder.toString();
    }
}
