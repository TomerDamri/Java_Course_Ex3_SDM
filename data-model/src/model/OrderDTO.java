package model;

import java.util.List;
import java.util.UUID;

public class OrderDTO {

    private UUID orderID;
    private List<SubOrderDTO> subOrders;

    public OrderDTO (UUID orderID, List<SubOrderDTO> subOrders) {
        this.orderID = orderID;
        this.subOrders = subOrders;
    }

    public UUID getOrderID () {
        return orderID;
    }

    public List<SubOrderDTO> getSubOrders () {
        return subOrders;
    }
}
