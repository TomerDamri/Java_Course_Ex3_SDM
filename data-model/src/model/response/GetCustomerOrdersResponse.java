package model.response;

import java.util.List;

import model.OrderDTO;

public class GetCustomerOrdersResponse {

    private final List<OrderDTO> orders;

    public GetCustomerOrdersResponse (List<OrderDTO> orders) {
        this.orders = orders;
    }

    public List<OrderDTO> getOrders () {
        return orders;
    }
}
