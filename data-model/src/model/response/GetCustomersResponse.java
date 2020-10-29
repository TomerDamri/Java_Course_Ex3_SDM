package model.response;

import java.util.List;

import model.CustomerDTO;

public class GetCustomersResponse {

    List<CustomerDTO> systemCustomers;

    public GetCustomersResponse (List<CustomerDTO> systemCustomers) {
        this.systemCustomers = systemCustomers;
    }

    public List<CustomerDTO> getSystemCustomers () {
        return systemCustomers;
    }
}