package model;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class StoreDTO {
    private final int id;
    private final String name;
    private final int deliveryPpk;
    private final int xCoordinate;
    private final int yCoordinate;
    private final Map<Integer, StoreItemDTO> items;
    private final List<StoreOrderDTO> orders;
    private final double totalDeliveriesPayment;

    public StoreDTO (int id,
                     String name,
                     int deliveryPpk,
                     int xCoordinate,
                     int yCoordinate,
                     Map<Integer, StoreItemDTO> items,
                     List<StoreOrderDTO> orders,
                     double totalDeliveriesPayment) {
        this.id = id;
        this.name = name;
        this.deliveryPpk = deliveryPpk;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.items = items;
        this.orders = orders;
        this.totalDeliveriesPayment = totalDeliveriesPayment;
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

    public int get_X_Coordinate () {
        return xCoordinate;
    }

    public int get_Y_Coordinate () {
        return yCoordinate;
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

    @Override
    public String toString () {
        StringBuilder builder = new StringBuilder("Store id: ").append(id)
                                                               .append(",\nName: ")
                                                               .append(name)
                                                               .append(",\nPPK: ")
                                                               .append(deliveryPpk)
                                                               .append(",\nStore Items:\n");
        if (!items.isEmpty()) {
            builder.append("[");
            Iterator<StoreItemDTO> iterator = items.values().iterator();
            while (iterator.hasNext()) {
                StoreItemDTO item = iterator.next();
                builder.append("{").append(item.toString()).append("}");
                if (iterator.hasNext()) {
                    builder.append(",\n");
                }
            }
            builder.append("]");

            builder.append("\nOrders: [");
            Iterator<StoreOrderDTO> storeOrderIterator = orders.iterator();
            while (storeOrderIterator.hasNext()) {
                StoreOrderDTO storeOrder = storeOrderIterator.next();
                builder.append("{").append(storeOrder.toString()).append("}");
                if (storeOrderIterator.hasNext()) {
                    builder.append(",\n");
                }
            }
            builder.append("]");
        }
        else {
            builder.append("There are no items");
        }
        builder.append("\nTotal deliveries payment: ").append(totalDeliveriesPayment);
        return builder.toString();
    }
}
