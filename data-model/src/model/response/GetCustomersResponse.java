package model.response;

import java.util.Map;
import java.util.UUID;

import model.CustomerDTO;

public class GetCustomersResponse {

    Map<UUID, CustomerDTO> systemCustomers;

    public GetCustomersResponse (Map<UUID, CustomerDTO> systemCustomers) {
        this.systemCustomers = systemCustomers;
    }

    public Map<UUID, CustomerDTO> getSystemCustomers () {
        return systemCustomers;
    }
}