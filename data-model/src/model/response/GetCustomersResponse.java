package model.response;

import java.util.Map;

import model.CustomerDTO;

public class GetCustomersResponse {

    Map<Integer, CustomerDTO> systemCustomers;

    public GetCustomersResponse (Map<Integer, CustomerDTO> systemCustomers) {
        this.systemCustomers = systemCustomers;
    }

    public Map<Integer, CustomerDTO> getSystemCustomers () {
        return systemCustomers;
    }
}