package model.response;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import model.OrderDTO;

public class GetCustomerOrdersResponse {

    private final Map<UUID, List<OrderDTO>> orders;

    public GetCustomerOrdersResponse(Map<UUID, List<OrderDTO>> orders) {
        this.orders = orders;
    }

    public Map<UUID, List<OrderDTO>> getOrders () {
        return orders;
    }
}
