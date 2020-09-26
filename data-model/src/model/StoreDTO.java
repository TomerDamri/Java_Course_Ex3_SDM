package model;

import java.util.List;
import java.util.Map;

public class StoreDTO {
    private final int id;
    private final String name;
    private final int deliveryPpk;
    private final LocationDTO location;
    private final Map<Integer, StoreItemDTO> items;
    private final List<StoreOrderDTO> orders;
    private final double totalDeliveriesPayment;
    private final List<DiscountDTO> storeDiscounts;

    public StoreDTO (int id,
                     String name,
                     int deliveryPpk,
                     LocationDTO locationDTO,
                     Map<Integer, StoreItemDTO> items,
                     List<StoreOrderDTO> orders,
                     double totalDeliveriesPayment,
                     List<DiscountDTO> storeDiscounts) {
        this.id = id;
        this.name = name;
        this.deliveryPpk = deliveryPpk;
        this.location = locationDTO;
        this.items = items;
        this.orders = orders;
        this.totalDeliveriesPayment = totalDeliveriesPayment;
        this.storeDiscounts = storeDiscounts;
    }

    public List<DiscountDTO> getStoreDiscounts () {
        return storeDiscounts;
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

    public LocationDTO getLocation () {
        return location;
    }

    public Map<Integer, StoreItemDTO> getItems () {
        return items;
    }

    public List<StoreOrderDTO> getOrders () {
        return orders;
    }

    public double getTotalDeliveriesPayment () {
        return totalDeliveriesPayment;
    }

    // @Override
    // public String toString() {
    // StringBuilder builder = new StringBuilder("Store id: ").append(id)
    // .append(",\nName: ")
    // .append(name)
    // .append(",\nPPK: ")
    // .append(deliveryPpk)
    // .append(",\nStore Items:\n");
    // if (!items.isEmpty()) {
    // builder.append("[");
    // Iterator<StoreItemDTO> iterator = items.values().iterator();
    // while (iterator.hasNext()) {
    // StoreItemDTO item = iterator.next();
    // builder.append("{").append(item.toString()).append("}");
    // if (iterator.hasNext()) {
    // builder.append(",\n");
    // }
    // }
    // builder.append("]");
    //
    // builder.append("\nOrders: [");
    // Iterator<StoreOrderDTO> storeOrderIterator = orders.iterator();
    // while (storeOrderIterator.hasNext()) {
    // StoreOrderDTO storeOrder = storeOrderIterator.next();
    // builder.append("{").append(storeOrder.toString()).append("}");
    // if (storeOrderIterator.hasNext()) {
    // builder.append(",\n");
    // }
    // }
    // builder.append("]");
    // } else {
    // builder.append("There are no items");
    // }
    // builder.append("\nTotal deliveries payment: ").append(totalDeliveriesPayment);
    // return builder.toString();
    // }

    @Override
    public String toString () {
        return String.format("id: %s name: %s location: %s", id, name, location.toString());
    }
}
