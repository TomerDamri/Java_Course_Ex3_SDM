package model.request;

import java.util.UUID;

public class GetCustomerOrdersRequest {

    private UUID customerId;

    public GetCustomerOrdersRequest (UUID customerId) {
        this.customerId = customerId;
    }

    public UUID getCustomerId () {
        return customerId;
    }
}
